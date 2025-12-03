package io.github.jarethjaziel.abyssbattle.model;

import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
/**
 * DTO (Data Transfer Object) que encapsula la configuración visual de una partida.
 * <p>
 * Se utiliza para transferir las selecciones de personalización (Skins) desde la pantalla
 * de configuración ({@code GameSetupScreen}) hacia la pantalla de juego ({@code GameScreen})
 * y sus renderizadores.
 */
public class MatchContext {

    /** Skin seleccionada para las tropas del Jugador 1 (Usuario actual). */
    private Skin player1TroopSkin;

    /** Skin seleccionada para las tropas del Jugador 2 (Enemigo/CPU). */
    private Skin player2TroopSkin;

    /** Skin seleccionada para los cañones (compartida por ambos jugadores en esta versión). */
    private Skin cannonSkin;

    /**
     * Construye un nuevo contexto de partida con las skins definidas.
     *
     * @param p1     La skin de tropa para el Jugador 1.
     * @param p2     La skin de tropa para el Jugador 2.
     * @param cannon La skin de cañón a utilizar en el mapa.
     */
    public MatchContext(Skin p1, Skin p2, Skin cannon) {
        this.player1TroopSkin = p1;
        this.player2TroopSkin = p2;
        this.cannonSkin = cannon;
    }

    public Skin getPlayer1TroopSkin() {
        return player1TroopSkin;
    }

    public Skin getPlayer2TroopSkin() {
        return player2TroopSkin;
    }

    public Skin getCannonSkin() {
        return cannonSkin;
    }
}