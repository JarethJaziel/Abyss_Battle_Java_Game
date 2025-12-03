package io.github.jarethjaziel.abyssbattle.model;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

public class CombatManager {

    /**
     * Applies damage to troops within a radius.
     * @return true if at least one troop was destroyed (for bonus turn logic).
     */
    public boolean applyAreaDamage(Vector2 explosionCenter, float radiusMeters, int maxDamage, List<Troop> targets) {
        boolean anyTroopKilled = false;
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
                System.out.println("Damage applied: " + finalDamage);

                if (!t.isActive()) {
                    anyTroopKilled = true;
                }
            }
        }
        return anyTroopKilled;
    }

    /**
     * Determines the game state based on troop health.
     * @return The new GameState (Win/Draw/LastChance) or NULL if the game continues.
     */
    public GameState checkWinCondition(List<Player> players, boolean lastChanceUsed) {
        boolean p1Dead = areAllTroopsDead(players.get(0));
        boolean p2Dead = areAllTroopsDead(players.get(1));

        // 1. Draw
        if (p1Dead && p2Dead) return GameState.DRAW;

        // 2. P2 Wins
        if (p1Dead) return GameState.PLAYER_2_WIN;

        // 3. P2 Died... check Last Chance
        if (p2Dead) {
            if (lastChanceUsed) {
                return GameState.PLAYER_1_WIN;
            } else {
                return GameState.LAST_CHANCE; // Signal to activate Last Chance
            }
        }

        return null; // Game continues
    }
    
    public boolean areAllTroopsDead(Player p) {
        return p.getTroopList().stream().noneMatch(Troop::isActive);
    }
}