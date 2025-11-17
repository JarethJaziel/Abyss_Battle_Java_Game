package io.github.jarethjaziel.abyssbattle.model;

import java.util.List;

/**
 * Controla el sistema de daño del juego.
 */
public class DamageSystem {

    /**
     * Daño directo a una tropa impactada por un proyectil.
     */
    public void applyDirectHit(Troop troop, int damage) {
        troop.receiveDamage(damage);
    }

    /**
     * Daño en área a todas las tropas dentro de un radio.
     */
    public void applyAreaDamage(Board board, float cx, float cy, float radius) {

        List<Troop> troops = board.getTroops();

        for (Troop t : troops) {

            if (!t.isActive()) continue;

            float tx = t.getBody().getPosition().x;
            float ty = t.getBody().getPosition().y;

            float dx = tx - cx;
            float dy = ty - cy;

            float dist2 = dx * dx + dy * dy;
            float radius2 = radius * radius;

            if (dist2 <= radius2) {

                // daño proporcional a distancia
                float dist = (float) Math.sqrt(dist2);
                int damage = (int) (20 * (1 - dist / radius));

                if (damage > 0) {
                    t.receiveDamage(damage);
                }
            }
        }
    }
}

