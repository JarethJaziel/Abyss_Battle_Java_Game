package database.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.Stats;

class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("braulio", "123hash");
    }

    @Test
    void testConstructorInitialValues() {
        assertEquals("braulio", user.getUsername());
        assertEquals("123hash", user.getPasswordHash());
        assertNotNull(user.getCreatedAt());
        assertNotNull(user.getStats());
        assertEquals(100, user.getCoins()); // monedas iniciales
    }

    @Test
    void testSetAndGetUsername() {
        user.setUsername("nuevo");
        assertEquals("nuevo", user.getUsername());
    }

    @Test
    void testSetAndGetPasswordHash() {
        user.setPasswordHash("otroh");
        assertEquals("otroh", user.getPasswordHash());
    }

    @Test
    void testAddCoins() {
        user.addCoins(50);
        assertEquals(150, user.getCoins());
        user.addCoins(25);
        assertEquals(175, user.getCoins());
    }

    @Test
    void testStatsAreCreatedAutomatically() {
        assertNotNull(user.getStats());
        assertEquals(0, user.getStats().getPlayed());
    }

    @Test
    void testSetStats() {
        Stats newStats = new Stats();
        newStats.addWin();
        user.setStats(newStats);

        assertEquals(newStats, user.getStats());
        assertEquals(1, user.getStats().getWon());
    }

    @Test
    void testCreatedAtIsCorrect() {
        Date created = user.getCreatedAt();
        assertNotNull(created);

        // una nueva fecha jamás puede ser futura
        assertTrue(created.before(new Date()) || created.equals(new Date()));
    }

    @Test
    void testToStringNotNull() {
        assertNotNull(user.toString());
        assertTrue(user.toString().contains("username"));
    }
}
