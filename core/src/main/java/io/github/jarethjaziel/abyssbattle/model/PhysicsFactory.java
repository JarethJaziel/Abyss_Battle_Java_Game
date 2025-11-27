package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public class PhysicsFactory {

    public static final float PPM = 100f;

    public static Cannon createCannon(World world, float px, float py) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.StaticBody;
        def.position.set(px / PPM, py / PPM);

        Body body = world.createBody(def);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(0.5f, 0.5f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;

        body.createFixture(fdef);
        shape.dispose();

        return new Cannon(body);
    }

    public static Troop createTroop(World world, float px, float py, int health) {
        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(px / PPM, py / PPM);

        Body body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.35f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;
        fdef.restitution = 0.2f;

        body.createFixture(fdef);
        shape.dispose();

        return new Troop(body, health);
    }

    public static Projectile createProjectile(World world, float px, float py, float angle, float power, int damage) {

        BodyDef def = new BodyDef();
        def.type = BodyDef.BodyType.DynamicBody;
        def.position.set(px / PPM, py / PPM);

        Body body = world.createBody(def);

        CircleShape shape = new CircleShape();
        shape.setRadius(0.15f);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;
        fdef.restitution = 0.1f;

        body.createFixture(fdef);
        shape.dispose();

        float velX = (float) (Math.cos(Math.toRadians(angle)) * power);
        float velY = (float) (Math.sin(Math.toRadians(angle)) * power);

        body.setLinearVelocity(new Vector2(velX, velY));

        return new Projectile(body, damage);
    }
}
