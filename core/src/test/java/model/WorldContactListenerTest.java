package model;

import com.badlogic.gdx.physics.box2d.*;
import io.github.jarethjaziel.abyssbattle.model.Projectile;
import io.github.jarethjaziel.abyssbattle.model.Troop;
import io.github.jarethjaziel.abyssbattle.model.WorldContactListener;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

 class WorldContactListenerTest {

    private WorldContactListener listener;

    @BeforeEach
    void setUp() {
        listener = new WorldContactListener();
    }

    /**
     * Verifica que cuando colisiona Projectile vs Troop:
     *  - Troop recibe daño
     *  - Projectile es destruido
     */
    @Test
    void testBeginContactProjectileHitsTroop() {

        // Crear mocks de contacto
        Contact contact = mock(Contact.class);

        Fixture fixtureA = mock(Fixture.class);
        Fixture fixtureB = mock(Fixture.class);

        Body bodyA = mock(Body.class);
        Body bodyB = mock(Body.class);

        Projectile projectile = mock(Projectile.class);
        Troop troop = mock(Troop.class);

        // Configurar jerarquía A → Projectile
        when(contact.getFixtureA()).thenReturn(fixtureA);
        when(fixtureA.getBody()).thenReturn(bodyA);
        when(bodyA.getUserData()).thenReturn(projectile);

        // Configurar jerarquía B → Troop
        when(contact.getFixtureB()).thenReturn(fixtureB);
        when(fixtureB.getBody()).thenReturn(bodyB);
        when(bodyB.getUserData()).thenReturn(troop);

        // Simular daño del proyectil
        when(projectile.getDamage()).thenReturn(15);

        // Ejecutar beginContact
        listener.beginContact(contact);

        // Verificar comportamiento correcto
        verify(troop).receiveDamage(15);
        verify(projectile).destroy();
    }

    /**
     * Verifica que no pasa nada cuando los objetos NO son Projectile vs Troop.
     */
    @Test
    void testBeginContactIrrelevantCollision() {

        Contact contact = mock(Contact.class);

        Fixture fixtureA = mock(Fixture.class);
        Fixture fixtureB = mock(Fixture.class);

        Body bodyA = mock(Body.class);
        Body bodyB = mock(Body.class);

        // A y B no son Troop ni Projectile
        when(contact.getFixtureA()).thenReturn(fixtureA);
        when(fixtureA.getBody()).thenReturn(bodyA);
        when(bodyA.getUserData()).thenReturn("No aplica");

        when(contact.getFixtureB()).thenReturn(fixtureB);
        when(fixtureB.getBody()).thenReturn(bodyB);
        when(bodyB.getUserData()).thenReturn(123);

        listener.beginContact(contact);

        // No debe llamarse ningún método
        // No hay verify() porque no existe comportamiento a validar
    }
}
