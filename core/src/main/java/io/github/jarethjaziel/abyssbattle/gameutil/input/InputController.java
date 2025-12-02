package io.github.jarethjaziel.abyssbattle.gameutil.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.jarethjaziel.abyssbattle.gameutil.manager.AudioManager;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.MapManager;
import io.github.jarethjaziel.abyssbattle.gameutil.view.GameHUD;
import io.github.jarethjaziel.abyssbattle.model.Cannon;
import io.github.jarethjaziel.abyssbattle.model.GameLogic;
import io.github.jarethjaziel.abyssbattle.model.Player;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

public class InputController extends InputAdapter {

    private final GameLogic logic;
    private final Viewport viewport;
    private final GameHUD hud;
    private final MapManager mapManager;

    // Drag Variables
    private boolean isDragging = false;
    private final Vector2 dragStart = new Vector2();
    private final Vector2 dragCurrent = new Vector2();

    public InputController(GameLogic logic, Viewport viewport, GameHUD hud, MapManager mapManager) {
        this.logic = logic;
        this.viewport = viewport;
        this.hud = hud;
        this.mapManager = mapManager;
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) {
            hud.togglePause(!hud.isPaused());
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (hud.isPaused() || logic.isGameOver())
            return false;

        Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));
        GameState state = logic.getState();

        // 1. Colocación de Tropas
        if (state == GameState.PLACEMENT_P1 || state == GameState.PLACEMENT_P2) {
            if (mapManager.isValidPlacement(worldCoords.x, worldCoords.y)) {
                logic.tryPlaceTroop(worldCoords.x, worldCoords.y);
                return true;
            }
            return false;
        }

        // 2. Disparo (Detectar click cerca del cañón)
        if (state != GameState.PLAYER_1_TURN && state != GameState.PLAYER_2_TURN)
            return false;

        Player currentPlayer = logic.getCurrentPlayer();
        Cannon cannon = currentPlayer.getCannon();

        float cannonX = cannon.getPosX() * Constants.PIXELS_PER_METER;
        float cannonY = cannon.getPosY() * Constants.PIXELS_PER_METER;

        float dist = Vector2.dst(worldCoords.x, worldCoords.y, cannonX, cannonY);

        if (dist < 50) {
            isDragging = true;
            dragStart.set(cannonX, cannonY);
            dragCurrent.set(worldCoords.x, worldCoords.y);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (!isDragging)
            return false;
        Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));
        dragCurrent.set(worldCoords);

        // Calcular ángulo y mandar a logic
        Vector2 force = new Vector2(dragStart).sub(dragCurrent);
        logic.playerAim(force.angleDeg());
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (!isDragging)
            return false;

        isDragging = false;

        float distance = dragStart.dst(dragCurrent);
        Gdx.app.log("DISTANCE", ""+distance);

        float power = (distance / Constants.MAX_DRAG_DISTANCE) * 100;
        Gdx.app.log("POWER",""+ power);
        if (power < Constants.AIM_DEADZONE) {
            return true;
        }

        if (power < Constants.MIN_AIM_POWER) {
            power = Constants.MIN_AIM_POWER;
        }

        if (power > Constants.MAX_AIM_POWER) {
            power = Constants.MAX_AIM_POWER;
        }
        Gdx.app.log("POWER",""+ power);


        logic.playerShoot(power);
        AudioManager.getInstance().playSound("sfx/boom.mp3");

        return true;
    }

    public boolean isDragging() {
        return isDragging;
    }

    public Vector2 getDragStart() {
        return dragStart;
    }

    public Vector2 getDragCurrent() {
        return dragCurrent;
    }
}
