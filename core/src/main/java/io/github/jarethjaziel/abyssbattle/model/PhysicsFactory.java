package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import io.github.jarethjaziel.abyssbattle.util.Constants;

/**
 * Fábrica encargada de la creación y configuración de cuerpos físicos en el mundo de Box2D.
 * <p>
 * Implementa el patrón Factory para centralizar la complejidad de la construcción de entidades
 * (definición de cuerpos, formas, fixtures y propiedades físicas).
 */
public class PhysicsFactory {

    private static final String TAG = PhysicsFactory.class.getSimpleName();
    private World world = null;

    /**
     * Constructor principal.
     * @param world El mundo físico de Box2D donde se crearán los cuerpos.
     */
    public PhysicsFactory(World world) {
        this.world = world;
    }

    /**
     * Método auxiliar privado para crear un cuerpo rectangular estándar.
     * * @param centralPosX Posición X en PÍXELES (se convertirá a Metros internamente).
     * @param centralPosY Posición Y en PÍXELES (se convertirá a Metros internamente).
     * @param width       Ancho visual en PÍXELES.
     * @param height      Alto visual en PÍXELES.
     * @param bodyType    Tipo de cuerpo (Dinámico, Estático, Kinemático).
     * @return El cuerpo de Box2D creado.
     */
    private Body createBody(float centralPosX, float centralPosY, float width, float height, BodyType bodyType) {
        BodyDef bodyBuilder = new BodyDef();

        bodyBuilder.position.set(centralPosX / Constants.PIXELS_PER_METER,
                centralPosY / Constants.PIXELS_PER_METER);

        bodyBuilder.type = bodyType;

        Body body = world.createBody(bodyBuilder);

        PolygonShape shape = new PolygonShape();

        shape.setAsBox((width / 2) / Constants.PIXELS_PER_METER, (height / 2) / Constants.PIXELS_PER_METER);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 1.0f;
        fixtureDef.friction = 0.3f;

        body.createFixture(fixtureDef);
        shape.dispose();

        return body;
    }

    /**
     * Crea un Cañón estático en el mundo.
     *
     * @param px Posición X inicial.
     * @param py Posición Y inicial.
     * @return La entidad {@link Cannon} vinculada al cuerpo físico.
     */
    public Cannon createCannon(float px, float py) {

        Body cannonBody = createBody(px, py, Constants.CANNON_SIZE, Constants.CANNON_SIZE, BodyType.StaticBody);
        Cannon cannon = new Cannon(cannonBody);
        cannonBody.setUserData(cannon);
        return cannon;
    }

    /**
     * Crea una Tropa dinámica (soldado) en el mundo.
     *
     * @param px Posición X inicial.
     * @param py Posición Y inicial.
     * @return La entidad {@link Troop} vinculada al cuerpo físico.
     */
    public Troop createTroop(float px, float py) {
        Body troopBody = createBody(px, py, Constants.TROOP_SIZE, Constants.TROOP_SIZE, BodyType.DynamicBody);
        Troop troop = new Troop(troopBody, Constants.TROOP_INITIAL_HEALTH);
        troopBody.setUserData(troop);
        return troop;
    }

    /**
     * Crea un proyectil dinámico y calcula su velocidad inicial basada en el ángulo y potencia.
     *
     * @param px           Posición X de origen.
     * @param py           Posición Y de origen.
     * @param angleDegrees Ángulo de disparo en grados.
     * @param power        Potencia del disparo (magnitud de la velocidad).
     * @param damage       Daño que infligirá el proyectil.
     * @return La entidad {@link Projectile} creada.
     */
    public Projectile createProjectile(float px, float py, float angleDegrees, float power, int damage) {

        Body projectileBody = createBody(px, py, Constants.BULLET_SIZE, Constants.BULLET_SIZE, BodyType.DynamicBody);

        projectileBody.setBullet(true);
        projectileBody.getFixtureList().first().setSensor(true);

        // Ajuste de grados a radianes
        float angleRad = MathUtils.degreesToRadians * angleDegrees;

        // Calculamos cuánto se mueve en X y cuánto en Y
        float velX = MathUtils.cos(angleRad) * power;
        float velY = MathUtils.sin(angleRad) * power;

        Vector2 velocity = new Vector2(velX, velY);

        float initialVerticalSpeed = power * 0.5f;

        Projectile projectile = new Projectile(projectileBody, damage, velocity, initialVerticalSpeed);
        projectileBody.setUserData(projectile);
        Gdx.app.log(TAG, "PROJECTILE CREADO: " + projectile);
        Gdx.app.log(TAG, "POSICIÓN (M): " + projectileBody.getPosition());
        Gdx.app.log(TAG, "VELOCIDAD LINEAL: " + projectileBody.getLinearVelocity());

        return projectile;
    }
}
