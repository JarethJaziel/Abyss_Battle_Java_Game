package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.jarethjaziel.abyssbattle.model.Projectile;

class ProjectileTest {

    private Body mockBody;
    private Projectile projectile;

    @BeforeEach
    void setUp() {
        mockBody = mock(Body.class);

        // Posición inicial simulada en Box2D
        when(mockBody.getPosition()).thenReturn(new Vector2(1, 2));

        projectile = new Projectile(
            mockBody,
            50,                     // DAMAGE
            new Vector2(10, 5),     // initial velocity (x,y)
            30                      // initialHeightSpeed (vertical "Z")
        );
    }

    @Test
    void constructor_initializesValuesCorrectly() {
        // Verificar velocidad inicial (solo chequea que se haya llamado)
        verify(mockBody).setLinearVelocity(new Vector2(10, 5));

        assertTrue(projectile.isActive());
        assertEquals(50, projectile.getDamage());
        assertEquals(0, projectile.getHeight());
        assertTrue(projectile.isFlying()); // aún no ha caído
    }

    @Test
    void update_increasesHeight_andAppliesGravity() {
        float previousHeight = projectile.getHeight();

        projectile.update(0.016f); // un frame (~60fps)

        assertTrue(projectile.getHeight() > previousHeight);
        assertTrue(projectile.isFlying());
    }

    @Test
    void update_landsProjectile_whenHeightReachesZero() {
        // Forzamos caída directa
        projectile.update(1.5f); // tiempo suficiente para que caiga a 0

        assertEquals(0, projectile.getHeight());
        assertFalse(projectile.isFlying());   // ha landed
        assertFalse(projectile.isActive());   // destroy() llamado

        // El cuerpo debe detenerse
        verify(mockBody, atLeastOnce()).setLinearVelocity(0, 0);
    }

    @Test
    void update_updatesGroundPositionCorrectly() {
        projectile.update(0.016f);

        Vector2 ground = projectile.getGroundPosition();

        // 1*PPM y 2*PPM → pero constants no importa aquí, solo validamos que se asignó algo
        assertNotNull(ground);
        assertTrue(ground.x > 0);
        assertTrue(ground.y > 0);
    }

    @Test
    void destroy_setsInactive() {
        projectile.destroy();
        assertFalse(projectile.isActive());
    }
}
