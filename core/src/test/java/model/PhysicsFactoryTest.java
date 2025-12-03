package model;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import io.github.jarethjaziel.abyssbattle.model.*;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class PhysicsFactoryTest {

    private World world;
    private PhysicsFactory factory;
    private Body mockBody;

    @BeforeEach
    void setUp() {

        // Mockear Gdx.app para evitar NPE dentro de LibGDX
        Gdx.app = Mockito.mock(Application.class);

        world = Mockito.mock(World.class);
        mockBody = Mockito.mock(Body.class);  // ← ESTO FALTABA

        // Mock fixture y lista de fixtures
        Fixture mockFixture = Mockito.mock(Fixture.class);
        com.badlogic.gdx.utils.Array<Fixture> fixtureArray =
                new com.badlogic.gdx.utils.Array<>(new Fixture[]{mockFixture});

        Mockito.when(mockBody.getFixtureList()).thenReturn(fixtureArray);

        // World.createBody debe devolver mockBody
        Mockito.when(world.createBody(any())).thenReturn(mockBody);

        factory = new PhysicsFactory(world);
    }


    @Test
    void createCannon_createsStaticBody() {

        Cannon cannon = factory.createCannon(100, 200);

        // Capturar el BodyDef que se usó
        ArgumentCaptor<BodyDef> captor = ArgumentCaptor.forClass(BodyDef.class);
        Mockito.verify(world).createBody(captor.capture());
        BodyDef def = captor.getValue();

        assertEquals(BodyDef.BodyType.StaticBody, def.type);
        assertEquals(100 / Constants.PIXELS_PER_METER, def.position.x);
        assertEquals(200 / Constants.PIXELS_PER_METER, def.position.y);

        assertNotNull(cannon);
        assertEquals(mockBody, cannon.getBody());
    }

    @Test
    void createTroop_createsDynamicBody() {

        Troop troop = factory.createTroop(40, 60);

        ArgumentCaptor<BodyDef> captor = ArgumentCaptor.forClass(BodyDef.class);
        Mockito.verify(world).createBody(captor.capture());
        BodyDef def = captor.getValue();

        assertEquals(BodyDef.BodyType.DynamicBody, def.type);
        assertEquals(40 / Constants.PIXELS_PER_METER, def.position.x);
        assertEquals(60 / Constants.PIXELS_PER_METER, def.position.y);

        assertNotNull(troop);
        assertEquals(mockBody, troop.getBody());
    }

    @Test
    void createProjectile_setsSensorAndBullet() {

        Projectile projectile = factory.createProjectile(10, 20, 30f, 8f, 15);

        // El fixture debe ser sensor
        Fixture fx = mockBody.getFixtureList().first();
        Mockito.verify(fx).setSensor(true);

        // Debe activarse como bullet
        Mockito.verify(mockBody).setBullet(true);

        assertNotNull(projectile);
    }
}
