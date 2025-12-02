package util;

import io.github.jarethjaziel.abyssbattle.util.Constants;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConstantsTest {

    @Test
    void testWorldDimensions() {
        assertEquals(736, Constants.WORLD_WIDTH);
        assertEquals(1104, Constants.WORLD_HEIGHT);
    }

    @Test
    void testPixelsPerMeter() {
        assertEquals(32f, Constants.PIXELS_PER_METER);
    }

    @Test
    void testSizes() {
        assertEquals(0.5f * 32f, Constants.BULLET_SIZE);
        assertEquals(1.2f * 32f, Constants.TROOP_SIZE);
        assertEquals(1.8f * 32f, Constants.CANNON_SIZE);
    }

    @Test
    void testExplosion() {
        assertEquals(2.5f, Constants.EXPLOSION_RATIO);
        assertEquals(2.5f * 32f, Constants.EXPLOSION_SIZE);
    }

    @Test
    void testGameDefaults() {
        assertEquals(3, Constants.MAX_PLAYER_TROOPS);
        assertEquals(100, Constants.TROOP_INITIAL_HEALTH);
        assertEquals(100, Constants.BULLET_DAMAGE);
    }
}
