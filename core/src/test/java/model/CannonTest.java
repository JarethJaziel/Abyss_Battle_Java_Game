package model;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;

import io.github.jarethjaziel.abyssbattle.model.Cannon;
import io.github.jarethjaziel.abyssbattle.model.PhysicsFactory;
import io.github.jarethjaziel.abyssbattle.model.Projectile;
import io.github.jarethjaziel.abyssbattle.util.Constants;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class CannonTest {

    @Mock
    Body mockBody;

    @Mock
    PhysicsFactory mockFactory;

    @Mock
    Projectile mockProjectile;

    @Mock
    Application mockApp;

    Cannon cannon;
    AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        Box2D.init();
        closeable = MockitoAnnotations.openMocks(this);

        // Mock necesario para permitir el uso de Gdx.app.log durante las pruebas
        Gdx.app = mockApp;

        // Posición simulada usada por el método shoot
        when(mockBody.getPosition()).thenReturn(new Vector2(2f, 3f));

        cannon = new Cannon(mockBody);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void initialAngleIsMidpointOfLimits() {
        float expected = (Constants.MIN_SHOOT_ANGLE + Constants.MAX_SHOOT_ANGLE) / 2f;
        assertEquals(expected, cannon.getAngle());
    }

    @Test
    void setAngleWithinBounds() {
        float target = Constants.MIN_SHOOT_ANGLE + 10f;
        cannon.setAngle(target);
        assertEquals(target, cannon.getAngle());
    }

    @Test
    void angleClampsBelowMinimum() {
        cannon.setAngle(Constants.MIN_SHOOT_ANGLE - 50f);
        assertEquals(Constants.MIN_SHOOT_ANGLE, cannon.getAngle());
    }

    @Test
    void angleClampsAboveMaximum() {
        cannon.setAngle(Constants.MAX_SHOOT_ANGLE + 50f);
        assertEquals(Constants.MAX_SHOOT_ANGLE, cannon.getAngle());
    }

    @Test
    void updatedMinMaxLimitsAffectClamping() {
        cannon.setMinAngle(30f);
        cannon.setMaxAngle(150f);

        cannon.setAngle(10f);
        assertEquals(30f, cannon.getAngle());

        cannon.setAngle(200f);
        assertEquals(150f, cannon.getAngle());
    }

    @Test
    void shootCreatesProjectileAtCorrectPosition() {
        float power = 20f;
        int damage = 15;

        when(mockFactory.createProjectile(anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyInt()))
                .thenReturn(mockProjectile);

        Projectile result = cannon.shoot(mockFactory, power, damage);

        assertNotNull(result);
        assertEquals(mockProjectile, result);

        verify(mockFactory, times(1)).createProjectile(
                anyFloat(),
                anyFloat(),
                eq(cannon.getAngle()),
                eq(power),
                eq(damage)
        );
    }

    @Test
    void shootUsesCurrentAngleAndProvidedPowerDamage() {
        cannon.setAngle(Constants.MIN_SHOOT_ANGLE + 5);

        when(mockFactory.createProjectile(anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyInt()))
                .thenReturn(mockProjectile);

        cannon.shoot(mockFactory, 70f, 80);

        verify(mockFactory).createProjectile(
                anyFloat(),
                anyFloat(),
                eq(Constants.MIN_SHOOT_ANGLE + 5),
                eq(70f),
                eq(80)
        );
    }
}
