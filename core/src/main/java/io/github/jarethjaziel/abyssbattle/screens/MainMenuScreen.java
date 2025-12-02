package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import static com.badlogic.gdx.graphics.Color.YELLOW;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;

public class MainMenuScreen implements Screen {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    private static final float TITLE_SCALE_FACTOR = 0.0090f;
    private static final float TITLE_BOTTOM_PADDING = 0.08f;
    private static final float TITLE_LEFT_PADDING = 0.36f;

    private static final float BUTTON_FONT_SCALE = 0.0032f;
    private static final float BUTTON_WIDTH_PERCENT = 0.28f;
    private static final float BUTTON_HEIGHT_PERCENT = 0.12f;

    private static final float BUTTON_LEFT_PADDING = 0.42f;
    private static final float BUTTON_BOTTOM_PADDING = 0.04f;

    public MainMenuScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/MenuBackGround.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        VisTable mainTable = new VisTable();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * TITLE_SCALE_FACTOR);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);
        Label title = new Label("Abyss Battle", titleStyle);
        mainTable.add(title)
                .padBottom(h * TITLE_BOTTOM_PADDING)
                .left()
                .padLeft(w * TITLE_LEFT_PADDING);
        mainTable.row();

        VisTextButton.VisTextButtonStyle buttonStyle =
                new VisTextButton.VisTextButtonStyle(
                        VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                );

        buttonStyle.font = new BitmapFont();
        buttonStyle.font.getData().setScale(h * BUTTON_FONT_SCALE);

        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.downFontColor = Color.YELLOW;

        buttonStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("34495EFF"));
        buttonStyle.over = VisUI.getSkin().newDrawable("white", Color.valueOf("1ABC9CFF"));
        buttonStyle.down = VisUI.getSkin().newDrawable("white", Color.valueOf("2ECC71FF"));

        float buttonWidth = w * BUTTON_WIDTH_PERCENT;
        float buttonHeight = h * BUTTON_HEIGHT_PERCENT;

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
                    .padBottom(h * BUTTON_BOTTOM_PADDING)
                    .left()
                    .padLeft(w * BUTTON_LEFT_PADDING);
            mainTable.row();
        }

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });

        shopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new ShopSkinsScreen(game));
            }
        });

        mySkinsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MySkinsScreen(game));
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
    public void pause() { throw new UnsupportedOperationException("Unimplemented method 'pause'"); }

    @Override
    public void resume() { throw new UnsupportedOperationException("Unimplemented method 'resume'"); }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

