package io.github.jarethjaziel.abyssbattle.database.systems;

import java.sql.SQLException;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.j256.ormlite.dao.Dao;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.PasswordUtils;

/**
 * Sistema encargado de la gestión de cuentas de usuario.
 * <p>
 * Maneja la autenticación (Login), el registro de nuevos usuarios y la
 * inicialización de sus datos por defecto (Inventario y Estadísticas).
 * Actúa como fachada para las operaciones relacionadas con la entidad {@link User}.
 */
public class AccountManagerSystem {

    private static final String TAG = AccountManagerSystem.class.getSimpleName();
    private DatabaseManager dbManager;
    private Dao<User, Integer> userDao;

    private UserInventorySystem inventorySystem;
    private PlayerStatsSystem statsSystem;

    /**
     * Constructor del sistema.
     *
     * @param dbManager Gestor de base de datos para obtener los DAOs y conexiones.
     */
    public AccountManagerSystem(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.userDao = dbManager.getUserDao();

        this.inventorySystem = new UserInventorySystem(dbManager);
        this.statsSystem = new PlayerStatsSystem(dbManager);
    }

    /**
     * Intenta iniciar sesión verificando las credenciales.
     *
     * @param username Nombre de usuario.
     * @param password Contraseña en texto plano (se verificará contra el hash).
     * @return El objeto {@link User} si las credenciales son correctas, o {@code null} si fallan.
     */
    public User login(String username, String password) {
        try {
            List<User> users = userDao.queryForEq("username", username);

            if (users.isEmpty()) {
                Gdx.app.log(TAG, "Login exitoso: " + username);
                return null;
            }

            User user = users.get(0);

            if (PasswordUtils.verifyPassword(password, user.getPasswordHash())) {
                Gdx.app.log(TAG, "Login exitoso: " + username);
                return user;
            } else {
                Gdx.app.log(TAG, "Intento de login fallido: Contraseña incorrecta para -> " + username);
                return null;
            }

        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error crítico al hacer login", e);
            return null;
        }
    }

    /**
     * Registra un nuevo usuario en el sistema y configura su cuenta inicial.
     * <p>
     * Este proceso incluye:
     * <ol>
     * <li>Crear el registro en la tabla 'users'.</li>
     * <li>Asignar skins por defecto en el inventario.</li>
     * <li>Inicializar las estadísticas en cero.</li>
     * </ol>
     *
     * @param username Nombre de usuario deseado.
     * @param password Contraseña en texto plano (será hasheada).
     * @return {@code true} si el registro y la configuración fueron exitosos.
     */
    public boolean registerUser(String username, String password) {
        try {
            if (!isUsernameAvailable(username)) {
                Gdx.app.log(TAG, "Registro fallido: El username ya existe -> " + username);
                return false;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPasswordHash(PasswordUtils.hashPassword(password));
            userDao.create(newUser);

            Gdx.app.log(TAG, "Usuario registrado en DB: " + username);

            try {
                Skin blueTroop = dbManager.getSkinByName(Constants.DEFAULT_TROOP_SKIN.getName());
                Skin redTroop = dbManager.getSkinByName(Constants.DEFAULT_ENEMY_TROOP_SKIN.getName());
                Skin cannon = dbManager.getSkinByName(Constants.DEFAULT_CANNON_SKIN.getName());

                if (blueTroop == null || redTroop == null || cannon == null) {
                    Gdx.app.error(TAG, "ERROR CRÍTICO: Faltan skins default en la DB. No se pudo completar el setup del usuario.");
                    return false;
                }

                inventorySystem.grantSkin(newUser, redTroop);
                inventorySystem.grantAndEquip(newUser, blueTroop);
                inventorySystem.grantAndEquip(newUser, cannon);

                statsSystem.createInitialStats(newUser);

                Gdx.app.log(TAG, "Setup de cuenta completado para: " + username);
                return true;

            } catch (Exception e) {
                Gdx.app.error(TAG, "Error durante la configuración inicial de la cuenta", e);
                return false;
            }

        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error SQL al registrar usuario", e);
            return false;
        }
    }

    /**
     * Actualiza los datos de un usuario existente en la base de datos.
     * Útil para guardar cambios en monedas o configuraciones.
     *
     * @param user El usuario con los datos modificados a persistir.
     */
    public void updateUser(User user) {
        try {
            userDao.update(user);
        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error actualizando usuario: " + user.getUsername(), e);
        }
    }

    /**
     * Verifica si un nombre de usuario está disponible para registro.
     *
     * @param username El nombre a verificar.
     * @return {@code true} si el nombre está libre, {@code false} si ya existe.
     */
    public boolean isUsernameAvailable(String username) {
        try {
            List<User> users = userDao.queryForEq("username", username);
            return users.isEmpty();
        } catch (SQLException e) {
            Gdx.app.error(TAG, "Error verificando disponibilidad de username", e);
            return false;
        }
    }
}
