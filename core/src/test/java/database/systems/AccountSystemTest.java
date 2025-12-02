package database.systems;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.dao.Dao;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserLoadout;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;
import io.github.jarethjaziel.abyssbattle.database.systems.AccountSystem;
import io.github.jarethjaziel.abyssbattle.util.Constants;

public class AccountSystemTest {

    private DatabaseManager dbManager;
    private Dao<User, Integer> userDao;
    private Dao<Skin, Integer> skinDao;
    private Dao<UserSkin, Integer> userSkinDao;
    private Dao<UserLoadout, Integer> loadoutDao;

    private AccountSystem accountSystem;

    @BeforeEach
    void setUp() throws SQLException {
        dbManager = mock(DatabaseManager.class);
        userDao = mock(Dao.class);
        skinDao = mock(Dao.class);
        userSkinDao = mock(Dao.class);
        loadoutDao = mock(Dao.class);

        when(dbManager.getUserDao()).thenReturn(userDao);
        when(dbManager.getSkinDao()).thenReturn(skinDao);
        when(dbManager.getUserSkinDao()).thenReturn(userSkinDao);
        when(dbManager.getUserLoadoutDao()).thenReturn(loadoutDao);

        accountSystem = new AccountSystem(dbManager);
    }

    @Test
    @DisplayName("Registrar usuario asigna las skins default")
    void testRegistrarNuevoUsuario() throws SQLException {
        Skin troopSkin = new Skin();
        Skin cannonSkin = new Skin();

        when(skinDao.queryForId(Constants.DEFAULT_TROOP_SKIN_ID)).thenReturn(troopSkin);
        when(skinDao.queryForId(Constants.DEFAULT_CANNON_SKIN_ID)).thenReturn(cannonSkin);

        accountSystem.registerUser("player", "1234");

        verify(userDao, times(1)).create(any(User.class));
        verify(userSkinDao, times(2)).create(any(UserSkin.class));
        verify(loadoutDao, times(2)).create(any(UserLoadout.class));
    }

    @Test
    @DisplayName("Lanza error si falta una skin default")
    void testSkinDefaultNoExiste() throws SQLException {
        when(skinDao.queryForId(Constants.DEFAULT_TROOP_SKIN_ID)).thenReturn(null);

        assertThrows(RuntimeException.class,
            () -> accountSystem.registerUser("player", "pass"));
    }

    @Test
    @DisplayName("asignarYEquiparSkin ejecuta DAOs correctamente usando reflexión")
    void testAsignarSkinReflexion() throws Exception {
        User user = new User();
        Skin skin = new Skin();

        when(skinDao.queryForId(5)).thenReturn(skin);

        // Obtener método privado sin usar var
        java.lang.reflect.Method method =
                AccountSystem.class.getDeclaredMethod("asignarYEquiparSkin", User.class, int.class);

        method.setAccessible(true);
        method.invoke(accountSystem, user, 5);

        verify(userSkinDao, times(1)).create(any(UserSkin.class));
        verify(loadoutDao, times(1)).create(any(UserLoadout.class));
    }
}
