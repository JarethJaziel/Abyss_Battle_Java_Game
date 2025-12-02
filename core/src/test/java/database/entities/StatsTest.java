package database.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.jarethjaziel.abyssbattle.database.entities.Stats;

class StatsTest {

    private Stats stats;

    @BeforeEach
    void setUp() {
        stats = new Stats();
    }

    @Test
    void testInitialValues() {
        assertEquals(0, stats.getPlayed());
        assertEquals(0, stats.getWon());
        assertEquals(0, stats.getLost());
        assertEquals(0, stats.getHits());
        assertEquals(0, stats.getMisses());
        assertEquals(0, stats.getDamageTotal());
        assertNotNull(stats.getLastPlayed());
    }

    @Test
    void testAddWin() {
        stats.addWin();
        assertEquals(1, stats.getWon());
        assertEquals(1, stats.getPlayed());
    }

    @Test
    void testAddLoss() {
        stats.addLoss();
        assertEquals(1, stats.getLost());
        assertEquals(1, stats.getPlayed());
    }

    @Test
    void testAddHit() {
        stats.addHit();
        assertEquals(1, stats.getHits());
    }

    @Test
    void testAddMiss() {
        stats.addMiss();
        assertEquals(1, stats.getMisses());
    }

    @Test
    void testAddDamage() {
        stats.addDamage(50);
        assertEquals(50, stats.getDamageTotal());
        stats.addDamage(20);
        assertEquals(70, stats.getDamageTotal());
    }

    @Test
    void testSetLastPlayed() {
        Date now = new Date();
        stats.setLastPlayed(now);
        assertEquals(now, stats.getLastPlayed());
    }
}

