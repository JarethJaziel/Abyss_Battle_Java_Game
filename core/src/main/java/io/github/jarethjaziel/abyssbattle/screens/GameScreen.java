package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.database.entities.Stats;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.systems.AccountManagerSystem;
import io.github.jarethjaziel.abyssbattle.database.systems.PlayerStatsSystem;
import io.github.jarethjaziel.abyssbattle.gameutil.input.InputController;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.AudioManager;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.MapManager;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.SessionManager;
import io.github.jarethjaziel.abyssbattle.gameutil.view.GameHUD;
import io.github.jarethjaziel.abyssbattle.gameutil.view.GameRenderer;
import io.github.jarethjaziel.abyssbattle.model.GameLogic;
import io.github.jarethjaziel.abyssbattle.model.MatchContext;
import io.github.jarethjaziel.abyssbattle.util.Constants;

/**
 * Pantalla principal del juego (Gameplay).
 * <p>
 * Actúa como el controlador principal (Orquestador) en el patrón MVC. Su responsabilidad es
 * inicializar y coordinar los diferentes subsistemas del juego: Lógica, Vista, Input y Persistencia.
 */
public class GameScreen implements Screen {

    private final AbyssBattle game;

    // --- COMPONENTES (MVC) ---
    /** Modelo: Gestiona la física, turnos y reglas del juego. */
    private GameLogic gameLogic; 
    
    /** Vista: Se encarga de dibujar el mundo del juego (mapa, entidades, proyectiles). */
    private GameRenderer gameRenderer; 
    
    /** Vista (UI): Gestiona la interfaz de usuario superpuesta (HUD, menús). */
    private GameHUD gameHUD; 
    
    /** Gestor del Mapa: Carga el nivel y genera las colisiones físicas. */
    private MapManager mapManager; 
    
    /** Controlador: Procesa la entrada del usuario y la traduce en acciones del juego. */
    private InputController inputController; 
    
    /** Contexto de la partida: Skins seleccionadas por los jugadores. */
    private MatchContext matchContext;

    private PlayerStatsSystem statsSystem; 
    private AccountManagerSystem accountSystem;

    /** Bandera para asegurar que las estadísticas se guarden una única vez al terminar la partida. */
    private boolean hasSavedStats = false;

    /**
     * Constructor de la pantalla de juego.
     * @param game Instancia principal del juego.
     * @param context Configuración de la partida (skins, etc.).
     */
    public GameScreen(AbyssBattle game, MatchContext context) {
        this.game = game;
        this.matchContext = context;
    }

    /**
     * Se llama cuando esta pantalla se convierte en la pantalla actual.
     * Inicializa todos los componentes y prepara la partida.
     */
    @Override
    public void show() {
        gameLogic = new GameLogic();

        mapManager = new MapManager(gameLogic.getWorld(), "maps/game_bg_1.tmx");

        gameRenderer = new GameRenderer(game.batch, game.assets, mapManager, gameLogic, matchContext);

        gameHUD = new GameHUD(game.batch, game);

        inputController = new InputController(gameLogic, gameRenderer.getViewport(), gameHUD, mapManager);

        statsSystem = new PlayerStatsSystem(game.getDbManager());
        accountSystem = new AccountManagerSystem(game.getDbManager());

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gameHUD.getOverlayStage());
        multiplexer.addProcessor(inputController);
        Gdx.input.setInputProcessor(multiplexer);

        AudioManager.getInstance().playMusic("music/game_music.mp3");

        setupGameStart();
    }

    /**
     * Configura el estado inicial de la lógica y la UI.
     */
    private void setupGameStart() {
        gameLogic.startGame();
        gameHUD.updateInfo(gameLogic.getState(), gameLogic.getTroopsToPlace()); // Actualizar texto inicial
    }

    /**
     * Ciclo principal de renderizado y actualización (Game Loop).
     * @param delta Tiempo en segundos desde el último frame.
     */
    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!gameHUD.isPaused() && !gameLogic.isGameOver()) {
            gameLogic.update(delta);
            gameRenderer.updateCamera(delta);
        }

        if (gameLogic.isGameOver()) {
            if (!hasSavedStats) {
                saveGameResults(); // <--- AQUÍ OCURRE LA MAGIA
                hasSavedStats = true; // Cerramos el candado
            }
            gameHUD.showGameOver(gameLogic.getState(), Constants.REWARD_PER_GAME);
        }

        gameRenderer.render(delta);

        if (inputController.isDragging() && !gameHUD.isPaused()) {
            gameRenderer.drawAimLine(inputController.getDragStart(), inputController.getDragCurrent());
        }

        gameHUD.updateInfo(gameLogic.getState(), gameLogic.getTroopsToPlace());
        gameHUD.render(delta);
    }

    /**
     * Guarda las estadísticas de la sesión en la base de datos y otorga recompensas.
     */
    private void saveGameResults() {
        User currentUser = SessionManager.getInstance().getCurrentUser();

        if (currentUser == null) {
            return;
        }

        Stats sessionStats = gameLogic.getMatchStats();

        statsSystem.updateStatsAfterMatch(currentUser, sessionStats);

        currentUser.addCoins(Constants.REWARD_PER_GAME);

        accountSystem.updateUser(currentUser);

        SessionManager.getInstance().login(currentUser);

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
        //resumes game
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