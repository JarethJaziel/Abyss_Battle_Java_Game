package io.github.jarethjaziel.abyssbattle.model;

import java.util.List;

import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;


public class TurnManager {

    private GameState state;
    private Player currentPlayer;
    private final List<Player> players;
    
    // Timer & Counters
    private float turnTimer = 0f;
    private int troopsToPlace = 0;
    
    // Flags
    private boolean lastChanceUsed = false;

    public TurnManager(List<Player> players) {
        this.players = players;
        this.state = GameState.INITIATED;
    }

    public void startPlacementPhase() {
        this.currentPlayer = players.get(0);
        this.state = GameState.PLACEMENT_P1;
        this.troopsToPlace = Constants.MAX_PLAYER_TROOPS;
    }

    public void update(float delta) {
        if (state == GameState.TURN_TRANSITION) {
            turnTimer -= delta;
            if (turnTimer <= 0) {
                advanceTurnStandard();
            }
        }
    }

    /**
     * Decides what to do after a shot lands.
     */
    public void handleTurnEnd(boolean troopDestroyed) {
        if (state == GameState.LAST_CHANCE) {
            lastChanceUsed = true;
            // The GameLogic will check for win/loss immediately after this
            return; 
        }

        // Bonus Turn Logic
        if (troopDestroyed) {
            System.out.println("Target Destroyed! Bonus Turn.");
            // Keep current player, just reset state to allow shooting
            state = (currentPlayer.getId() == 1) ? GameState.PLAYER_1_TURN : GameState.PLAYER_2_TURN;
        } else {
            // Normal turn change with delay
            startTransitionTimer();
        }
    }

    private void advanceTurnStandard() {
        if (currentPlayer == players.get(0)) {
            currentPlayer = players.get(1);
            state = GameState.PLAYER_2_TURN;
        } else {
            currentPlayer = players.get(0);
            state = GameState.PLAYER_1_TURN;
        }
    }

    public void activateLastChance() {
        System.out.println("Last Chance Activated for Player 2!");
        this.state = GameState.LAST_CHANCE;
        this.currentPlayer = players.get(1);
    }

    public void advancePlacementPhase() {
        if (state == GameState.PLACEMENT_P1) {
            state = GameState.PLACEMENT_P2;
            currentPlayer = players.get(1);
            troopsToPlace = Constants.MAX_PLAYER_TROOPS;
        } else if (state == GameState.PLACEMENT_P2) {
            state = GameState.PLAYER_1_TURN;
            currentPlayer = players.get(0);
            System.out.println("Combat Started!");
        }
    }
    
    public void decreaseTroopsToPlace() {
        troopsToPlace--;
        if (troopsToPlace <= 0) advancePlacementPhase();
    }

    public void setWaitingState() { this.state = GameState.WAITING; }

    private void startTransitionTimer() {
        this.state = GameState.TURN_TRANSITION;
        this.turnTimer = Constants.TRANSITION_TIME_TO_WAIT;
    }
    
    // --- Getters ---
    public Player getEnemyPlayer() {
        return (currentPlayer == players.get(0)) ? players.get(1) : players.get(0);
    }
    public GameState getState() { return state; }
    public void setState(GameState state) { this.state = state; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public int getTroopsToPlace() { return troopsToPlace; }
    public boolean isLastChanceUsed() { return lastChanceUsed; }
}