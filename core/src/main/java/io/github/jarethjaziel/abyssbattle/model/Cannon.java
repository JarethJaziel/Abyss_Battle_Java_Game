package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.jarethjaziel.abyssbattle.util.Constants;

public class Cannon extends Entity {

    private float angle;
    
    private float barrelLength; 
    
    private float minAngle;
    private float maxAngle;

    public Cannon(Body body) {
        super(body);
        this.angle = (Constants.MIN_SHOOT_ANGLE + Constants.MAX_SHOOT_ANGLE) / 2;
        this.barrelLength = Constants.CANNON_SIZE/2f;
        this.minAngle = Constants.MIN_SHOOT_ANGLE;
        this.maxAngle = Constants.MAX_SHOOT_ANGLE;
    }

    public void setAngle(float angle) {
        this.angle = MathUtils.clamp(angle, minAngle, maxAngle);
    }

    public float getMinAngle() {
        return minAngle;
    }

    public void setMinAngle(float minAngle) {
        this.minAngle = minAngle;
    }

    public float getMaxAngle() {
        return maxAngle;
    }

    public void setMaxAngle(float maxAngle) {
        this.maxAngle = maxAngle;
    }

    public float getAngle() {
        return angle;
    }

    /**
     * Este método calcula la posición de la punta y pide a la fábrica crear la bala
     */
    public Projectile shoot(PhysicsFactory factory, float power, int damage) {
        
        float centerX = body.getPosition().x * Constants.PIXELS_PER_METER;
        float centerY = body.getPosition().y * Constants.PIXELS_PER_METER;

        float angleRad = MathUtils.degreesToRadians * angle;
        
        float tipX = centerX + MathUtils.cos(angleRad) * barrelLength;
        float tipY = centerY + MathUtils.sin(angleRad) * barrelLength;

        return factory.createProjectile(tipX, tipY, angle, power, damage);
    }

    @Override
    public boolean isActive() {
        return true;
    }
}