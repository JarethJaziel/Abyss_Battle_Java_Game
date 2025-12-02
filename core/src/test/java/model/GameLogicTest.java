package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import io.github.jarethjaziel.abyssbattle.model.Cannon;
import io.github.jarethjaziel.abyssbattle.model.GameLogic;
import io.github.jarethjaziel.abyssbattle.model.PhysicsFactory;
import io.github.jarethjaziel.abyssbattle.model.Player;
import io.github.jarethjaziel.abyssbattle.model.Projectile;
import io.github.jarethjaziel.abyssbattle.model.Troop;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

/**
 * GameLogicTest — único archivo, adaptado a tu GameLogic.java real.
 */
class GameLogicTest {

    // ---------- utilidades de reflexión (privadas dentro del mismo archivo) ----------
    private static class R {
        static void setField(Object target, String name, Object value) {
            try {
                Field f = findField(target.getClass(), name);
                f.setAccessible(true);
                f.set(target, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        static Object invoke(Object target, String name, Class<?>[] types, Object... args) {
            try {
                Method m = target.getClass().getDeclaredMethod(name, types);
                m.setAccessible(true);
                return m.invoke(target, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        private static Field findField(Class<?> cls, String name) throws NoSuchFieldException {
            Class<?> cur = cls;
            while (cur != null) {
                try {
                    return cur.getDeclaredField(name);
                } catch (NoSuchFieldException ex) {
                    cur = cur.getSuperclass();
                }
            }
            throw new NoSuchFieldException(name);
        }
    }

    // ---------- fixtures ----------
    private World mockWorld;
    private PhysicsFactory mockFactory;
    private GameLogic logic;

    private Player p1, p2;
    private Cannon c1, c2;
    private Body body1, body2;

    @BeforeEach
    void setUp() {
        mockWorld = mock(World.class);
        mockFactory = mock(PhysicsFactory.class);

        // create GameLogic with mocked world (constructor: GameLogic(World))
        logic = new GameLogic(mockWorld);

        // inject mocked physicsFactory into private field physicsFactory
        R.setField(logic, "physicsFactory", mockFactory);

        // prepare mocked Bodies for cannons
        body1 = mock(Body.class);
        body2 = mock(Body.class);
        when(body1.getPosition()).thenReturn(new Vector2(1f, 1f));
        when(body2.getPosition()).thenReturn(new Vector2(10f, 1f));

        // real Cannons but with mocked Body
        c1 = new Cannon(body1);
        c2 = new Cannon(body2);

        // real Players using your constructor Player(int) and setCannon
        p1 = new Player(1);
        p2 = new Player(2);
        p1.setCannon(c1);
        p2.setCannon(c2);

        // add players to logic (uses addPlayer(Player))
        logic.addPlayer(p1);
        logic.addPlayer(p2);
    }

    // ---------------- TESTS ----------------

    @Test
    void testStartGame_initializesAnglesStateAndTroopsToPlace() {
        logic.startGame();

        assertEquals(GameState.PLACEMENT_P1, logic.getState());
        assertEquals(p1, logic.getCurrentPlayer());
        assertEquals(Constants.MAX_PLAYER_TROOPS, logic.getTroopsToPlace());

        // p2 cannon angles adjusted (startGame config)
        assertEquals(180 + Constants.MIN_SHOOT_ANGLE, p2.getCannon().getMinAngle());
        assertEquals(180 + Constants.MAX_SHOOT_ANGLE, p2.getCannon().getMaxAngle());
        assertEquals(270f, p2.getCannon().getAngle());
    }

    @Test
    void testPlayerAim_changesAngle_whenNotWaitingOrGameOver() {
        logic.startGame(); // placement phase
        float before = p1.getCannon().getAngle();

        logic.playerAim(before + 12f);

        assertEquals(before + 12f, p1.getCannon().getAngle());
    }

    @Test
    void testPlayerAim_doesNotChangeWhenWaiting() {
        logic.startGame();
        // set internal state WAITING
        R.setField(logic, "state", GameState.WAITING);

        float before = p1.getCannon().getAngle();
        logic.playerAim(before + 30f);

        assertEquals(before, p1.getCannon().getAngle());
    }

    @Test
    void testPlayerShoot_registersProjectileAndSetsWaiting() {
        // mock a Projectile returned by factory
        Projectile mockProj = mock(Projectile.class);
        when(mockFactory.createProjectile(anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyInt()))
            .thenReturn(mockProj);

        // set logic to player 1 turn and currentPlayer to p1
        R.setField(logic, "state", GameState.PLAYER_1_TURN);
        R.setField(logic, "currentPlayer", p1);

        logic.playerShoot(35f);

        assertEquals(1, logic.getActiveProjectiles().size());
        assertEquals(GameState.WAITING, logic.getState());

        verify(mockFactory, times(1)).createProjectile(
                anyFloat(), anyFloat(), eq(c1.getAngle()), eq(35f), eq(Constants.BULLET_DAMAGE)
        );
    }

    @Test
    void testUpdate_whenProjectileLands_destroysBodyAndRemovesProjectile() {
        Projectile mockP = mock(Projectile.class);
        when(mockP.isFlying()).thenReturn(false);
        when(mockP.getGroundPosition()).thenReturn(new Vector2(50f, 50f));
        when(mockP.getDamage()).thenReturn(10);
        Body projBody = mock(Body.class);
        when(mockP.getBody()).thenReturn(projBody);

        // add projectile to active list
        logic.getActiveProjectiles().add(mockP);

        // call update — should call world.destroyBody and remove projectile
        logic.update(0.1f);

        verify(mockWorld, times(1)).destroyBody(projBody);
        assertTrue(logic.getActiveProjectiles().isEmpty());
    }

    @Test
    void testApplyAreaDamage_invokesReceiveDamageOnEnemyTroops_inRange() {
        // Ensure current player is p1 so enemy is p2
        R.setField(logic, "currentPlayer", p1);

        Troop troop = mock(Troop.class);
        when(troop.isActive()).thenReturn(true);
        when(troop.getPosX()).thenReturn(2.5f);
        when(troop.getPosY()).thenReturn(1.0f);

        p2.addTroop(troop);

        Vector2 explosion = new Vector2(2.5f * Constants.PIXELS_PER_METER, 1f * Constants.PIXELS_PER_METER);

        // call private applyAreaDamage
        R.invoke(logic, "applyAreaDamage", new Class[]{Vector2.class, float.class, int.class},
                explosion, Constants.EXPLOSION_RATIO, 100);

        verify(troop, atLeastOnce()).receiveDamage(anyInt());
    }

    @Test
    void testCheckWinner_setsPlayer2Win_whenP1AllDead() {
        Troop dead = mock(Troop.class);
        when(dead.isActive()).thenReturn(false);
        p1.addTroop(dead);

        Troop alive = mock(Troop.class);
        when(alive.isActive()).thenReturn(true);
        p2.addTroop(alive);

        boolean ended = logic.checkWinner();

        assertTrue(ended);
        assertEquals(GameState.PLAYER_2_WIN, logic.getState());
    }

    @Test
    void testCheckWinner_draw_whenBothDead() {
        Troop d1 = mock(Troop.class);
        when(d1.isActive()).thenReturn(false);
        Troop d2 = mock(Troop.class);
        when(d2.isActive()).thenReturn(false);

        p1.addTroop(d1);
        p2.addTroop(d2);

        boolean ended = logic.checkWinner();

        assertTrue(ended);
        assertEquals(GameState.DRAW, logic.getState());
    }

    @Test
    void testCheckWinner_activatesLastChance_whenP2AllDead_once() {
        Troop alive = mock(Troop.class);
        when(alive.isActive()).thenReturn(true);
        p1.addTroop(alive);

        Troop dead = mock(Troop.class);
        when(dead.isActive()).thenReturn(false);
        p2.addTroop(dead);

        boolean ended = logic.checkWinner();

        assertFalse(ended);
        assertEquals(GameState.LAST_CHANCE, logic.getState());
    }

    @Test
    void testChangeTurn_respectsLastChance_and_normalAlternation() {
        logic.startGame(); // sets currentPlayer = p1 and placement phase
        logic.changeTurn();
        assertEquals(p2, logic.getCurrentPlayer());
        logic.changeTurn();
        assertEquals(p1, logic.getCurrentPlayer());

        // Last chance case forces currentPlayer to p2
        R.setField(logic, "state", GameState.LAST_CHANCE);
        logic.changeTurn();
        assertEquals(p2, logic.getCurrentPlayer());
    }
}
