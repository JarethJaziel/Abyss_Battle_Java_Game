package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

import io.github.jarethjaziel.abyssbattle.util.Constants;

public class PhysicsFactory {

    private World world = null;

    public PhysicsFactory(World world) {
        this.world = world;
    }

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

    public Cannon createCannon(float px, float py) {
        
        Body cannonBody = createBody(px, py, Constants.CANNON_SIZE, Constants.CANNON_SIZE, BodyType.StaticBody);
        Cannon cannon = new Cannon(cannonBody);
        cannonBody.setUserData(cannon);
        return cannon;
    }

    public Troop createTroop(float px, float py) {
        Body troopBody = createBody(px, py, Constants.TROOP_SIZE, Constants.TROOP_SIZE, BodyType.DynamicBody);
        Troop troop = new Troop(troopBody, Constants.TROOP_INITIAL_HEALTH);
        troopBody.setUserData(troop);
        return troop;
    }

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
        
        return projectile;
    }
}
