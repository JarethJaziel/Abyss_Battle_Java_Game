package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.jarethjaziel.abyssbattle.util.Constants;
/**
 * Representa un proyectil disparado en el juego.
 * <p>
 * Combina un cuerpo físico de Box2D (para movimiento top-down en X/Y) con una
 * simulación manual de altura (eje Z falso) para crear el efecto parabólico visual.
 */
public class Projectile extends Entity {
    
    /** Daño que inflige este proyectil al impactar. */
    private final int damage; 
        
    private boolean active;

    /** Altura visual simulada. */
    private float height = 0;
    
    /** Velocidad vertical para la simulación de gravedad visual. */
    private float verticalSpeed;
    
    private boolean hasLanded = false;

    /** Posición en coordenadas de pantalla (Píxeles) para renderizado. */
    private Vector2 groundPosition;

    /**
     * Crea un nuevo proyectil.
     *
     * @param body                 Cuerpo físico Box2D.
     * @param damage               Daño del impacto.
     * @param initialVelocity      Velocidad lineal en el plano (Metros/s).
     * @param initialHeightSpeed   Impulso vertical inicial (simulación visual).
     */
    public Projectile(Body body, int damage, Vector2 initialVelocity, float initialHeightSpeed) {
        super(body);
        groundPosition = new Vector2();
        this.body.setLinearVelocity(initialVelocity);
        
        this.verticalSpeed = initialHeightSpeed; 
        this.damage = damage;
        this.active = true;
    }

    /**
     * Actualiza la física visual del proyectil (altura y gravedad).
     *
     * @param delta Tiempo transcurrido en segundos.
     */
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
    }
    
    public float getHeight() { return height; }
    public boolean isFlying() { return !hasLanded; }
    public Vector2 getGroundPosition() { return groundPosition;}
    public int getDamage(){ return damage; }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return "Projectile [DAMAGE=" + damage + ", active=" + active + ", height=" + height + ", verticalSpeed="
                + verticalSpeed + ", hasLanded=" + hasLanded + ", groundPosition=" + groundPosition + "]";
    }

    public void destroy() {
        this.active = false;
    }
}