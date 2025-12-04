package io.github.jarethjaziel.abyssbattle.database;

import com.badlogic.gdx.Gdx;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.Stats;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserLoadout;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;
import io.github.jarethjaziel.abyssbattle.util.GameSkins;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Gestor central de la conexión a la base de datos SQLite (usando ORMLite).
 * <p>
 * Se encarga de inicializar la conexión, crear las tablas si no existen,
 * poblar datos iniciales (como el catálogo de skins) y proveer acceso a los DAOs.
 */
public class DatabaseManager {
    private static final String TAG = DatabaseManager.class.getSimpleName();
    private static final String DB_URL = "jdbc:sqlite:users.db";
    /** Bandera de desarrollo para reiniciar la DB. DEBE SER FALSE EN PRODUCCIÓN. */
    private static final boolean RESET_DB = false;

    private ConnectionSource connectionSource;

    private Dao<User, Integer> userDao;
    private Dao<Stats, Integer> statsDao;
    private Dao<Skin, Integer> skinDao;
    private Dao<UserSkin, Integer> userSkinDao;
    private Dao<UserLoadout, Integer> userLoadoutDao;

    /**
     * Establece la conexión con la base de datos e inicializa la estructura.
     *
     * @throws SQLException Si ocurre un error crítico al conectar o crear tablas.
     */
    public void connect() throws SQLException {
        connectionSource = new JdbcConnectionSource(DB_URL);

        // RESET TEMPORAL DE BASE DE DATOS (para desarrollo)
        if (RESET_DB) {
            Gdx.app.log(TAG, "RESETEANDO BASE DE DATOS...");
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

        initializeSkins();

        Gdx.app.log(TAG, "Base de datos conectada y lista.");    
    }

    /**
     * Elimina todas las tablas para un reinicio limpio.
     * Útil durante el desarrollo o cambios de esquema.
     */
    private void dropAllTables() throws SQLException {
        try {
            TableUtils.dropTable(connectionSource, UserLoadout.class, true);
            TableUtils.dropTable(connectionSource, UserSkin.class, true);
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Skin.class, true);
            TableUtils.dropTable(connectionSource, Stats.class, true);
            Gdx.app.log(TAG, "Tablas eliminadas correctamente");
        } catch (SQLException e) {
            Gdx.app.error(TAG, "Algunas tablas no existían, continuando...", e);    
        }
    }

    /**
     * Verifica e inicializa el catálogo de skins en la base de datos.
     * Si una skin del Enum {@link GameSkins} no existe, la crea.
     */
    public void initializeSkins() throws SQLException {
        Gdx.app.log(TAG, "Verificando catálogo de skins...");
        for (GameSkins skin : GameSkins.values()) {
            createSkinIfNotExists(skin.getName(), skin.getPrice(), skin.getType());
        }
    }

    /**
     * Crea una skin en la base de datos solo si no existe previamente.
     */
    private void createSkinIfNotExists(String name, int price, SkinType type) throws SQLException {
        QueryBuilder<Skin, Integer> qb = skinDao.queryBuilder();
        qb.where().eq("name", name);

        if (qb.countOf() == 0) {
            skinDao.create(new Skin(name, price, type));
            Gdx.app.log(TAG, "Nueva skin agregada al catálogo: " + name);    
        }
    }
    /**
     * Cierra la conexión con la base de datos de forma segura.
     */
    public void close() {
        if (connectionSource != null) {
            try {
                connectionSource.close();
            } catch (IOException e) {
                Gdx.app.error(TAG, "Error al cerrar conexión IO", e);
            } catch (Exception e) {
                Gdx.app.error(TAG, "Error general al cerrar DB: " + e.getMessage(), e);
            }
        }
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
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
    /**
     * Busca una skin por su nombre único.
     * @param skinName El nombre de la skin a buscar.
     * @return El objeto {@link Skin} o {@code null} si no existe.
     */
    public Skin getSkinByName(String skinName) {
        try {
            List<Skin> results = skinDao.queryForEq("name", skinName);
            if (!results.isEmpty()) {
                return results.get(0); // Retorna el objeto Skin completo
            }
        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error buscando skin: " + skinName, e);
        }
        return null;
    }
}
