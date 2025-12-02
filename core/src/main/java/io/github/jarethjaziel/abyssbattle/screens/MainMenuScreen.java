package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.util.Constants;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.SessionManager;

public class MainMenuScreen implements Screen {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    public MainMenuScreen(AbyssBattle game) {
        this.game = game;

        stage = new Stage(new ScreenViewport());
        background = new Texture("images/MenuBackGround.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // tabla principal
        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        VisTable mainTable = new VisTable();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * Constants.TITLE_SCALE_FACTOR);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);
        Label title = new Label("Abyss Battle", titleStyle);
        mainTable.add(title)
                .padBottom(h * Constants.TITLE_BOTTOM_PADDING)
                .left()
                .padLeft(w * Constants.TITLE_LEFT_PADDING);
        mainTable.row();

        VisTextButton.VisTextButtonStyle buttonStyle = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));

        buttonStyle.font = new BitmapFont();
        buttonStyle.font.getData().setScale(h * Constants.BUTTON_FONT_SCALE_MENU);

        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.YELLOW;

        buttonStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("34495EFF"));
        buttonStyle.over = VisUI.getSkin().newDrawable("white", Color.valueOf("1ABC9CFF"));
        buttonStyle.down = VisUI.getSkin().newDrawable("white", Color.valueOf("2ECC71FF"));

        float buttonWidth = w * Constants.BUTTON_WIDTH_PERCENT;
        float buttonHeight = h * Constants.BUTTON_HEIGHT_PERCENT;

        VisTextButton loginButton = new VisTextButton("Iniciar Sesión", buttonStyle);
        VisTextButton playButton = new VisTextButton("Jugar", buttonStyle);
        VisTextButton shopButton = new VisTextButton("Tienda de Skins", buttonStyle);
        VisTextButton mySkinsButton = new VisTextButton("Mis Skins", buttonStyle);
        VisTextButton exitButton = new VisTextButton("Salir", buttonStyle);

        VisTextButton[] buttons = { loginButton, playButton, shopButton, mySkinsButton, exitButton };

        for (VisTextButton b : buttons) {
            mainTable.add(b)
                    .width(buttonWidth)
                    .height(buttonHeight)
                    .padBottom(h * Constants.BUTTON_BOTTOM_PADDING)
                    .left()
                    .padLeft(w * Constants.BUTTON_LEFT_PADDING);
            mainTable.row();
        }

        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SessionManager.getInstance().isLoggedIn()) {
                    // Si ya está logueado, cerrar sesión
                    SessionManager.getInstance().logout();
                    loginButton.setText("Iniciar Sesion");
                } else {
                    // Si no está logueado, ir a la pantalla de login
                    game.setScreen(new LoginScreen(game));
                }
            }
        });

        if (SessionManager.getInstance().isLoggedIn()) {
            loginButton.setText("Cerrar Sesion");
        }

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                 if (SessionManager.getInstance().isLoggedIn()) {
                    game.setScreen(new GameScreen(game));
                    showLoginWarning();
                }
            }
        });

        shopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SessionManager.getInstance().isLoggedIn()) {
                    game.setScreen(new ShopSkinsScreen(game));
                } else {
                    showLoginWarning(); 
                }
            }
        });

        mySkinsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SessionManager.getInstance().isLoggedIn()) {
                    game.setScreen(new MySkinsScreen(game));
                } else {
                    showLoginWarning();
                }
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().draw(background, 0, 0, stage.getWidth(), stage.getHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    /**
     * Muestra un diálogo flotante avisando que se requiere login.
     */
    private void showLoginWarning() {
        VisDialog dialog = new VisDialog("Acceso Restringido");
        dialog.text("Debes iniciar sesión para acceder a esta sección.");
        dialog.button("Entendido");
        dialog.pack(); // Ajusta el tamaño al contenido
        dialog.centerWindow(); // Lo centra en la pantalla
        dialog.show(stage); // Lo muestra en el stage actual
    }
}
