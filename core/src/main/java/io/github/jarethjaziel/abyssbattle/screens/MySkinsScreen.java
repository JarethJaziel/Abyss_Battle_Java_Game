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

    private static final float TITLE_SCALE = 0.0042f;
    private static final float SKIN_NAME_SCALE = 0.0022f;

    private static final float TITLE_POS_X = 0.35f;
    private static final float TITLE_POS_Y = 0.86f;

    private static final float BACK_BUTTON_FONT_SCALE = 0.0014f;
    private static final float BACK_BUTTON_WIDTH = 0.11f;
    private static final float BACK_BUTTON_HEIGHT = 0.085f;
    private static final float BACK_BUTTON_POS_X = 0.87f;
    private static final float BACK_BUTTON_POS_Y = 0.86f;

    private static final float TROP_BUTTON_FONT_SCALE = 0.0014f;
    private static final float TROP_BUTTON_WIDTH = 0.13f;
    private static final float TROP_BUTTON_HEIGHT = 0.075f;
    private static final float TROP_BUTTON_POS_X = 0.84f;
    private static final float TROP_BUTTON_POS_Y = 0.86f;

    private static final float SKIN_START_X = 0.05f;
    private static final float SKIN_OFFSET_X = 0.242f;

    private static final float SKIN_LABEL_OFFSET_X = 0.03f;
    private static final float SKIN_LABEL_Y = 0.17f;

    private static final float EQUIP_BUTTON_FONT_SCALE = 0.002f;
    private static final float EQUIP_BUTTON_WIDTH = 0.12f;
    private static final float EQUIP_BUTTON_HEIGHT = 0.065f;
    private static final float EQUIP_BUTTON_OFFSET_X = 0.025f;
    private static final float EQUIP_BUTTON_Y = 0.02f;

    public MySkinsScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/SkinsShop2.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * TITLE_SCALE);

        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(h * SKIN_NAME_SCALE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);

        Label title = new Label("Inventario de Skins", titleStyle);
        title.setPosition(w * TITLE_POS_X, h * TITLE_POS_Y);
        stage.addActor(title);

        VisTextButton.VisTextButtonStyle backStyle =
                new VisTextButton.VisTextButtonStyle(
                        VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                );

        backStyle.font = new BitmapFont();
        backStyle.font.getData().setScale(h * BACK_BUTTON_FONT_SCALE);
        backStyle.fontColor = Color.WHITE;
        backStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("8E44ADFF"));

        VisTextButton back = new VisTextButton("Regresar", backStyle);
        back.setSize(w * BACK_BUTTON_WIDTH, h * BACK_BUTTON_HEIGHT);
        back.setPosition(w * BACK_BUTTON_POS_X, h * BACK_BUTTON_POS_Y);
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
        tropSkinStyle.font.getData().setScale(h * TROP_BUTTON_FONT_SCALE);
        tropSkinStyle.fontColor = Color.WHITE;
        tropSkinStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2980B9FF"));

        VisTextButton troopsButton = new VisTextButton("Skins de Tropas", tropSkinStyle);
        troopsButton.setSize(w * TROP_BUTTON_WIDTH, h * TROP_BUTTON_HEIGHT);
        troopsButton.setPosition(w * TROP_BUTTON_POS_X, h * (TROP_BUTTON_POS_Y - 0.12f));
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
            float x = w * SKIN_START_X + i * w * SKIN_OFFSET_X;

            Label skinLabel = new Label(skins[i], skinStyle);
            skinLabel.setPosition(x + w * SKIN_LABEL_OFFSET_X, h * SKIN_LABEL_Y);
            stage.addActor(skinLabel);

            VisTextButton.VisTextButtonStyle equipStyle =
                    new VisTextButton.VisTextButtonStyle(
                            VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                    );

            equipStyle.font = new BitmapFont();
            equipStyle.font.getData().setScale(h * EQUIP_BUTTON_FONT_SCALE);
            equipStyle.fontColor = Color.WHITE;
            equipStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF"));

            VisTextButton equipBtn = new VisTextButton("", equipStyle);
            equipBtn.setSize(w * EQUIP_BUTTON_WIDTH, h * EQUIP_BUTTON_HEIGHT);
            equipBtn.setPosition(x + w * EQUIP_BUTTON_OFFSET_X, h * EQUIP_BUTTON_Y);

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



