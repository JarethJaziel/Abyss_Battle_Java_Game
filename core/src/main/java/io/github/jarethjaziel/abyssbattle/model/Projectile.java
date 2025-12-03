package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.jarethjaziel.abyssbattle.util.Constants;

public class Projectile extends Entity {

    private final int DAMAGE; 
        
    private boolean active;

    private float height = 0;
    private float verticalSpeed;
    private boolean hasLanded = false;

    private Vector2 groundPosition;

    public Projectile(Body body, int damage, Vector2 initialVelocity, float initialHeightSpeed) {
        super(body);
        groundPosition = new Vector2();
        this.body.setLinearVelocity(initialVelocity); // Movimiento en el plano X,Y (Suelo)
        
        // El "impulso" hacia arriba
        this.verticalSpeed = initialHeightSpeed; 
        this.DAMAGE = damage;
        this.active = true;
    }

    // Actualiza la posición z falsa
    public void update(float delta) {
        
        if (hasLanded) return;
        
        final float GRAVITY = -9.8f;
        final float TIME_FACTOR = 5.0f;
        
        verticalSpeed += GRAVITY * delta * TIME_FACTOR ; 

        height += verticalSpeed * delta * TIME_FACTOR;

        groundPosition.set(
            body.getPosition().x * Constants.PIXELS_PER_METER, 
            body.getPosition().y * Constants.PIXELS_PER_METER
        );

        if (height <= 0) {
            height = 0;
            hasLanded = true;
            body.setLinearVelocity(0, 0); // Detener el cuerpo de Box2D
            destroy();
        }
        System.out.println("body linear:" + body.getLinearVelocity());
    }
    
    public float getHeight() { return height; }
    public boolean isFlying() { return !hasLanded; }
    public Vector2 getGroundPosition() { return groundPosition;}
    public int getDamage(){ return DAMAGE; }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Projectile [DAMAGE=" + DAMAGE + ", active=" + active + ", height=" + height + ", verticalSpeed="
                + verticalSpeed + ", hasLanded=" + hasLanded + ", groundPosition=" + groundPosition + "]";
    }

    public void destroy() {
        this.active = false;
    }
}