package io.github.jarethjaziel.abyssbattle.model;

import io.github.jarethjaziel.abyssbattle.database.entities.Skin;

public class MatchContext {
    public Skin player1TroopSkin;
    public Skin player2TroopSkin; // La que elegiste para el enemigo
    public Skin cannonSkin;       // La que usarán ambos

    public MatchContext(Skin p1, Skin p2, Skin cannon) {
        this.player1TroopSkin = p1;
        this.player2TroopSkin = p2;
        this.cannonSkin = cannon;
    }
}