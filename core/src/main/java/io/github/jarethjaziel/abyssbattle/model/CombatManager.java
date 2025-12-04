package io.github.jarethjaziel.abyssbattle.model;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

/**
 * Gestor de lógica de combate puro.
 * <p>
 * Esta clase se encarga de realizar cálculos matemáticos relacionados con el daño,
 * las áreas de efecto y la verificación de condiciones de victoria.
 * No mantiene estado del juego, solo procesa datos.
 */
public class CombatManager {

    /**
     * Aplica daño de área a una lista de tropas basada en un punto de explosión.
     * <p>
     * El daño disminuye linealmente desde el centro de la explosión hacia afuera.
     *
     * @param explosionCenter Coordenada (Píxeles/Mundo) donde ocurrió la explosión.
     * @param radiusMeters    Radio de la explosión en metros (se convertirá a píxeles).
     * @param maxDamage       Daño máximo en el epicentro.
     * @param targets         Lista de tropas que pueden recibir daño.
     * @return Un objeto {@link DamageReport} con el resumen del daño total y si hubo bajas.
     */
    public DamageReport applyAreaDamage(Vector2 explosionCenter, float radiusMeters, int maxDamage, List<Troop> targets) {
        boolean anyTroopKilled = false;
        int totalRealDamage = 0;

        float radiusPixels = radiusMeters * Constants.PIXELS_PER_METER;

        for (Troop t : targets) {
            if (!t.isActive()) continue;

            Vector2 posTroop = new Vector2(
                    t.getPosX() * Constants.PIXELS_PER_METER,
                    t.getPosY() * Constants.PIXELS_PER_METER);
            
            float distance = posTroop.dst(explosionCenter);

            if (distance <= radiusPixels) {
                float damageFactor = 1.0f - (distance / radiusPixels);
                if (damageFactor < 0) damageFactor = 0;

                int finalDamage = (int) (maxDamage * damageFactor);
                if (finalDamage < 1 && maxDamage > 0) finalDamage = 1;

                t.receiveDamage(finalDamage);
                totalRealDamage += finalDamage;
                Gdx.app.log("DAMAGE: ", ""+finalDamage);

                if (!t.isActive()) {
                    anyTroopKilled = true;
                }
            }
        }
        return new DamageReport(anyTroopKilled, totalRealDamage);
    }

    /**
     * Determina el estado del juego basado en la salud de las tropas.
     *
     * @param players        Lista de jugadores en la partida.
     * @param lastChanceUsed Indica si el jugador 2 ya gastó su "Última Oportunidad".
     * @return El nuevo {@link GameState} (Victoria, Empate, LastChance) o {@code null} si el juego continúa.
     */
    public GameState checkWinCondition(List<Player> players, boolean lastChanceUsed) {
        boolean p1Dead = areAllTroopsDead(players.get(0));
        boolean p2Dead = areAllTroopsDead(players.get(1));

        if (p1Dead && p2Dead) return GameState.DRAW;

        if (p1Dead) return GameState.PLAYER_2_WIN;

        if (p2Dead) {
            if (lastChanceUsed) {
                return GameState.PLAYER_1_WIN;
            } else {
                return GameState.LAST_CHANCE;
            }
        }

        return null;
    }
    /**
     * Verifica si todas las tropas de un jugador están inactivas.
     *
     * @param p El jugador a verificar.
     * @return {@code true} si todas las tropas tienen vida &lt;= 0.
     */
    public boolean areAllTroopsDead(Player p) {
        return p.getTroopList().stream().noneMatch(Troop::isActive);
    }
}