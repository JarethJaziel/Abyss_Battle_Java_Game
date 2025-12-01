package io.github.jarethjaziel.abyssbattle.database;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.Stats;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserLoadout;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseManager {

    // Nombre del archivo de la base de datos
    private static final String DB_URL = "jdbc:sqlite:users.db";
    
    private ConnectionSource connectionSource;

    private Dao<User, Integer> userDao;
    private Dao<Stats, Integer> statsDao;
    private Dao<Skin, Integer> skinDao;
    private Dao<UserSkin, Integer> userSkinDao;
    private Dao<UserLoadout, Integer> userLoadoutDao;

    public void connect() throws SQLException {
        connectionSource = new JdbcConnectionSource(DB_URL);
        
        TableUtils.createTableIfNotExists(connectionSource, Stats.class);
        TableUtils.createTableIfNotExists(connectionSource, Skin.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, UserSkin.class);

        userDao = DaoManager.createDao(connectionSource, User.class);
        statsDao = DaoManager.createDao(connectionSource, Stats.class);
        skinDao = DaoManager.createDao(connectionSource, Skin.class);
        userSkinDao = DaoManager.createDao(connectionSource, UserSkin.class);
        
        System.out.println("Base de datos conectada y lista.");
    }

    public void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
            }
        }
    }

    public Dao<UserLoadout, Integer> getUserLoadoutDao() {
        return userLoadoutDao;
    }

    public Dao<User, Integer> getUserDao() {
        return userDao;
    }

    public Dao<Stats, Integer> getStatsDao() {
        return statsDao;
    }

    public Dao<Skin, Integer> getSkinDao() {
        return skinDao;
    }

    public Dao<UserSkin, Integer> getUserSkinDao() {
        return userSkinDao;
    }

}