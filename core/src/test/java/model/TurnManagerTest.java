package model;

import io.github.jarethjaziel.abyssbattle.model.Player;
import io.github.jarethjaziel.abyssbattle.model.TurnManager;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TurnManagerTest {

    private Player p1, p2;
    private TurnManager tm;

    @BeforeEach
    void setUp() {
        p1 = mock(Player.class);
        p2 = mock(Player.class);

        when(p1.getId()).thenReturn(1);
        when(p2.getId()).thenReturn(2);

        tm = new TurnManager(Arrays.asList(p1, p2));
    }

    @Test
    void startPlacementPhase_initializesP1Placement() {
        tm.startPlacementPhase();

        assertEquals(GameState.PLACEMENT_P1, tm.getState());
        assertEquals(p1, tm.getCurrentPlayer());
        assertEquals(Constants.MAX_PLAYER_TROOPS, tm.getTroopsToPlace());
    }

    @Test
    void decreaseTroopsToPlace_advancesToP2Placement() {
        tm.startPlacementPhase();

        for (int i = 0; i < Constants.MAX_PLAYER_TROOPS; i++) {
            tm.decreaseTroopsToPlace();
        }

        assertEquals(GameState.PLACEMENT_P2, tm.getState());
        assertEquals(p2, tm.getCurrentPlayer());
    }

    @Test
    void placementP2_completesAndStartsCombat() {
        tm.startPlacementPhase();

        for (int i = 0; i < Constants.MAX_PLAYER_TROOPS; i++) tm.decreaseTroopsToPlace();
        for (int i = 0; i < Constants.MAX_PLAYER_TROOPS; i++) tm.decreaseTroopsToPlace();

        assertEquals(GameState.PLAYER_1_TURN, tm.getState());
        assertEquals(p1, tm.getCurrentPlayer());
    }

    @Test
    void handleTurnEnd_grantsBonusTurnWhenDestroyingTroop() {
        tm.startPlacementPhase();
        tm.setState(GameState.PLAYER_1_TURN);

        tm.handleTurnEnd(true);

        assertEquals(GameState.PLAYER_1_TURN, tm.getState());
        assertEquals(p1, tm.getCurrentPlayer());
    }

    @Test
    void handleTurnEnd_startsTransitionWhenNoHit() {
        tm.startPlacementPhase();
        tm.setState(GameState.PLAYER_1_TURN);

        tm.handleTurnEnd(false);

        assertEquals(GameState.TURN_TRANSITION, tm.getState());
    }

    @Test
    void update_whenTransitionFinishes_advancesTurn() {
        tm.startPlacementPhase();
        tm.setState(GameState.PLAYER_1_TURN);

        tm.handleTurnEnd(false);
        assertEquals(GameState.TURN_TRANSITION, tm.getState());

        tm.update(Constants.TRANSITION_TIME_TO_WAIT + 0.1f);

        assertEquals(GameState.PLAYER_2_TURN, tm.getState());
        assertEquals(p2, tm.getCurrentPlayer());
    }

    @Test
    void activateLastChance_setsCorrectState() {
        tm.activateLastChance();

        assertEquals(GameState.LAST_CHANCE, tm.getState());
        assertEquals(p2, tm.getCurrentPlayer());
    }

    @Test
    void handleTurnEnd_inLastChance_marksFlag() {
        tm.activateLastChance();

        tm.handleTurnEnd(false);

        assertTrue(tm.isLastChanceUsed());
    }

    @Test
    void getEnemyPlayer_returnsOppositePlayer() {
        tm.startPlacementPhase();

        // Forzar estado de combate
        for (int i = 0; i < Constants.MAX_PLAYER_TROOPS; i++) tm.decreaseTroopsToPlace();
        for (int i = 0; i < Constants.MAX_PLAYER_TROOPS; i++) tm.decreaseTroopsToPlace();

        assertEquals(GameState.PLAYER_1_TURN, tm.getState());
        assertEquals(p2, tm.getEnemyPlayer());

        // Cambiar turno de forma natural
        tm.handleTurnEnd(false);
        tm.update(Constants.TRANSITION_TIME_TO_WAIT + 0.1f);

        assertEquals(GameState.PLAYER_2_TURN, tm.getState());
        assertEquals(p1, tm.getEnemyPlayer());
    }


    @Test
    void setWaitingState_setsState() {
        tm.setWaitingState();
        assertEquals(GameState.WAITING, tm.getState());
    }
}
