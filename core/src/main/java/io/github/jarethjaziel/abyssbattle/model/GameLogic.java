package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

import io.github.jarethjaziel.abyssbattle.util.GAME_STATE;

public class GameLogic {

    private List<Player> players;
    private Player currentPlayer;
    private boolean projectileActive;
    private GAME_STATE state;
    
    public GameLogic() {
        players = new ArrayList<>();
        projectileActive = false;
        state = GAME_STATE.INITIATED;
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void startGame() {
        if (players.size() < 2) return;

        currentPlayer = players.get(0);
        state = GAME_STATE.PLAYER_1_TURN;
    }

    public void onCollision(int a, int b) {
  
    }

    public boolean checkWinner() {

        Player p1 = players.get(0);
        Player p2 = players.get(1);

        boolean p1AllDead = p1.getTroopList().stream().allMatch(t -> !t.isActive());
        boolean p2AllDead = p2.getTroopList().stream().allMatch(t -> !t.isActive());

        if (p1AllDead && p2AllDead) {
            state = GAME_STATE.DRAW;
            return true;
        }

        if (p1AllDead) {
            state = GAME_STATE.PLAYER_2_WIN;
            return true;
        }

        if (p2AllDead) {
            state = GAME_STATE.PLAYER_1_WIN;
            return true;
        }

        return false;
    }

    public void changeTurn() {
        if (currentPlayer == players.get(0)) {
            currentPlayer = players.get(1);
            state = GAME_STATE.PLAYER_2_TURN;
        } else {
            currentPlayer = players.get(0);
            state = GAME_STATE.PLAYER_1_TURN;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GAME_STATE getState() {
        return state;
    }
}

