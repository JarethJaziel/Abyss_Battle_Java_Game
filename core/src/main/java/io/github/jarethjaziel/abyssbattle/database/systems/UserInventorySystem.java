package io.github.jarethjaziel.abyssbattle.database.systems;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserLoadout;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;

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
    public boolean doesUserOwnSkin(User user, int skinId) throws SQLException {
        QueryBuilder<UserSkin, Integer> qb = userSkinDao.queryBuilder();
        qb.where().eq("user_id", user.getId()).and().eq("skin_id", skinId);
        return qb.countOf() > 0;
    }

    /**
     * Método de conveniencia para el Registro (Usa los dos anteriores)
     */
    public void grantAndEquip(User user, Skin skin) throws SQLException {
        if (skin == null) return;
        
        grantSkin(user, skin); // 1. Dar
        equipSkin(user, skin); // 2. Equipar
    }

}
