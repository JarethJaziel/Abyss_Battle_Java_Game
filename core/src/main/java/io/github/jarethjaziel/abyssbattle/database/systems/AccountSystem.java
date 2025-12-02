package io.github.jarethjaziel.abyssbattle.database.systems;

import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserLoadout;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.PasswordUtils;

public class AccountSystem {

    private DatabaseManager dbManager;

    public AccountSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }

    /**
     * Intenta iniciar sesión con username y contraseña
     * @return El usuario si las credenciales son correctas, null si no
     */
    public User login(String username, String password) {
        try {
            // Buscar usuario por username
            List<User> users = dbManager.getUserDao().queryForEq("username", username);

            if (users.isEmpty()) {
                System.out.println(" Usuario no existe: " + username);
                return null;
            }

            User user = users.get(0);

            // Verificar contraseña
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
     * @return true si se registró exitosamente, false si el username ya existe
     */
    public boolean registerUser(String username, String password) {
        try {
            // Verificar si el username ya existe
            List<User> existingUsers = dbManager.getUserDao().queryForEq("username", username);
            if (!existingUsers.isEmpty()) {
                System.out.println(" El username ya existe: " + username);
                return false;
            }

            // Crear nuevo usuario
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPasswordHash(PasswordUtils.hashPassword(password));
            dbManager.getUserDao().create(newUser);

            System.out.println(" Usuario registrado: " + username);

            // Asignar skins por defecto
            assignAndEquipSkin(newUser, Constants.DEFAULT_TROOP_SKIN_ID);
            assignAndEquipSkin(newUser, Constants.DEFAULT_CANNON_SKIN_ID);

            return true;

        } catch (SQLException e) {
            System.err.println("Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Asigna una skin al user y la equipa en su loadout
     */
    private void assignAndEquipSkin(User user, int skinId) throws SQLException {
        Dao<Skin, Integer> skinDao = dbManager.getSkinDao();
        Dao<UserSkin, Integer> userSkinDao = dbManager.getUserSkinDao();
        Dao<UserLoadout, Integer> loadoutDao = dbManager.getUserLoadoutDao();

        Skin skinBasica = skinDao.queryForId(skinId);
        if (skinBasica == null) {
            throw new RuntimeException("Error: Skin default ID " + skinId + " no existe en la DB.");
        }

        // Dar ownership de la skin
        UserSkin ownership = new UserSkin();
        ownership.setUser(user);
        ownership.setSkin(skinBasica);
        userSkinDao.create(ownership);

        // Equipar en loadout
        UserLoadout loadout = new UserLoadout(user, skinBasica);
        loadoutDao.create(loadout);
    }

    /**
     * Verifica si un username está disponible
     */
    public boolean isUsernameAvailable(String username) {
        try {
            List<User> users = dbManager.getUserDao().queryForEq("username", username);
            return users.isEmpty();
        } catch (SQLException e) {
            System.err.println("Error al verificar username: " + e.getMessage());
            return false;
        }
    }
}
