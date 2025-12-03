package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.jarethjaziel.abyssbattle.util.Constants;
/**
 * Representa el cañón de un jugador en el mundo físico.
 * <p>
 * Esta entidad gestiona el ángulo de disparo, las restricciones de movimiento (clamp)
 * y el cálculo de la posición de salida del proyectil.
 */
public class Cannon extends Entity {

    /** Ángulo actual del cañón en grados. */
    private float angle;

    /** Longitud del cañón visual/físico, usado para calcular el punto de origen del disparo (tip). */
    private float barrelLength;

    /** Ángulo mínimo permitido para este cañón (restricción de rotación). */
    private float minAngle;

    /** Ángulo máximo permitido para este cañón (restricción de rotación). */
    private float maxAngle;

    /**
     * Crea una nueva instancia de Cannon asociada a un cuerpo físico de Box2D.
     * * @param body El cuerpo físico (Box2D) que representa la base del cañón.
     */
    public Cannon(Body body) {
        super(body);
        this.angle = (Constants.MIN_SHOOT_ANGLE + Constants.MAX_SHOOT_ANGLE) / 2;
        this.barrelLength = Constants.CANNON_SIZE/2f;
        this.minAngle = Constants.MIN_SHOOT_ANGLE;
        this.maxAngle = Constants.MAX_SHOOT_ANGLE;
    }

    /**
     * Establece el ángulo del cañón, respetando los límites definidos.
     * * @param angle El nuevo ángulo deseado en grados.
     */
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
     * Calcula la posición exacta de la punta del cañón y solicita la creación de un proyectil.
     * * @param factory La fábrica de física encargada de instanciar el cuerpo del proyectil.
     * @param power   La fuerza o potencia del disparo (0-100).
     * @param damage  El daño que infligirá el proyectil.
     * @return El objeto {@link Projectile} creado y activo.
     */
    public Projectile shoot(PhysicsFactory factory, float power, int damage) {
        
        float centerX = body.getPosition().x * Constants.PIXELS_PER_METER;
        float centerY = body.getPosition().y * Constants.PIXELS_PER_METER;
        Gdx.app.log("CENTERX", ""+centerX);
        Gdx.app.log("CENTERY", ""+centerY);
        
        float angleRad = MathUtils.degreesToRadians * angle;
        Gdx.app.log("ANGLERAD", ""+angleRad);
        float tipX = centerX + MathUtils.cos(angleRad) * barrelLength;
        float tipY = centerY + MathUtils.sin(angleRad) * barrelLength;
        Gdx.app.log("TIP_CENTERX", ""+tipX);
        Gdx.app.log("TIP_CENTERY", ""+tipY);
        return factory.createProjectile(tipX, tipY, angle, power, damage);
    }

    @Override
    public boolean isActive() {
        return true;
    }
}