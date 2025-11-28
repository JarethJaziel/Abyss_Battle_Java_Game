package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;

public class MainMenuScreen implements Screen{

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    public MainMenuScreen(AbyssBattle game) {
        this.game = game;
        
        //Crear el 'Stage' para que la UI se ajuste al tamaño de la ventana
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/MenuBackGround.png");
    }

    @Override
    public void show() {
        //Hacer que el Stage pueda recibir input (clics, etc.)
        Gdx.input.setInputProcessor(stage);

        VisTable mainTable = new VisTable();
        mainTable.setFillParent(true); 
        stage.addActor(mainTable);    

        mainTable.add(new VisLabel("Abyss Battle")).padBottom(50);
        mainTable.row(); 

        VisTextButton playButton = new VisTextButton("Jugar");
        mainTable.add(playButton).fillX().pad(10);
        mainTable.row();

        VisTextButton shopButton = new VisTextButton("Tienda de Skins");
        mainTable.add(shopButton).fillX().pad(10);
        mainTable.row();

        VisTextButton mySkinsButton = new VisTextButton("Mis Skins");
        mainTable.add(mySkinsButton).fillX().pad(10);
        mainTable.row();

        VisTextButton exitButton = new VisTextButton("Salir");
        mainTable.add(exitButton).fillX().pad(10);
        mainTable.row();

        //Acciones vinculadas a los botones:

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game)); 
            }
        });

        shopButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: Ir a la pantalla de la tienda
                Gdx.app.log("MENU", "Ir a la Tienda de Skins");
                game.setScreen(new ShopSkinsScreen(game));
            }
        });

        mySkinsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // TODO: Ir a la pantalla de "Mis Skins"
                Gdx.app.log("MENU", "Ir a Mis Skins");
                game.setScreen(new MySkinsScreen(game));
            }
        });

        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Salir del juego
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
