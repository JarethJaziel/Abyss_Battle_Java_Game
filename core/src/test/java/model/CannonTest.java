package model;

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

    Cannon cannon;
    AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        // Iniciar Box2D para evitar problemas con librerías nativas
        Box2D.init();

        closeable = MockitoAnnotations.openMocks(this);

        // Simular posicion del body (usada por shoot)
        when(mockBody.getPosition()).thenReturn(new Vector2(2f, 3f));

        // Crear la instancia real con el Body mockeado (constructor correcto)
        cannon = new Cannon(mockBody);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testInitialAngleIsMidpoint() {
        float expected = (Constants.MIN_SHOOT_ANGLE + Constants.MAX_SHOOT_ANGLE) / 2f;
        assertEquals(expected, cannon.getAngle());
    }

    @Test
    void testSetAngleWithinLimits() {
        float target = Constants.MIN_SHOOT_ANGLE + 10f;
        cannon.setAngle(target);
        assertEquals(target, cannon.getAngle());
    }

    @Test
    void testSetAngleClampsBelowMin() {
        cannon.setAngle(Constants.MIN_SHOOT_ANGLE - 100f);
        assertEquals(Constants.MIN_SHOOT_ANGLE, cannon.getAngle());
    }

    @Test
    void testSetAngleClampsAboveMax() {
        cannon.setAngle(Constants.MAX_SHOOT_ANGLE + 100f);
        assertEquals(Constants.MAX_SHOOT_ANGLE, cannon.getAngle());
    }

    @Test
    void testChangeMinMaxAngleAffectsClamping() {
        // Cambiamos los límites
        cannon.setMinAngle(30f);
        cannon.setMaxAngle(150f);

        cannon.setAngle(10f);
        assertEquals(30f, cannon.getAngle());

        cannon.setAngle(200f);
        assertEquals(150f, cannon.getAngle());
    }

    @Test
    void testShootCreatesProjectileWithCorrectParams() {
        float power = 20f;
        int damage = 15;

        // Configurar factory para devolver el mock de projectile
        when(mockFactory.createProjectile(anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyInt()))
                .thenReturn(mockProjectile);

        Projectile result = cannon.shoot(mockFactory, power, damage);

        assertNotNull(result);
        assertEquals(mockProjectile, result);

        // Verificamos que createProjectile fue llamado con:
        // (tipX, tipY, angle, power, damage)
        verify(mockFactory, times(1))
                .createProjectile(anyFloat(), anyFloat(), eq(cannon.getAngle()), eq(power), eq(damage));
    }

    @Test
    void testShootUsesCurrentAngleAndPowerOrder() {
        // Ajustamos ángulo
        cannon.setAngle(Constants.MIN_SHOOT_ANGLE + 5);


        when(mockFactory.createProjectile(anyFloat(), anyFloat(), anyFloat(), anyFloat(), anyInt()))
                .thenReturn(mockProjectile);

        cannon.shoot(mockFactory, 70f, 80);

        // Verificamos llamada con power y angle en sus posiciones correctas:
        // createProjectile(tipX, tipY, angle, power, damage)
        verify(mockFactory).createProjectile(
            anyFloat(),
            anyFloat(),
            eq(Constants.MIN_SHOOT_ANGLE + 5),
            eq(70f),
            eq(80)
        );

    }
}
