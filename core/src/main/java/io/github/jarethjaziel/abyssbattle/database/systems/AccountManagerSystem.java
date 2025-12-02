package io.github.jarethjaziel.abyssbattle.database.systems;

import java.sql.SQLException;
import java.util.List;


import com.j256.ormlite.dao.Dao;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.PasswordUtils;

public class AccountManagerSystem {

    private DatabaseManager dbManager;
    private Dao<User, Integer> userDao;

    private UserInventorySystem inventorySystem;
    private PlayerStatsSystem statsSystem;

    public AccountManagerSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.userDao = dbManager.getUserDao();

        this.inventorySystem = new UserInventorySystem(dbManager);
        this.statsSystem = new PlayerStatsSystem(dbManager);
    }

    /**
     * Intenta iniciar sesión con username y contraseña
     * 
     * @return El usuario si las credenciales son correctas, null si no
     */
    public User login(String username, String password) {
        try {
            List<User> users = userDao.queryForEq("username", username);

            if (users.isEmpty()) {
                System.out.println(" Usuario no existe: " + username);
                return null;
            }

            User user = users.get(0);

            if (PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                System.out.println(" Login exitoso: " + username);
                return user;
            } else {
                System.out.println(" Contraseña incorrecta para: " + username);
                return null;
            }

        } catch (SQLException e) {
            System.err.println("Error al hacer login: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Registra un nuevo usuario en el sistema
     * 
     * @return true si se registró exitosamente, false si el username ya existe
     */
    public boolean registerUser(String username, String password) {
        try {
            if (!isUsernameAvailable(username)) {
                System.out.println(" El username ya existe: " + username);
                return false;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPasswordHash(PasswordUtils.hashPassword(password));
            userDao.create(newUser);

            System.out.println(" Usuario registrado: " + username);

            try {
                Skin blueTroop = dbManager.getSkinByName(Constants.DEFAULT_TROOP_SKIN.getName());
                Skin redTroop = dbManager.getSkinByName(Constants.DEFAULT_ENEMY_TROOP_SKIN.getName());
                Skin cannon = dbManager.getSkinByName(Constants.DEFAULT_CANNON_SKIN.getName());

                if (blueTroop == null || redTroop == null || cannon == null) {
                    System.err.println("ERROR CRÍTICO: Faltan skins default en la DB.");
                    return false;
                }

                inventorySystem.grantSkin(newUser, redTroop);
                inventorySystem.grantAndEquip(newUser, blueTroop);
                inventorySystem.grantAndEquip(newUser, cannon);

                statsSystem.createInitialStats(newUser);

                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifica si un username está disponible
     */
    public boolean isUsernameAvailable(String username) {
        try {
            List<User> users = userDao.queryForEq("username", username);
            return users.isEmpty();
        } catch (SQLException e) {
            System.err.println("Error al verificar username: " + e.getMessage());
            return false;
        }
    }
}
