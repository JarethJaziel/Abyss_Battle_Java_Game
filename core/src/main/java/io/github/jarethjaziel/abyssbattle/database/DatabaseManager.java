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
import io.github.jarethjaziel.abyssbattle.util.SkinType;

import java.io.IOException;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:users.db";
    private static final boolean RESET_DB = false; // cambia a false después de probar (reiniciar db)

    private ConnectionSource connectionSource;

    private Dao<User, Integer> userDao;
    private Dao<Stats, Integer> statsDao;
    private Dao<Skin, Integer> skinDao;
    private Dao<UserSkin, Integer> userSkinDao;
    private Dao<UserLoadout, Integer> userLoadoutDao;

    public void connect() throws SQLException {
        connectionSource = new JdbcConnectionSource(DB_URL);

        // 🔥 RESET TEMPORAL DE BASE DE DATOS (para desarrollo)
        if (RESET_DB) {
            System.out.println("RESETEANDO BASE DE DATOS...");
            dropAllTables();
        }


        TableUtils.createTableIfNotExists(connectionSource, Stats.class);
        TableUtils.createTableIfNotExists(connectionSource, Skin.class);
        TableUtils.createTableIfNotExists(connectionSource, User.class);
        TableUtils.createTableIfNotExists(connectionSource, UserSkin.class);
        TableUtils.createTableIfNotExists(connectionSource, UserLoadout.class);

        userDao = DaoManager.createDao(connectionSource, User.class);
        statsDao = DaoManager.createDao(connectionSource, Stats.class);
        skinDao = DaoManager.createDao(connectionSource, Skin.class);
        userSkinDao = DaoManager.createDao(connectionSource, UserSkin.class);
        userLoadoutDao = DaoManager.createDao(connectionSource, UserLoadout.class);

        initializeDefaultSkins();

        System.out.println("✅ Base de datos conectada y lista.");
    }

    /**
     * ELIMINA TODAS LAS TABLAS (solo para desarrollo)
     */
    private void dropAllTables() throws SQLException {
        try {
            TableUtils.dropTable(connectionSource, UserLoadout.class, true);
            TableUtils.dropTable(connectionSource, UserSkin.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Skin.class, true);
            TableUtils.dropTable(connectionSource, Stats.class, true);
            System.out.println("Tablas eliminadas correctamente");
        } catch (SQLException e) {
            System.out.println(" Algunas tablas no existían, continuando...");
        }
    }

    /**
     * Inicializa las 2 skins básicas GRATIS (solo se ejecuta la primera vez)
     */
    public void initializeDefaultSkins() throws SQLException {
        long skinCount = skinDao.countOf();

        if (skinCount > 0) {
            System.out.println("✅ Skins ya inicializadas (" + skinCount + " skins)");
            return;
        }

        System.out.println(" Inicializando skins básicas...");

        // Solo 2 skins GRATIS por defecto
        Skin troopBasic = new Skin("Tropa Basica", 0, SkinType.TROOP);
        skinDao.create(troopBasic);

        Skin cannonBasic = new Skin("Cañon Basico", 0, SkinType.CANNON);
        skinDao.create(cannonBasic);

        System.out.println(" 2 skins básicas creadas (ID 1 y 2)");
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
