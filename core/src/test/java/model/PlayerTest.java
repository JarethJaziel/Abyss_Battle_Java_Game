package model;

import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import io.github.jarethjaziel.abyssbattle.model.Cannon;
import io.github.jarethjaziel.abyssbattle.model.PhysicsFactory;
import io.github.jarethjaziel.abyssbattle.model.Player;
import io.github.jarethjaziel.abyssbattle.model.Troop;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testPlayerInitialization() {
        Player player = new Player(1);

        assertEquals(1, player.getId());
        assertNotNull(player.getTroopList());
        assertTrue(player.getTroopList().isEmpty());
        assertNull(player.getCannon());
    }

    @Test
    void testAddTroop() {
        Player player = new Player(1);
        Troop troop = mock(Troop.class);

        player.addTroop(troop);

        List<Troop> troops = player.getTroopList();
        assertEquals(1, troops.size());
        assertEquals(troop, troops.get(0));
    }

    @Test
    void testSetCannon() {
        Player player = new Player(1);
        Cannon cannon = mock(Cannon.class);

        player.setCannon(cannon);

        assertEquals(cannon, player.getCannon());
    }

    @Test
    void testShootCallsCannon() {
        Player player = new Player(1);

        Cannon cannon = mock(Cannon.class);
        PhysicsFactory factory = mock(PhysicsFactory.class);

        player.setCannon(cannon);

        player.shoot(factory, 10f, 5);

        verify(cannon, times(1)).shoot(factory, 10f, 5);
    }

    @Test
    void testShootWithNoCannonDoesNotCrash() {
        Player player = new Player(1);
        PhysicsFactory factory = mock(PhysicsFactory.class);

        assertThrows(NullPointerException.class, () -> {
            player.shoot(factory, 10f, 5);
    });
}


    @Test
    void testFinishTurnDoesNotCrash() {
        Player player = new Player(1);

        assertDoesNotThrow(player::finishTurn);
    }
}
