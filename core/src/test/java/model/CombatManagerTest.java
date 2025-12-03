package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.jarethjaziel.abyssbattle.model.CombatManager;
import io.github.jarethjaziel.abyssbattle.model.Player;
import io.github.jarethjaziel.abyssbattle.model.Troop;
import io.github.jarethjaziel.abyssbattle.util.GameState;

class CombatManagerTest {

    private CombatManager cm;

    @BeforeEach
    void setup() {
        cm = new CombatManager();
    }

    private Troop mockTroop(float x, float y, int health) {
        Body body = mock(Body.class);
        Troop troop = spy(new Troop(body, health));

        // Mock position: body.getPosition()
        when(body.getPosition()).thenReturn(new Vector2(x, y));

        return troop;
    }

    private Player mockPlayer(Troop... troops) {
        Player p = new Player(1);
        for (Troop t : troops) p.addTroop(t);
        return p;
    }

    @Test
    void applyAreaDamage_appliesDamageAndDetectsKilled() {
        Troop t1 = mockTroop(0, 0, 30);
        Troop t2 = mockTroop(0.5f, 0, 30);
        Troop t3 = mockTroop(10, 10, 100); // fuera del rango

        Vector2 explosion = new Vector2(0, 0);
        float radiusMeters = 2f;
        int maxDamage = 30;

        boolean killed = cm.applyAreaDamage(explosion, radiusMeters, maxDamage, List.of(t1, t2, t3));

        assertTrue(t1.getHealth() < 30);
        assertTrue(t2.getHealth() < 30);
        assertEquals(100, t3.getHealth());
        assertTrue(killed);
    }

    @Test
    void applyAreaDamage_returnsFalseIfNoTroopDies() {
        Troop t = mockTroop(5, 5, 100);
        boolean killed = cm.applyAreaDamage(new Vector2(0, 0), 1, 10, List.of(t));
        assertFalse(killed);
    }

    @Test
    void areAllTroopsDead_returnsTrueWhenAllDead() {
        Troop t1 = mockTroop(0, 0, 0);
        Troop t2 = mockTroop(1, 1, 0);

        Player p = mockPlayer(t1, t2);

        assertTrue(cm.areAllTroopsDead(p));
    }

    @Test
    void areAllTroopsDead_returnsFalseIfAnyAlive() {
        Troop t1 = mockTroop(0, 0, 50);
        Troop t2 = mockTroop(1, 1, 0);

        Player p = mockPlayer(t1, t2);

        assertFalse(cm.areAllTroopsDead(p));
    }

    @Test
    void checkWinCondition_drawWhenBothDead() {
        Troop t1 = mockTroop(0, 0, 0);
        Troop t2 = mockTroop(1, 1, 0);

        Player p1 = mockPlayer(t1);
        Player p2 = mockPlayer(t2);

        assertEquals(GameState.DRAW, cm.checkWinCondition(List.of(p1, p2), false));
    }

    @Test
    void checkWinCondition_p2WinsIfP1Dead() {
        Troop deadP1 = mockTroop(0, 0, 0);
        Troop aliveP2 = mockTroop(1, 1, 50);

        Player p1 = mockPlayer(deadP1);
        Player p2 = mockPlayer(aliveP2);

        assertEquals(GameState.PLAYER_2_WIN, cm.checkWinCondition(List.of(p1, p2), false));
    }

    @Test
    void checkWinCondition_lastChanceIfP2DeadAndNotUsed() {
        Troop aliveP1 = mockTroop(0, 0, 50);
        Troop deadP2 = mockTroop(1, 1, 0);

        Player p1 = mockPlayer(aliveP1);
        Player p2 = mockPlayer(deadP2);

        assertEquals(GameState.LAST_CHANCE, cm.checkWinCondition(List.of(p1, p2), false));
    }

    @Test
    void checkWinCondition_p1WinsIfP2DeadAndLastChanceUsed() {
        Troop aliveP1 = mockTroop(0, 0, 50);
        Troop deadP2 = mockTroop(1, 1, 0);

        Player p1 = mockPlayer(aliveP1);
        Player p2 = mockPlayer(deadP2);

        assertEquals(GameState.PLAYER_1_WIN, cm.checkWinCondition(List.of(p1, p2), true));
    }

    @Test
    void checkWinCondition_returnsNullIfGameContinues() {
        Troop alive1 = mockTroop(0, 0, 50);
        Troop alive2 = mockTroop(1, 1, 50);

        Player p1 = mockPlayer(alive1);
        Player p2 = mockPlayer(alive2);

        assertNull(cm.checkWinCondition(List.of(p1, p2), false));
    }
}
