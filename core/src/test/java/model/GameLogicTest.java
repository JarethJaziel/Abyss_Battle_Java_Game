package model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import io.github.jarethjaziel.abyssbattle.model.*;
import io.github.jarethjaziel.abyssbattle.util.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameLogicTest {

    private GameLogic game;
    private World world;
    private PhysicsFactory physicsFactory;
    private TurnManager turnManager;
    private CombatManager combatManager;

    private Player p1;
    private Player p2;

    @BeforeEach
    void setup() throws Exception {
        world = mock(World.class);
        physicsFactory = mock(PhysicsFactory.class);
        turnManager = mock(TurnManager.class);
        combatManager = mock(CombatManager.class);

        p1 = mock(Player.class);
        p2 = mock(Player.class);

        ArrayList<Player> players = new ArrayList<>();
        players.add(p1);
        players.add(p2);

        game = new GameLogic(world);

        setField("physicsFactory", physicsFactory);
        setField("turnManager", turnManager);
        setField("combatManager", combatManager);
        setField("players", players);
        setField("activeProjectiles", new ArrayList<Projectile>());
    }

    private void setField(String fieldName, Object value) throws Exception {
        Field f = GameLogic.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(game, value);
    }

    private Object invokePrivate(String methodName, Object... args) throws Exception {
        for (Method m : GameLogic.class.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {
                m.setAccessible(true);
                return m.invoke(game, args);
            }
        }
        throw new RuntimeException("Method not found: " + methodName);
    }

    @SuppressWarnings("unchecked")
    private <T> ArrayList<T> list(String fieldName) throws Exception {
        Field f = GameLogic.class.getDeclaredField(fieldName);
        f.setAccessible(true);
        return (ArrayList<T>) f.get(game);
    }

    @Test
    void tryPlaceTroop_delegatesCorrectly() throws Exception {
        when(turnManager.getCurrentPlayer()).thenReturn(p1);
        when(p1.getId()).thenReturn(1);

        Troop t = mock(Troop.class);
        when(physicsFactory.createTroop(10f, 200f)).thenReturn(t);

        game.tryPlaceTroop(10f, 200f);

        verify(physicsFactory).createTroop(10f, 200f);
        verify(p1).addTroop(t);
        verify(turnManager).decreaseTroopsToPlace();
    }

    @Test
    void handleImpact_whenWin_setsState() throws Exception {
        when(turnManager.getCurrentPlayer()).thenReturn(p1);
        when(p1.getId()).thenReturn(1);
        when(turnManager.getEnemyPlayer()).thenReturn(p2);
        when(p2.getTroopList()).thenReturn(new ArrayList<>());

        when(combatManager.applyAreaDamage(any(), anyFloat(), anyInt(), anyList()))
                .thenReturn(false);

        when(combatManager.checkWinCondition(anyList(), anyBoolean()))
                .thenReturn(GameState.PLAYER_1_WIN);

        Projectile proj = mock(Projectile.class);
        when(proj.getGroundPosition()).thenReturn(new Vector2(0, 0));
        when(proj.getDamage()).thenReturn(50);
        when(proj.getBody()).thenReturn(mock(Body.class));

        // invoke private handleImpact
        invokePrivate("handleImpact", proj);

        verify(turnManager).setState(GameState.PLAYER_1_WIN);
    }

    @Test
    void update_whenProjectileLands_cleansUp() throws Exception {
        when(turnManager.getCurrentPlayer()).thenReturn(p1);
        when(p1.getId()).thenReturn(1);
        when(turnManager.getEnemyPlayer()).thenReturn(p2);
        when(p2.getTroopList()).thenReturn(new ArrayList<>());

        Projectile proj = mock(Projectile.class);
        Body body = mock(Body.class);

        when(proj.isFlying()).thenReturn(false);
        when(proj.getGroundPosition()).thenReturn(new Vector2(0, 0));
        when(proj.getDamage()).thenReturn(50);
        when(proj.getBody()).thenReturn(body);

        ArrayList<Projectile> active = list("activeProjectiles");
        active.add(proj);

        game.update(0.016f);

        verify(world).destroyBody(body);
        assertTrue(list("activeProjectiles").isEmpty());
    }

    @Test
    void playerShoot_registersProjectile() throws Exception {
        when(turnManager.getState()).thenReturn(GameState.PLAYER_1_TURN);
        when(turnManager.getCurrentPlayer()).thenReturn(p1);
        when(p1.getId()).thenReturn(1);

        Cannon cannon = mock(Cannon.class);
        Projectile proj = mock(Projectile.class);

        when(p1.getCannon()).thenReturn(cannon);
        when(cannon.shoot(any(), anyFloat(), anyInt())).thenReturn(proj);

        game.playerShoot(0.9f);

        assertTrue(list("activeProjectiles").contains(proj));
        verify(turnManager).setWaitingState();
    }

    @Test
    void playerAim_setsAngle() {
        when(turnManager.getState()).thenReturn(GameState.PLAYER_1_TURN);
        when(turnManager.getCurrentPlayer()).thenReturn(p1);

        Cannon cannon = mock(Cannon.class);
        when(p1.getCannon()).thenReturn(cannon);

        game.playerAim(40);

        verify(cannon).setAngle(40);
    }
}
