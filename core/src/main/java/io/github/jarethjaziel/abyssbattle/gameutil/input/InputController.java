package io.github.jarethjaziel.abyssbattle.gameutil.input;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.jarethjaziel.abyssbattle.gameutil.manager.MapManager;
import io.github.jarethjaziel.abyssbattle.gameutil.view.GameHUD;
import io.github.jarethjaziel.abyssbattle.model.GameLogic;

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
