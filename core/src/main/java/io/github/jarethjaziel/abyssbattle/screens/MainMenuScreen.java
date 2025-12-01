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

public class MainMenuScreen implements Screen{

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

    VisTable mainTable = new VisTable();
    mainTable.setFillParent(true);
    stage.addActor(mainTable);

    BitmapFont titleFont = new BitmapFont();
    titleFont.getData().setScale(3f);

    Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);

    Label title = new Label("Abyss Battle", titleStyle);
    mainTable.add(title).right().pad(10);
    mainTable.add(title).padBottom(80);
    mainTable.row();

    VisTextButton.VisTextButtonStyle buttonStyle =
            new VisTextButton.VisTextButtonStyle(
                    VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
            );

    buttonStyle.font = new BitmapFont();
    buttonStyle.font.getData().setScale(2f);

    buttonStyle.fontColor = Color.WHITE;
    buttonStyle.downFontColor = YELLOW;

    buttonStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("34495EFF"));   // Azul grisáceo
    buttonStyle.over = VisUI.getSkin().newDrawable("white", Color.valueOf("1ABC9CFF")); // Hover
    buttonStyle.down = VisUI.getSkin().newDrawable("white", Color.valueOf("2ECC71FF")); // Presionado

    VisTextButton loginButton = new VisTextButton("Iniciar Sesion", buttonStyle);
    mainTable.add(loginButton).top().pad(10);
    mainTable.add(loginButton).fillX().pad(10);
    mainTable.row();

    VisTextButton playButton = new VisTextButton("Jugar", buttonStyle);
    mainTable.add(playButton).right().pad(10);
    mainTable.add(playButton).fillX().pad(10);
    mainTable.row();

    VisTextButton shopButton = new VisTextButton("Tienda de Skins", buttonStyle);
    mainTable.add(shopButton).right().pad(10);
    mainTable.add(shopButton).fillX().pad(10);
    mainTable.row();

    VisTextButton mySkinsButton = new VisTextButton("Mis Skins", buttonStyle);
    mainTable.add(mySkinsButton).right().pad(10);
    mainTable.add(mySkinsButton).fillX().pad(10);
    mainTable.row();

    VisTextButton exitButton = new VisTextButton("Salir", buttonStyle);
    mainTable.add(exitButton).right().pad(10);
    mainTable.add(exitButton).fillX().pad(10);
    mainTable.row();

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
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
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

}
