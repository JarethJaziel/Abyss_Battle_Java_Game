package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;

public class ShopSkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    public ShopSkinsScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/ShopSkins.jpeg");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        VisTable table = new VisTable(true);
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new VisLabel("Tienda de Skins")).padBottom(40);
        table.row();

        // Ejemplo de skin que se puede comprar
        VisTextButton buySkin1 = new VisTextButton("Comprar Skin Roja 100 monedas");
        table.add(buySkin1).pad(10);
        table.row();

        VisTextButton back = new VisTextButton("Regresar");
        table.add(back).padTop(40);

        back.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new MainMenuScreen(game));
            }
            return true;
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
    public void dispose() {
        stage.dispose();
    }
}
