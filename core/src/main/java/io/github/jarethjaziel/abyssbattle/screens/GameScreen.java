package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.gameutil.input.InputController;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.AudioManager;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.MapManager;
import io.github.jarethjaziel.abyssbattle.gameutil.view.GameHUD;
import io.github.jarethjaziel.abyssbattle.gameutil.view.GameRenderer;
import io.github.jarethjaziel.abyssbattle.model.GameLogic;

public class GameScreen implements Screen {

    private final AbyssBattle game;

    // --- COMPONENTES (MVC) ---
    private GameLogic gameLogic; // El CEREBRO (Física, Turnos, Reglas)
    private GameRenderer gameRenderer; // LA VISTA (Dibujo del mundo, Líneas, Explosiones)
    private GameHUD gameHUD; // LA UI (Botones, Textos, Menús de Pausa)
    private MapManager mapManager; // EL MAPA (Carga TMX, Crea Paredes)
    private InputController inputController; // EL CONTROL (Mouse/Touch)

    public GameScreen(AbyssBattle game) {
        this.game = game;
    }

    @Override
    public void show() {
        gameLogic = new GameLogic();

        mapManager = new MapManager(gameLogic.getWorld(), "maps/game_bg_1.tmx");

        gameRenderer = new GameRenderer(game.batch, game.assets, mapManager, gameLogic);

        gameHUD = new GameHUD(game.batch, game.assets, game);

        inputController = new InputController(gameLogic, gameRenderer.getViewport(), gameHUD, mapManager);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameHUD.getStage());
        multiplexer.addProcessor(inputController);
        Gdx.input.setInputProcessor(multiplexer);

        AudioManager.getInstance().playMusic("music/game_music.mp3");

        setupGameStart();
    }

    private void setupGameStart() {
        gameLogic.startGame();
        gameHUD.updateInfo(gameLogic.getState(), gameLogic.getTroopsToPlace()); // Actualizar texto inicial
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!gameHUD.isPaused() && !gameLogic.isGameOver()) {
            gameLogic.update(delta);
            gameRenderer.updateCamera(delta);
        }

        if (gameLogic.isGameOver()) {
            gameHUD.showGameOver(gameLogic.getState());
        }

        gameRenderer.render(delta);

        if (inputController.isDragging() && !gameHUD.isPaused()) {
            gameRenderer.drawAimLine(inputController.getDragStart(), inputController.getDragCurrent());
        }

        gameHUD.updateInfo(gameLogic.getState(), gameLogic.getTroopsToPlace());
        gameHUD.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        gameRenderer.resize(width, height);
        gameHUD.resize(width, height);
    }

    @Override
    public void pause() {
        gameHUD.togglePause(true); // Auto-pausar si la app se minimiza
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        // Delegamos la limpieza a cada componente responsable
        if (gameLogic != null)
            gameLogic.dispose(); // Dispose World
        if (gameRenderer != null)
            gameRenderer.dispose();
        if (gameHUD != null)
            gameHUD.dispose();
        if (mapManager != null)
            mapManager.dispose();

        // No hacemos dispose de 'game' ni 'assets' porque son globales
    }
}