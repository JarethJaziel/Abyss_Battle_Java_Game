package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Clase responsable de crear proyectiles y aplicar la física inicial.
 */
public class ProjectilePhysics {

    private final float gravity;

    public ProjectilePhysics(float gravity) {
        this.gravity = gravity;
    }

    /**
     * Crea un proyectil en el mundo Box2D con posición y velocidad inicial.
     */
    public Projectile createProjectile(
            World world,
            float startX,
            float startY,
            float angleDegrees,
            float power
    ) {
        // Convertir a radianes
        float angle = (float) Math.toRadians(angleDegrees);

        // Crear el cuerpo físico
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(startX, startY);
        bodyDef.gravityScale = 1f;

        Body body = world.createBody(bodyDef);

        // Crear la forma del proyectil
        CircleShape shape = new CircleShape();
        shape.setRadius(0.1f);

        FixtureDef fixDef = new FixtureDef();
        fixDef.shape = shape;
        fixDef.density = 1f;
        fixDef.restitution = 0.1f;
        fixDef.friction = 0.2f;

        body.createFixture(fixDef);
        shape.dispose();

        // Aplicar velocidad inicial
        float vx = (float) (power * Math.cos(angle));
        float vy = (float) (power * Math.sin(angle));

        body.setLinearVelocity(new Vector2(vx, vy));

        // Crear y devolver el proyectil (daño fijo por ahora)
        return new Projectile(body, 20);
    }
}


