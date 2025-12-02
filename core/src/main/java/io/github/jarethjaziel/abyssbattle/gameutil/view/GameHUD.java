package io.github.jarethjaziel.abyssbattle.gameutil.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
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
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextButton.VisTextButtonStyle;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.screens.GameScreen;
import io.github.jarethjaziel.abyssbattle.screens.MainMenuScreen;
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
        gameOverMenu = new VisTable();
        gameOverMenu.setFillParent(true);
        gameOverMenu.setVisible(false);
        addBackground(gameOverMenu);

        LabelStyle titleStyle = new LabelStyle(new BitmapFont(), Color.GOLD);
        Label title = new Label("FIN DEL JUEGO", titleStyle);
        title.setName("gameOverTitle");
        gameOverMenu.add(title).padBottom(40);
        gameOverMenu.row();

        // Estilo de botones
        VisTextButton.VisTextButtonStyle buttonStyle = createButtonStyle();

        // Botón Jugar de Nuevo
        VisTextButton playAgainButton = new VisTextButton("Jugar de Nuevo", buttonStyle);
        playAgainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });
        gameOverMenu.add(playAgainButton).width(300).height(60).pad(10);
        gameOverMenu.row();

        // Botón Menú Principal
        VisTextButton menuButton = new VisTextButton("Menu Principal", buttonStyle);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
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
            VisUI.getSkin().get("default", VisTextButtonStyle.class)
        );

        style.font = new BitmapFont();
        style.font.getData().setScale(1.5f);
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.YELLOW;

        style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("34495EFF"));
        style.over = VisUI.getSkin().newDrawable("white", Color.valueOf("1ABC9CFF"));
        style.down = VisUI.getSkin().newDrawable("white", Color.valueOf("2ECC71FF"));

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

    public Stage getStage() { return overlayStage; } // Para input processor (prioridad botones)
    public boolean isPaused() { return isPaused; }

    public void showGameOver(GameState state) {
        Label title = gameOverMenu.findActor("gameOverTitle");
        if (title != null)
            title.setText("VICTORIA: " + state);
        gameOverMenu.setVisible(true);
    }

    public void togglePause(boolean paused) {
        this.isPaused = paused;
        pauseMenu.setVisible(paused);
    }

    @Override
    public void dispose() {
        stage.dispose();
        overlayStage.dispose();
    }

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
