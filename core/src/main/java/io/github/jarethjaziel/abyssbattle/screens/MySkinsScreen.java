package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
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
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;

public class MySkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    public MySkinsScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/SkinsStock.png");
    }

@Override
public void show() {
    Gdx.input.setInputProcessor(stage);

    // ======== ESTILOS ========
    // Fuente para títulos
    BitmapFont titleFont = new BitmapFont();
    titleFont.getData().setScale(3f);

    Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);

    // Fuente para skins
    BitmapFont skinFont = new BitmapFont();
    skinFont.getData().setScale(2f);

    Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.WHITE);

    // Estilo botones Equip
    VisTextButton.VisTextButtonStyle equipStyle =
            new VisTextButton.VisTextButtonStyle(
                    VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
            );

    equipStyle.font = new BitmapFont();
    equipStyle.font.getData().setScale(1.8f);
    equipStyle.fontColor = Color.WHITE;

    equipStyle.up   = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF")); 
    equipStyle.over = VisUI.getSkin().newDrawable("white", Color.valueOf("34495EFF"));
    equipStyle.down = VisUI.getSkin().newDrawable("white", Color.valueOf("27AE60FF"));


    // ======== LAYOUT ========
    VisTable table = new VisTable(true);
    table.setFillParent(true);
    table.pad(20);
    stage.addActor(table);

    // Título
    Label title = new Label("Mis Skins", titleStyle);
    table.add(title).padBottom(60);
    table.row();


    // ===============================
    // SISTEMA DE SKINS (dinámico)
    // ===============================

    // Lista de nombres de skins
    String[] skins = {
            "Skin Roja",
            "Skin Azul",
            "Skin Verde"
    };

    // Índice de la skin actualmente equipada (puedes guardarlo luego en Preferences)
    final int[] equippedIndex = {0}; // por defecto la primera

    // Para actualizar botones
    VisTextButton[] equipButtons = new VisTextButton[skins.length];

    // Crear cada skin en una fila
    for (int i = 0; i < skins.length; i++) {
        int index = i;

        VisTable skinRow = new VisTable(true);

        // Nombre de la skin
        Label skinLabel = new Label(skins[i], skinStyle);
        skinRow.add(skinLabel).left().padRight(20);

        // Botón equipar
        VisTextButton equipButton = new VisTextButton("", equipStyle);
        equipButtons[i] = equipButton;

        // Texto inicial
        if (i == equippedIndex[0]) {
            equipButton.setText("Equipped");
        } else {
            equipButton.setText("Equip");
        }

        // Listener del botón
        equipButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Cambiar el equip actual
                equippedIndex[0] = index;

                // Actualizar todos
                for (int j = 0; j < equipButtons.length; j++) {
                    if (j == equippedIndex[0]) {
                        equipButtons[j].setText("Equipped");
                    } else {
                        equipButtons[j].setText("Equip");
                    }
                }
            }
        });

        skinRow.add(equipButton).width(200);

        table.add(skinRow).pad(15);
        table.row();
    }


    // ======== BOTÓN REGRESAR ========
    VisTextButton.VisTextButtonStyle backStyle =
            new VisTextButton.VisTextButtonStyle(
                    VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
            );
    backStyle.font = new BitmapFont();
    backStyle.font.getData().setScale(2f);
    backStyle.fontColor = Color.WHITE;
    backStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("8E44ADFF"));
    backStyle.over = VisUI.getSkin().newDrawable("white", Color.valueOf("9B59B6FF"));
    backStyle.down = VisUI.getSkin().newDrawable("white", Color.valueOf("663399FF"));

    VisTextButton back = new VisTextButton("Regresar", backStyle);

    table.row().padTop(50);
    table.add(back).pad(10).width(300);

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
