package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Representa una unidad de tropa (soldado, tanque, etc.) en el campo de
 * batalla.
 * <p>
 * Esta entidad posee salud y puede recibir daño. Su estado activo depende de si
 * su salud es mayor a cero.
 */
public class Troop extends Entity {

    /** Puntos de salud actuales de la tropa. */
    private int health;

    /**
     * Crea una nueva tropa.
     *
     * @param body          El cuerpo físico de Box2D asociado.
     * @param initialHealth La cantidad inicial de puntos de vida.
     */
    public Troop(Body body, int initialHealth) {
        super(body);
        this.health = initialHealth;
    }

    /**
     * Aplica daño a la tropa, reduciendo su salud.
     * <p>
     * La salud nunca bajará de cero.
     *
     * @param damage Cantidad de daño a infligir.
     */
    public void receiveDamage(int damage) {
        int newHealth = this.health - damage;
        this.health = Math.max(0, newHealth);
    }

    /**
     * Verifica si la tropa sigue operativa.
     *
     * @return {@code true} si la salud es mayor a 0, {@code false} si ha sido
     *         destruida.
     */
    @Override
    public boolean isActive() {
        return health > 0;
    }

    public int getHealth() {
        return health;
    }

}
