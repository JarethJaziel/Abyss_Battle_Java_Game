package io.github.jarethjaziel.abyssbattle.database.systems;

import com.j256.ormlite.dao.Dao;
import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Stats;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import java.sql.SQLException;
import java.util.Date;

public class PlayerStatsSystem {

    private DatabaseManager dbManager;
    private Dao<Stats, Integer> statsDao;
    private Dao<User, Integer> userDao;

    public PlayerStatsSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.statsDao = dbManager.getStatsDao(); // Asumo que creas este método en DBManager
        this.userDao = dbManager.getUserDao();
    }

    /**
     * Crea una fila de estadísticas en 0 y la vincula al usuario.
     * Se debe llamar al registrar un nuevo usuario.
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
     * Actualiza las estadísticas después de una partida
     */
    public void updateStatsAfterMatch(User user, Stats sessionStats) {
        try {
            Stats dbStats = user.getStats();

            if (dbStats == null) {
                // Manejo de error si no existen...
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
            System.out.println("Stats actualizadas correctamente.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Obtener stats para mostrar en el perfil
     */
    public Stats getStats(User user) {
        return user.getStats();
    }
}