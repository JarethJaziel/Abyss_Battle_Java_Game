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
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;

public class MySkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    public MySkinsScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/SkinsShop2.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // ==== FUENTES ====
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);

        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(1.2f);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);

        Label title = new Label("Inventario de Skins", titleStyle);
        title.setPosition(40, stage.getHeight() - 90);
        stage.addActor(title);

        VisTextButton.VisTextButtonStyle backStyle =
                new VisTextButton.VisTextButtonStyle(
                        VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                );

        backStyle.font = new BitmapFont();
        backStyle.font.getData().setScale(1.1f);
        backStyle.fontColor = Color.WHITE;
        backStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("8E44ADFF"));

        VisTextButton back = new VisTextButton("Regresar", backStyle);
        back.setSize(100, 70);
        back.setPosition(stage.getWidth() - 130, stage.getHeight() - 90);
        stage.addActor(back);

        back.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new MainMenuScreen(game));
            }
            return true;
        });

        String[] skins = { "Skin Bronze", "Skin Plata", "Skin Verde", "Skin Azul" };
        int startX = 45;   // posición X inicial del primer recuadro de skin
        int offsetX = 155; // distancia entre recuadros

        final int[] equippedIndex = {0};

        VisTextButton[] equipButtons = new VisTextButton[skins.length];

        for (int i = 0; i < skins.length; i++) {
            int x = startX + i * offsetX;

            // Texto dentro del recuadro crema
            Label skinLabel = new Label(skins[i], skinStyle);
            skinLabel.setPosition(x, 70);
            stage.addActor(skinLabel);

 
            VisTextButton.VisTextButtonStyle equipStyle =
                    new VisTextButton.VisTextButtonStyle(
                            VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                    );
            equipStyle.font = new BitmapFont();
            equipStyle.font.getData().setScale(1.0f);
            equipStyle.fontColor = Color.WHITE;
            equipStyle.up   = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF"));

            VisTextButton equipBtn = new VisTextButton("", equipStyle);
            equipBtn.setSize(70, 25);
            equipBtn.setPosition(x+10, 15);

            if (i == equippedIndex[0])
                equipBtn.setText("Equipado");
            else
                equipBtn.setText("Equipar");

            equipButtons[i] = equipBtn;

            final int index = i;
            equipBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    equippedIndex[0] = index;

                    for (int j = 0; j < equipButtons.length; j++) {
                        if (j == equippedIndex[0])
                            equipButtons[j].setText("Equipado");
                        else
                            equipButtons[j].setText("Equipar");
                    }
                }
            });

            stage.addActor(equipBtn);
        }
    }

    @Override
    public void render(float delta) {
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

