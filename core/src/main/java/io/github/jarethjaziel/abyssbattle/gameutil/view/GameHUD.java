package io.github.jarethjaziel.abyssbattle.gameutil.view;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.widget.VisTable;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

public class GameHUD implements Disposable {

    private final Stage stage;
    private final Stage overlayStage;
    private final AbyssBattle game;
    private Label statusLabel;
    
    private VisTable pauseMenu;
    private VisTable gameOverMenu;
    private boolean isPaused = false;

    public GameHUD(SpriteBatch batch, AssetManager assets, AbyssBattle game) {
        this.game = game;
        
        // 1. Stage Principal (Información)
        stage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), batch);
        createStatusUI();

        // 2. Stage Overlay (Pausa/Menu)
        overlayStage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), batch);
        createPauseMenu();
        createGameOverMenu();
    }

    private void createGameOverMenu() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createGameOverMenu'");
    }

    private void createPauseMenu() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createPauseMenu'");
    }

    private void createStatusUI() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createStatusUI'");
    }

    public Stage getStage() {
        return stage;
    }

    public void updateStatusLabel(GameState state) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateStatusLabel'");
    }

    public boolean isPaused() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isPaused'");
    }

    public void showGameOver(GameState state) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'showGameOver'");
    }

    public void togglePause(boolean b) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'togglePause'");
    }


    @Override
    public void dispose() {
        stage.dispose();
        overlayStage.dispose();
    }

}
