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

public class GameScreenRefactor implements Screen {

    private final AbyssBattle game;

    // --- COMPONENTES (MVC) ---
    private GameLogic gameLogic; // El CEREBRO (Física, Turnos, Reglas)
    private GameRenderer gameRenderer; // LA VISTA (Dibujo del mundo, Líneas, Explosiones)
    private GameHUD gameHUD; // LA UI (Botones, Textos, Menús de Pausa)
    private MapManager mapManager; // EL MAPA (Carga TMX, Crea Paredes)
    private InputController inputController; // EL CONTROL (Mouse/Touch)

    public GameScreenRefactor(AbyssBattle game) {
        this.game = game;
    }

    @Override
    public void show() {
        // 1. Inicializar Lógica (MODELO)
        // GameLogic crea el World de Box2D internamente
        gameLogic = new GameLogic();

        // 2. Inicializar Mapa
        // Le pasamos el World para que cree las paredes físicas
        mapManager = new MapManager(gameLogic.getWorld(), "maps/game_bg_1.tmx");

        // 3. Inicializar Renderer (VISTA JUEGO)
        // Necesita el batch, los assets, el mapa (para dibujarlo) y la lógica (para
        // saber qué dibujar)
        gameRenderer = new GameRenderer(game.batch, game.assets, mapManager, gameLogic);

        // 4. Inicializar HUD (VISTA UI)
        // Necesita assets y el juego principal para cambiar de pantalla (ej: Salir al
        // menú)
        gameHUD = new GameHUD(game.batch, game.assets, game);

        // 5. Inicializar Input (CONTROLADOR)
        // Conecta los dedos del usuario con la lógica y el HUD
        inputController = new InputController(gameLogic, gameRenderer.getViewport(), gameHUD, mapManager);

        // 6. Configurar el Multiplexer
        // IMPORTANTE: El HUD va primero para que los botones agarren el click antes que
        // el juego
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameHUD.getStage());
        multiplexer.addProcessor(inputController);
        Gdx.input.setInputProcessor(multiplexer);

        // 7. Arrancar Música (Opcional, usando un Manager)

        AudioManager.getInstance().playMusic("music/game_music.mp3");

        // Configuración inicial de la partida
        setupGameStart();
    }

    private void setupGameStart() {
        gameLogic.startGame();
        gameHUD.updateInfo(gameLogic.getState(), gameLogic.getTroopsToPlace()); // Actualizar texto inicial
    }

    @Override
    public void render(float delta) {
        // A. Limpiar Pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // B. Actualizar Lógica (Si no está pausado)
        if (!gameHUD.isPaused() && !gameLogic.isGameOver()) {
            gameLogic.update(delta);
            gameRenderer.updateCamera(delta); // Mover cámara si es necesario
        }

        // C. Verificar condiciones de fin de juego
        if (gameLogic.isGameOver()) {
            gameHUD.showGameOver(gameLogic.getState()); // Mostrar menú de fin
        }

        // D. Dibujar Juego (Fondo, Mapa, Tanques, Proyectiles)
        gameRenderer.render(delta);

        // E. Dibujar línea de apuntado (Solo si se está arrastrando)
        if (inputController.isDragging() && !gameHUD.isPaused()) {
            gameRenderer.drawAimLine(inputController.getDragStart(), inputController.getDragCurrent());
        }

        // F. Actualizar y Dibujar UI (HUD encima de todo)
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