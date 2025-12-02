package model;

import com.badlogic.gdx.physics.box2d.*;
import io.github.jarethjaziel.abyssbattle.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.badlogic.gdx.utils.Array;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class PhysicsFactoryTest {

    private World world;
    private PhysicsFactory factory;

    // Body mock genérico para pruebas de Cannon y Troop
    private Body mockBody;

    @BeforeEach
    void setUp() {

        // Crear world mock (no usa librerías nativas)
        world = Mockito.mock(World.class);

        // Body mock usado como retorno de createBody()
        mockBody = Mockito.mock(Body.class);

        // Simular la lista de fixtures
        Fixture mockFixture = Mockito.mock(Fixture.class);
        Array<Fixture> fixtureArray = new Array<>();
        fixtureArray.add(mockFixture);

        Mockito.when(mockBody.getFixtureList()).thenReturn(fixtureArray);

        // Siempre que PhysicsFactory llame a createBody → regresará mockBody
        Mockito.when(world.createBody(any())).thenReturn(mockBody);

        // Instanciar la factory real
        factory = new PhysicsFactory(world);
    }

    
      //Verifica que createCannon configure el tipo StaticBody.
    
    @Test
    void testCreateCannon() {
        Mockito.when(mockBody.getType()).thenReturn(BodyDef.BodyType.StaticBody);

        Cannon cannon = factory.createCannon(100, 200);

        assertNotNull(cannon);
        assertNotNull(cannon.getBody());
        assertEquals(BodyDef.BodyType.StaticBody, cannon.getBody().getType());
    }

    
    // Verifica que createTroop configure el tipo DynamicBody.
    
    @Test
    void testCreateTroop() {
        Mockito.when(mockBody.getType()).thenReturn(BodyDef.BodyType.DynamicBody);

        Troop troop = factory.createTroop(50, 60);

        assertNotNull(troop);
        assertNotNull(troop.getBody());
        assertEquals(BodyDef.BodyType.DynamicBody, troop.getBody().getType());
    }

    /**
     * Verifica que createProjectile cree el proyectil y configure el sensor.
     */
    @Test
    void testCreateProjectile() {
        Mockito.when(mockBody.getType()).thenReturn(BodyDef.BodyType.DynamicBody);

        Projectile proj = factory.createProjectile(10, 10, 45f, 5f, 20);

        assertNotNull(proj);
        assertNotNull(proj.getBody());

        // Verificar sensor configurado en el primer fixture
        Fixture fixture = mockBody.getFixtureList().first();
        Mockito.verify(fixture).setSensor(true);
    }
}
