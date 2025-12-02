package io.github.jarethjaziel.abyssbattle.database.systems;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserLoadout;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

public class UserInventorySystem {

    private DatabaseManager dbManager;
    private Dao<UserSkin, Integer> userSkinDao;
    private Dao<Skin, Integer> skinDao;
    private Dao<UserLoadout, Integer> loadoutDao;

    public UserInventorySystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.userSkinDao = dbManager.getUserSkinDao();
        this.skinDao = dbManager.getSkinDao();
        this.loadoutDao = dbManager.getUserLoadoutDao();
    }

    /**
     * SOLO OTORGA PROPIEDAD (Para la Tienda)
     * Retorna true si se agregó, false si ya la tenía.
     */
    public boolean grantSkin(User user, Skin skin) throws SQLException {
        if (doesUserOwnSkin(user, skin.getId())) {
            return false; // Ya la tiene
        }

        UserSkin ownership = new UserSkin();
        ownership.setUser(user);
        ownership.setSkin(skin);
        userSkinDao.create(ownership);
        System.out.println("Skin agregada al inventario: " + skin.getName());
        return true;
    }

    /**
     * SOLO EQUIPA (Para el selector de skins)
     */
    public void equipSkin(User user, Skin skin) throws SQLException {
        // Lógica de Upsert en Loadout
        System.out.println(skin);
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
        System.out.println("Skin equipada: " + skin.getName());
    }

    /**
     * Helper: Verifica propiedad
     */
    public boolean doesUserOwnSkin(User user, int skinId){
        QueryBuilder<UserSkin, Integer> qb = userSkinDao.queryBuilder();
        try {
            qb.where().eq("user_id", user.getId()).and().eq("skin_id", skinId);
            return qb.countOf() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        
    }

    /**
     * Método de conveniencia para el Registro (Usa los dos anteriores)
     */
    public void grantAndEquip(User user, Skin skin) throws SQLException {
        if (skin == null)
            return;

        grantSkin(user, skin); // 1. Dar
        equipSkin(user, skin); // 2. Equipar
    }

    /**
     * Devuelve la lista de skins que el usuario posee, filtradas por tipo.
     */
    public List<Skin> getOwnedSkinsByType(User user, SkinType type) {
        try {
            // Hacemos un JOIN interno entre UserSkin y Skin
            QueryBuilder<UserSkin, Integer> ownershipQb = userSkinDao.queryBuilder(); 
            ownershipQb.selectColumns("skin_id"); // Solo nos importa la columna ID
            ownershipQb.where().eq("user_id", user.getId());

            // 2. Consulta Principal: Obtener las Skins completas
            QueryBuilder<Skin, Integer> skinQb = skinDao.queryBuilder();
            skinQb.where()
                    .eq("type", type) // Que sean del tipo correcto (TROPA/CAÑON)
                    .and()
                    .in("id", ownershipQb); // Y que su ID esté en la lista que tiene el usuario

            // Esto devuelve objetos Skin completos directamente, no a través de UserSkin
            return skinQb.query();

        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Devuelve la skin que tiene equipada actualmente en ese slot/tipo.
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
            e.printStackTrace();
        }
        return null; // No tiene nada equipado (raro si inicializaste defaults)
    }

}
