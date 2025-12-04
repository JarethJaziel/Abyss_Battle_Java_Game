package io.github.jarethjaziel.abyssbattle.database.systems;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserLoadout;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

/**
 * Sistema encargado de gestionar el inventario y equipamiento de los usuarios.
 * <p>
 * Maneja la propiedad de las skins (tabla {@code user_skins}) y la configuración
 * de qué skin está activa para cada tipo (tabla {@code user_loadouts}).
 */
public class UserInventorySystem {

    private static final String TAG = UserInventorySystem.class.getSimpleName();

    private DatabaseManager dbManager;
    private Dao<UserSkin, Integer> userSkinDao;
    private Dao<Skin, Integer> skinDao;
    private Dao<UserLoadout, Integer> loadoutDao;

    /**
     * Constructor del sistema de inventario.
     * @param dbManager Gestor de base de datos para obtener los DAOs.
     */
    public UserInventorySystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.userSkinDao = dbManager.getUserSkinDao();
        this.skinDao = dbManager.getSkinDao();
        this.loadoutDao = dbManager.getUserLoadoutDao();
    }

    /**
     * Otorga la propiedad de una skin a un usuario.
     * Útil para compras en la tienda o recompensas.
     *
     * @param user El usuario beneficiario.
     * @param skin La skin a agregar al inventario.
     * @return {@code true} si se agregó exitosamente, {@code false} si el usuario ya la tenía.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public boolean grantSkin(User user, Skin skin) throws SQLException {
        if (doesUserOwnSkin(user, skin.getId())) {
            Gdx.app.log(TAG, "Intento de duplicar skin rechazado. Usuario ya posee: " + skin.getName());
            return false;
        }

        UserSkin ownership = new UserSkin();
        ownership.setUser(user);
        ownership.setSkin(skin);
        userSkinDao.create(ownership);
        
        Gdx.app.log(TAG, "Skin agregada al inventario: " + skin.getName());
        return true;
    }

    /**
     * Equipa una skin en el slot correspondiente a su tipo (TROPA o CAÑON).
     * Si ya había una skin equipada de ese tipo, la reemplaza (Upsert).
     *
     * @param user El usuario que equipa el ítem.
     * @param skin La skin a equipar.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    public void equipSkin(User user, Skin skin) throws SQLException {
        Gdx.app.log(TAG, "Intentando equipar: " + skin);

        QueryBuilder<UserLoadout, Integer> loadoutQb = loadoutDao.queryBuilder();
        loadoutQb.where()
                .eq("user_id", user.getId())
                .and()
                .eq("skinType", skin.getType());

        UserLoadout existingLoadout = loadoutQb.queryForFirst();

        if (existingLoadout != null) {
            existingLoadout.setActiveSkin(skin);
            loadoutDao.update(existingLoadout);
        } else {
            UserLoadout newLoadout = new UserLoadout(user, skin);
            loadoutDao.create(newLoadout);
        }
        
        Gdx.app.log(TAG, "Skin equipada exitosamente: " + skin.getName());    
    }

    /**
     * Verifica si un usuario posee una skin específica.
     *
     * @param user   El usuario a verificar.
     * @param skinId El ID de la skin.
     * @return {@code true} si la tiene en su inventario.
     */
    public boolean doesUserOwnSkin(User user, int skinId){
        QueryBuilder<UserSkin, Integer> qb = userSkinDao.queryBuilder();
        try {
            qb.where()
                .eq("user_id", user.getId())
                .and()
                .eq("skin_id", skinId);
            return qb.countOf() > 0;
        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error verificando propiedad de skin ID: " + skinId, e);
            return false;
        }
        
    }

    /**
     * Método de utilidad para el registro inicial: Otorga la skin y la equipa inmediatamente.
     *
     * @param user El usuario nuevo.
     * @param skin La skin default.
     * @throws SQLException Si falla la transacción.
     */
    public void grantAndEquip(User user, Skin skin) throws SQLException {
        if (skin == null) {
            Gdx.app.error(TAG, "Intento de grantAndEquip con skin nula");
            return;
        }

        grantSkin(user, skin);
        equipSkin(user, skin);
    }

    /**
     * Obtiene la lista de skins que el usuario posee, filtradas por categoría.
     * Utiliza una subconsulta para eficiencia.
     *
     * @param user El usuario.
     * @param type El tipo de skin a buscar ({@link SkinType#TROOP} o {@link SkinType#CANNON}).
     * @return Lista de objetos {@link Skin} completos.
     */
    public List<Skin> getOwnedSkinsByType(User user, SkinType type) {
        try {
            // Hacemos un JOIN interno entre UserSkin y Skin
            QueryBuilder<UserSkin, Integer> ownershipQb = userSkinDao.queryBuilder(); 
            ownershipQb.selectColumns("skin_id");
            ownershipQb.where().eq("user_id", user.getId());

            QueryBuilder<Skin, Integer> skinQb = skinDao.queryBuilder();
            skinQb.where()
                    .eq("type", type)
                    .and()
                    .in("id", ownershipQb); 

            // Esto devuelve objetos Skin completos directamente, no a través de UserSkin
            return skinQb.query();

        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error obteniendo skins por tipo: " + type, e);
            return new ArrayList<>();
        }
    }

    /**
     * Recupera la skin que el usuario tiene equipada actualmente para un tipo dado.
     *
     * @param user El usuario.
     * @param type El tipo de slot a consultar.
     * @return La {@link Skin} equipada o {@code null} si no tiene nada.
     */
    public Skin getEquippedSkin(User user, SkinType type) {
        try {
            QueryBuilder<UserLoadout, Integer> qb = loadoutDao.queryBuilder();
            qb.where()
                    .eq("user_id", user.getId())
                    .and()
                    .eq("skinType", type);

            UserLoadout loadout = qb.queryForFirst();

            if (loadout != null) {
                return loadout.getActiveSkin();
            }
        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error obteniendo skin equipada para tipo: " + type, e);
        }
        return null;
    }

}
