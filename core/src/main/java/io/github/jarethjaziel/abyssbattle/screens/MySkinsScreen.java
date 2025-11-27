package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;

public class MySkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;

    public MySkinsScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        VisTable table = new VisTable(true);
        table.setFillParent(true);
        stage.addActor(table);

        table.add(new VisLabel("Mis Skins")).padBottom(40);
        table.row();

        // Aquí luego pondrás tu inventario real
        table.add(new VisLabel("Skin roja - equipada")).pad(10);
        table.row();
        table.add(new VisLabel("Skin azul - disponible")).pad(10);
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

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
