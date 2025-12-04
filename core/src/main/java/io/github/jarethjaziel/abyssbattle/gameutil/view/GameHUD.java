package io.github.jarethjaziel.abyssbattle.gameutil.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSlider;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.AudioManager;
import io.github.jarethjaziel.abyssbattle.screens.GameSetupScreen;
import io.github.jarethjaziel.abyssbattle.screens.MainMenuScreen;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;


/**
 * Heads-Up Display (HUD) y Gestor de UI superpuesta para el juego.
 * <p>
 * Esta clase maneja todos los elementos de interfaz gráfica que se dibujan sobre el mundo del juego,
 * incluyendo:
 * <ul>
 * <li>Etiquetas de estado (turno actual, mensajes).</li>
 * <li>Menú de Pausa (con control de volumen).</li>
 * <li>Pantalla de Fin de Juego (GameOver) con resumen de recompensas.</li>
 * </ul>
 */
public class GameHUD implements Disposable {

    private static final String TAG = GameHUD.class.getSimpleName();

    private final Stage stage;
    private final Stage overlayStage;
    private final AbyssBattle game;
    private Label statusLabel;

    private VisTable pauseMenu;
    private VisTable gameOverMenu;
    private boolean isPaused = false;

    /**
     * Constructor del HUD. Inicializa los stages y construye los menús.
     *
     * @param batch  SpriteBatch compartido para renderizar.
     * @param assets Gestor de assets (no utilizado directamente aquí, pero útil para consistencia).
     * @param game   Referencia al juego principal para la navegación entre pantallas.
     */
    public GameHUD(SpriteBatch batch, AbyssBattle game) {
        this.game = game;

        stage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), batch);
        createStatusUI();

        overlayStage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT), batch);
        createPauseMenu();
        createGameOverMenu();
    }

    private void createGameOverMenu() {
        gameOverMenu = new VisTable();
        gameOverMenu.setFillParent(true);
        gameOverMenu.setVisible(false);
        addBackground(gameOverMenu);

        LabelStyle titleStyle = new LabelStyle(new BitmapFont(), Color.GOLD);
        Label title = new Label("FIN DEL JUEGO", titleStyle);
        title.setName("gameOverTitle");
        gameOverMenu.add(title).padBottom(40);
        gameOverMenu.row();

        LabelStyle coinStyle = new LabelStyle(new BitmapFont(), Color.YELLOW);
        Label coinsLabel = new Label("", coinStyle); // Texto vacío por defecto
        coinsLabel.setName("coinsLabel"); // Nombre clave para buscarlo luego
        coinsLabel.setFontScale(1.5f);
        gameOverMenu.add(coinsLabel).padBottom(30);
        gameOverMenu.row();

        // Estilo de botones
        VisTextButton.VisTextButtonStyle buttonStyle = createButtonStyle();

        // Botón Jugar de Nuevo
        VisTextButton playAgainButton = new VisTextButton("Jugar de Nuevo", buttonStyle);
        playAgainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameSetupScreen(game));
            }
        });
        gameOverMenu.add(playAgainButton).width(300).height(60).pad(10);
        gameOverMenu.row();

        // Botón Menú Principal
        VisTextButton menuButton = new VisTextButton("Menu Principal", buttonStyle);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().stopMusic();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        gameOverMenu.add(menuButton).width(300).height(60).pad(10);
        gameOverMenu.row();

        // Botón Salir
        VisTextButton exitButton = new VisTextButton("Salir del Juego", buttonStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        gameOverMenu.add(exitButton).width(300).height(60).pad(10);

        overlayStage.addActor(gameOverMenu);
    }

    private void createPauseMenu() {
        pauseMenu = new VisTable();
        pauseMenu.setFillParent(true);
        pauseMenu.setVisible(false);
        // Título
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.YELLOW);
        Label title = new Label("PAUSA", titleStyle);
        pauseMenu.add(title).padBottom(40);
        pauseMenu.row();

        // Etiqueta "Música"
        Label musicLabel = new Label("Volumen Música", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        pauseMenu.add(musicLabel).padBottom(5);
        pauseMenu.row();

        // Slider (Min: 0, Max: 1, Step: 0.1, Vertical: false)
        VisSlider musicSlider = new VisSlider(0f, 1f, 0.05f, false);

        musicSlider.setValue(AudioManager.getInstance().getVolume()); // Valor por defecto si no tienes el getter

        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float newVolume = musicSlider.getValue();
                // Actualizar volumen en tiempo real
                AudioManager.getInstance().setMusicVolume(newVolume);
            }
        });

        // Añadir slider a la tabla (Ancho de 300px para que se vea bien)
        pauseMenu.add(musicSlider).width(300).padBottom(30);
        pauseMenu.row();

        // Estilo de botones
        VisTextButtonStyle buttonStyle = createButtonStyle();

        // Botón Continuar
        VisTextButton resumeButton = new VisTextButton("Continuar", buttonStyle);
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                togglePause(false);
            }
        });
        pauseMenu.add(resumeButton).width(300).height(60).pad(10);
        pauseMenu.row();

        // Botón Menú Principal
        VisTextButton menuButton = new VisTextButton("Menu Principal", buttonStyle);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                AudioManager.getInstance().stopMusic();
                game.setScreen(new MainMenuScreen(game));
            }
        });
        pauseMenu.add(menuButton).width(300).height(60).pad(10);
        pauseMenu.row();

        // Botón Salir
        VisTextButton exitButton = new VisTextButton("Salir del Juego", buttonStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        pauseMenu.add(exitButton).width(300).height(60).pad(10);

        overlayStage.addActor(pauseMenu);
        addBackground(pauseMenu);

    }

    private VisTextButtonStyle createButtonStyle() {
        VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButtonStyle.class));

        style.font = new BitmapFont();
        style.font.getData().setScale(1.5f);
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.YELLOW;
        final String color = "white";
        style.up = VisUI.getSkin().newDrawable(color, Color.valueOf("34495EFF"));
        style.over = VisUI.getSkin().newDrawable(color, Color.valueOf("1ABC9CFF"));
        style.down = VisUI.getSkin().newDrawable(color, Color.valueOf("2ECC71FF"));

        return style;
    }

    private void addBackground(Table table) {
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0, 0, 0, 0.8f));
        bgPixmap.fill();
        table.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap))));
        bgPixmap.dispose();
    }

    private void createStatusUI() {
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        statusLabel = new Label("Iniciando...", labelStyle);
        statusLabel.setFontScale(2);

        Table table = new Table();
        table.bottom().padBottom(20).setFillParent(true);
        table.add(statusLabel).colspan(3);
        stage.addActor(table);
    }

    public Stage getOverlayStage() {
        return overlayStage;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void showGameOver(GameState state, int coinsEarned) {
        Label title = gameOverMenu.findActor("gameOverTitle");
        if (title != null)
            title.setText("Result: " + state);
        Label coinsLabel = gameOverMenu.findActor("coinsLabel");
        if (coinsLabel != null) {
            if (coinsEarned > 0) {
                coinsLabel.setText("+" + coinsEarned + " Monedas");
                coinsLabel.setVisible(true);
            } else {
                coinsLabel.setVisible(false); // Ocultar si no ganó nada (ej. invitado)
            }
        }
        gameOverMenu.setVisible(true);
    }

    /**
     * Alterna el estado de pausa del juego.
     * Gestiona automáticamente la música de fondo.
     *
     * @param paused {@code true} para pausar, {@code false} para reanudar.
     */
    public void togglePause(boolean paused) {
        this.isPaused = paused;
        pauseMenu.setVisible(paused);
        if (paused) {
            AudioManager.getInstance().pauseMusic();
            Gdx.app.log(TAG, "Juego Pausado");
        } else {
            AudioManager.getInstance().resumeMusic();
            Gdx.app.log(TAG, "Juego Reanudado");
        }
    }

    @Override
    public void dispose() {
        stage.dispose();
        overlayStage.dispose();
    }

    /**
     * Actualiza la etiqueta de información inferior según el estado actual de la lógica.
     *
     * @param state         Estado actual del juego.
     * @param troopsToPlace Cantidad de tropas restantes por colocar (si aplica).
     */
    public void updateInfo(GameState state, int troopsToPlace) {
        if (state == GameState.WAITING) {
            statusLabel.setText("Proyectil en el aire...");
            statusLabel.setColor(Color.YELLOW);
        } else if (state == GameState.PLAYER_1_TURN) {
            statusLabel.setText("TURNO JUGADOR 1 (Abajo)");
            statusLabel.setColor(Color.CYAN);
        } else if (state == GameState.PLAYER_2_TURN) {
            statusLabel.setText("TURNO JUGADOR 2 (Arriba)");
            statusLabel.setColor(Color.RED);
        } else if (state == GameState.PLAYER_1_WIN) {
            statusLabel.setText("¡GANÓ JUGADOR 1!");
            statusLabel.setColor(Color.GREEN);
        } else if (state == GameState.PLAYER_2_WIN) {
            statusLabel.setText("¡GANÓ JUGADOR 2!");
            statusLabel.setColor(Color.GREEN);
        } else if (state == GameState.DRAW) {
            statusLabel.setText("¡EMPATE!");
            statusLabel.setColor(Color.YELLOW);
        } else if (state == GameState.TURN_TRANSITION) {
            statusLabel.setText("¡IMPACTO!");
            statusLabel.setColor(Color.CORAL);
        } else if (state == GameState.PLACEMENT_P1) {
            statusLabel.setText("COLOCA TUS TROPAS, JUGADOR 1: " +
                    "(" + troopsToPlace + " restantes)");
            statusLabel.setColor(Color.DARK_GRAY);
        } else if (state == GameState.PLACEMENT_P2) {
            statusLabel.setText("COLOCA TUS TROPAS, JUGADOR 2: " +
                    "(" + troopsToPlace + " restantes)");
            statusLabel.setColor(Color.DARK_GRAY);
        } else if (state == GameState.LAST_CHANCE) {
            statusLabel.setText("¡ÚLTIMA OPORTUNIDAD JUGADOR 2!");
            statusLabel.setColor(Color.BLACK);
        }
    }

    public void render(float delta) {
        stage.act(delta);
        stage.draw();

        if (isPaused || gameOverMenu.isVisible()) {
            overlayStage.act(delta);
            overlayStage.draw();
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        overlayStage.getViewport().update(width, height, true);
    }

}
