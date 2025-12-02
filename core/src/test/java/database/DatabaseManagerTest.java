package database;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;

class DatabaseManagerTest {

    private DatabaseManager dbManager;

    @BeforeEach
    void setUp() {
        dbManager = new DatabaseManager();
    }

    @Test
    @DisplayName("Conectarse inicializa tablas y DAOs correctamente")
    void testConnectInicializaDaos() throws Exception {
        ConnectionSource fakeConnection = mock(JdbcConnectionSource.class);

        Field field = DatabaseManager.class.getDeclaredField("connectionSource");
        field.setAccessible(true);
        field.set(dbManager, fakeConnection);

        TableUtils tableUtilsMock = mock(TableUtils.class);

        dbManager.connect();

        assertNotNull(dbManager.getUserDao());
        assertNotNull(dbManager.getStatsDao());
        assertNotNull(dbManager.getSkinDao());
        assertNotNull(dbManager.getUserSkinDao());
    }

    @Test
    @DisplayName("Cerrar conexión no lanza excepción")
    void testClose() throws Exception {
        ConnectionSource fakeConnection = mock(ConnectionSource.class);

        Field field = DatabaseManager.class.getDeclaredField("connectionSource");
        field.setAccessible(true);
        field.set(dbManager, fakeConnection);

        dbManager.close();

        verify(fakeConnection, times(1)).close();
    }

    @Test
    @DisplayName("DAOs son null antes de connect()")
    void testDaosInicialmenteNulos() {
        assertNull(dbManager.getUserDao());
        assertNull(dbManager.getStatsDao());
        assertNull(dbManager.getSkinDao());
        assertNull(dbManager.getUserSkinDao());
        assertNull(dbManager.getUserLoadoutDao());
    }
}
