package io.github.jarethjaziel.abyssbattle.model;

import java.util.List;

import com.badlogic.gdx.Gdx;

import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

/**
 * Gestor responsable del flujo de turnos y estados de la partida.
 * <p>
 * Controla quién está jugando, gestiona los temporizadores de transición
 * y administra las fases especiales como la colocación inicial o la "Última
 * Oportunidad".
 */
public class TurnManager {

    private static final String TAG = TurnManager.class.getSimpleName();

    private GameState state;
    private Player currentPlayer;
    private final List<Player> players;

    // Timer & Counters
    private float turnTimer = 0f;
    private int troopsToPlace = 0;

    /** Indica si el jugador ya gastó su tiro final fallido en Last Chance. */
    private boolean lastChanceUsed = false;

    /**
     * Indica si el modo Last Chance está activo (independiente del estado WAITING).
     */
    private boolean lastChanceActive = false;

    /**
     * Recibe
     * 
     * @param players la lista de jugadores para manejar los turnos.
     */
    public TurnManager(List<Player> players) {
        this.players = players;
        this.state = GameState.INITIATED;
    }

    /**
     * Inicia la fase de configuración donde el J1 coloca sus tropas.
     */
    public void startPlacementPhase() {
        this.currentPlayer = players.get(0);
        this.state = GameState.PLACEMENT_P1;
        this.troopsToPlace = Constants.MAX_PLAYER_TROOPS;
    }

    /**
     * Actualiza el temporizador de transición de turnos.
     * 
     * @param delta Tiempo transcurrido.
     */
    public void update(float delta) {
        if (state == GameState.TURN_TRANSITION) {
            turnTimer -= delta;
            if (turnTimer <= 0) {
                advanceTurnStandard();
            }
        }
    }

    /**
     * Decide qué acción tomar después de que un proyectil termina su trayectoria.
     * Maneja la lógica de bonificación por impacto y la mecánica de Last Chance.
     * * @param troopDestroyed {@code true} si el disparo destruyó una tropa.
     */
    public void handleTurnEnd(boolean troopDestroyed) {
        if (lastChanceActive) {

            if (troopDestroyed) {
                Gdx.app.log(TAG, "¡LAST CHANCE: Enemigo abatido! TIRO EXTRA.");
                state = GameState.LAST_CHANCE;
            } else {
                Gdx.app.log(TAG, "¡LAST CHANCE: Tiro fallado! Fin del juego.");
                lastChanceUsed = true;
            }
            return;
        }

        if (troopDestroyed) {
            Gdx.app.log(TAG, "¡Objetivo destruido! Tiro de bonificación.");
            state = (currentPlayer.getId() == 1) ? GameState.PLAYER_1_TURN : GameState.PLAYER_2_TURN;
        } else {
            startTransitionTimer();
        }
    }

    /**
     * Cambia el turno al siguiente jugador de forma estándar.
     */
    private void advanceTurnStandard() {
        if (currentPlayer == players.get(0)) {
            currentPlayer = players.get(1);
            state = GameState.PLAYER_2_TURN;
        } else {
            currentPlayer = players.get(0);
            state = GameState.PLAYER_1_TURN;
        }
    }

    /**
     * Activa el modo especial donde el Jugador 2 tiene oportunidad de
     * empatar/ganar.
     */
    public void activateLastChance() {
        Gdx.app.log(TAG, "¡Activando ÚLTIMA OPORTUNIDAD para Player 2!");
        this.state = GameState.LAST_CHANCE;
        this.currentPlayer = players.get(1);
        this.lastChanceActive = true;
    }

    /**
     * Avanza el flujo de la fase de preparación (P1 -> P2 -> Combate).
     */
    public void advancePlacementPhase() {
        if (state == GameState.PLACEMENT_P1) {
            state = GameState.PLACEMENT_P2;
            currentPlayer = players.get(1);
            troopsToPlace = Constants.MAX_PLAYER_TROOPS;
            Gdx.app.log(TAG, "Fase de Colocación: JUGADOR 2");
        } else if (state == GameState.PLACEMENT_P2) {
            state = GameState.PLAYER_1_TURN;
            currentPlayer = players.get(0);
            Gdx.app.log(TAG, "¡Colocación terminada! INICIA EL COMBATE");
        }
    }

    /**
     * Reduce el contador de tropas por colocar y avanza fase si llega a 0.
     */
    public void decreaseTroopsToPlace() {
        troopsToPlace--;
        if (troopsToPlace <= 0)
            advancePlacementPhase();
    }

    public void setWaitingState() {
        this.state = GameState.WAITING;
    }

    private void startTransitionTimer() {
        this.state = GameState.TURN_TRANSITION;
        this.turnTimer = Constants.TRANSITION_TIME_TO_WAIT;
    }

    /**
     * Obtiene el jugador contrario al que tiene el turno actual.
     * @return Objeto Player enemigo.
     */
    public Player getEnemyPlayer() {
        return (currentPlayer == players.get(0)) ? players.get(1) : players.get(0);
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getTroopsToPlace() {
        return troopsToPlace;
    }

    public boolean isLastChanceUsed() {
        return lastChanceUsed;
    }
}