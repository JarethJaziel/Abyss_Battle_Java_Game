package io.github.jarethjaziel.abyssbattle.database.systems;

import java.sql.SQLException;

import com.j256.ormlite.dao.Dao;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserLoadout;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;

public class ShopSystem {

    private final DatabaseManager dbManager;

    public ShopSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    public boolean purchaseSkin(User user, String type, String color) throws SQLException {

        String fileName = generateSkinFilename(type, color);
        Skin skin = findSkinByFilename(fileName);

        if (skin == null) {
            throw new RuntimeException("Skin not found in DB: " + fileName);
        }

        if (userOwnsSkin(user, skin)) {
            return false; 
        }

        Dao<UserSkin, Integer> userSkinDao = dbManager.getUserSkinDao();
        UserSkin ownership = new UserSkin(user, skin);
        userSkinDao.create(ownership);

        return true;
    }

    public void equipSkin(User user, String type, String color) throws SQLException {
        String fileName = generateSkinFilename(type, color);
        Skin skin = findSkinByFilename(fileName);

        if (skin == null)
            throw new RuntimeException("Skin not found: " + fileName);

        if (!userOwnsSkin(user, skin))
            throw new RuntimeException("User does not own this skin: " + fileName);

        Dao<UserLoadout, Integer> loadoutDao = dbManager.getUserLoadoutDao();

        UserLoadout existingLoadout = loadoutDao.queryBuilder()
            .where()
            .eq("user_id", user.getId())
            .and()
            .eq("skin_type", skin.getType())
            .queryForFirst();

        if (existingLoadout != null) {
            existingLoadout.setActiveSkin(skin);
            loadoutDao.update(existingLoadout);
        } else {
            UserLoadout newLoadout = new UserLoadout(user, skin);
            loadoutDao.create(newLoadout);
        }
    }

    private boolean userOwnsSkin(User user, Skin skin) throws SQLException {
        Dao<UserSkin, Integer> userSkinDao = dbManager.getUserSkinDao();

        UserSkin ownership = userSkinDao.queryBuilder()
                .where()
                .eq("user_id", user.getId())
                .and()
                .eq("skin_id", skin.getId())
                .queryForFirst();

        return ownership != null;
    }

    private Skin findSkinByFilename(String filename) throws SQLException {
        Dao<Skin, Integer> skinDao = dbManager.getSkinDao();

        return skinDao.queryBuilder()
                .where()
                .eq("file_name", filename)
                .queryForFirst();
    }

    private String generateSkinFilename(String type, String color) {

        type = type.toLowerCase();
        color = color.toLowerCase();

        switch (type) {

            case "cannon":
            case "cannons":
            case "cannon_barrel":
                return "cannon_barrel_" + color + ".png";

            case "troop":
            case "troops":
                return "troop_" + color + ".png";

            default:
                throw new RuntimeException("Invalid skin type: " + type);
        }
    }
}
