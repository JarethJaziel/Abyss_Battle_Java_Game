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
import io.github.jarethjaziel.abyssbattle.util.Constants;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;

public class MySkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;


    public MySkinsScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/ShopSkins.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * Constants.TITLE_SCALE);

        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(h * Constants.SKIN_NAME_SCALE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);

        Label title = new Label("Inventario de Skins", titleStyle);
        title.setPosition(w * Constants.TITLE_POS_X, h * Constants.TITLE_POS_Y);
        stage.addActor(title);

        VisTextButton.VisTextButtonStyle backStyle =
                new VisTextButton.VisTextButtonStyle(
                        VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                );

        backStyle.font = new BitmapFont();
        backStyle.font.getData().setScale(h * Constants.BACK_BUTTON_FONT_SCALE);
        backStyle.fontColor = Color.WHITE;
        backStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("8E44ADFF"));

        VisTextButton back = new VisTextButton("Regresar", backStyle);
        back.setSize(w * Constants.BACK_BUTTON_WIDTH, h * Constants.BACK_BUTTON_HEIGHT);
        back.setPosition(w * Constants.BACK_BUTTON_POS_X, h * Constants.BACK_BUTTON_POS_Y);
        stage.addActor(back);

        back.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new MainMenuScreen(game));
            }
            return true;
        });

        VisTextButton.VisTextButtonStyle tropSkinStyle =
                new VisTextButton.VisTextButtonStyle(
                        VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                );

        tropSkinStyle.font = new BitmapFont();
        tropSkinStyle.font.getData().setScale(h * Constants.TROP_BUTTON2_FONT_SCALE);
        tropSkinStyle.fontColor = Color.WHITE;
        tropSkinStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2980B9FF"));

        VisTextButton troopsButton = new VisTextButton("Skins de Tropas", tropSkinStyle);
        troopsButton.setSize(w * Constants.TROP_BUTTON2_WIDTH, h * Constants.TROP_BUTTON2_HEIGHT);
        troopsButton.setPosition(w * Constants.TROP_BUTTON2_POS_X, h * (Constants.TROP_BUTTON2_POS_Y - 0.12f));
        stage.addActor(troopsButton);

        troopsButton.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new MyTroopSkinsScreen(game));
            }
            return true;
        });

        String[] skins = { "Skin Bronze", "Skin Plata", "Skin Verde", "Skin Azul" };

        final int[] equippedIndex = { game.getEquippedSkinIndex() };
        VisTextButton[] equipButtons = new VisTextButton[skins.length];

        for (int i = 0; i < skins.length; i++) {
            float x = w * Constants.SKIN_START_X + i * w * Constants.SKIN_OFFSET_X;

            Label skinLabel = new Label(skins[i], skinStyle);
            skinLabel.setPosition(x + w * Constants.SKIN_LABEL_OFFSET_X, h * Constants.SKIN_LABEL_Y);
            stage.addActor(skinLabel);

            VisTextButton.VisTextButtonStyle equipStyle =
                    new VisTextButton.VisTextButtonStyle(
                            VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                    );

            equipStyle.font = new BitmapFont();
            equipStyle.font.getData().setScale(h * Constants.EQUIP_BUTTON_FONT_SCALE);
            equipStyle.fontColor = Color.WHITE;
            equipStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF"));

            VisTextButton equipBtn = new VisTextButton("", equipStyle);
            equipBtn.setSize(w * Constants.EQUIP_BUTTON_WIDTH, h * Constants.EQUIP_BUTTON_HEIGHT);
            equipBtn.setPosition(x + w * Constants.EQUIP_BUTTON_OFFSET_X, h * Constants.EQUIP_BUTTON_Y);

            if (i == equippedIndex[0])
                equipBtn.setText("Equipado");
            else
                equipBtn.setText("Equipar");

            equipButtons[i] = equipBtn;

            final int index = i;
            equipBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    game.setEquippedSkinIndex(index);  
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
        stage.getBatch().draw(background, 0, 0,
                stage.getViewport().getWorldWidth(),
                stage.getViewport().getWorldHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}



