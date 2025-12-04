package io.github.jarethjaziel.abyssbattle.database.systems;

import com.badlogic.gdx.Gdx;
import com.j256.ormlite.dao.Dao;
import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Stats;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import java.sql.SQLException;
import java.util.Date;

/**
 * Sistema responsable de la gestión y persistencia de las estadísticas de juego.
 * <p>
 * Se encarga de crear los registros iniciales de estadísticas para nuevos usuarios
 * y de actualizar los acumulados históricos (victorias, daño, precisión) al finalizar cada partida.
 */
public class PlayerStatsSystem {

    private static final String TAG = PlayerStatsSystem.class.getSimpleName();

    private DatabaseManager dbManager;
    private Dao<Stats, Integer> statsDao;
    private Dao<User, Integer> userDao;

    /**
     * Constructor del sistema.
     * @param dbManager Gestor de base de datos para obtener los DAOs.
     */
    public PlayerStatsSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.statsDao = dbManager.getStatsDao(); // Asumo que creas este método en DBManager
        this.userDao = dbManager.getUserDao();
    }

    /**
     * Inicializa una fila de estadísticas en cero y la vincula a un usuario nuevo.
     * <p>
     * Este método debe llamarse obligatoriamente durante el registro del usuario
     * para evitar errores de referencia nula (NullPointerException) posteriormente.
     *
     * @param user El usuario recién creado al que se le asignarán las stats.
     */
    public void createInitialStats(User user) throws SQLException {
        Stats newStats = new Stats();
        newStats.setPlayed(0);
        newStats.setWon(0);
        newStats.setLost(0);
        newStats.setHits(0);
        newStats.setMisses(0);
        newStats.setDamageTotal(0);
        newStats.setLastPlayed(new Date());

        statsDao.create(newStats);

        user.setStats(newStats);
        userDao.update(user);
    }

    /**
     * Actualiza las estadísticas históricas sumando los resultados de una partida reciente.
     *
     * @param user         El usuario que jugó la partida.
     * @param sessionStats Objeto Stats (DTO) que contiene SOLO los datos de la partida actual (no acumulados).
     */
    public void updateStatsAfterMatch(User user, Stats sessionStats) {
        try {
            Stats dbStats = user.getStats();

            if (dbStats == null) {
                Gdx.app.error(TAG, "Error Crítico: El usuario " + user.getUsername() + " no tiene stats vinculadas.");
                return;
            }

            dbStats.setPlayed(dbStats.getPlayed() + 1);

            dbStats.setWon(dbStats.getWon() + sessionStats.getWon());
            dbStats.setLost(dbStats.getLost() + sessionStats.getLost());
            dbStats.setDamageTotal(dbStats.getDamageTotal() + sessionStats.getDamageTotal());
            dbStats.setHits(dbStats.getHits() + sessionStats.getHits());
            dbStats.setMisses(dbStats.getMisses() + sessionStats.getMisses());

            dbStats.setLastPlayed(new Date());

            statsDao.update(dbStats);
            Gdx.app.log(TAG, "Estadísticas actualizadas correctamente tras la partida.");
        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error al actualizar estadísticas post-partida", e);
        }
    }

    /**
     * Obtiene el objeto de estadísticas asociado a un usuario.
     *
     * @param user El usuario a consultar.
     * @return Objeto {@link Stats} con el historial del jugador.
     */
    public Stats getStats(User user) {
        return user.getStats();
    }
}