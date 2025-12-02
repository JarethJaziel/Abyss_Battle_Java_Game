package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
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

public class ShopTropSkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    // =======================
    //        CONSTANTES
    // =======================

    private static final float TITLE_POS_X = 0.35f;
    private static final float TITLE_POS_Y = 0.86f;

    private static final float BACK_WIDTH = 0.11f;
    private static final float BACK_HEIGHT = 0.085f;
    private static final float BACK_POS_X = 0.87f;
    private static final float BACK_POS_Y = 0.86f;

    private static final float CANNON_BTN_WIDTH = 0.13f;
    private static final float CANNON_BTN_HEIGHT = 0.075f;
    private static final float CANNON_BTN_POS_X = 0.84f;
    private static final float CANNON_BTN_POS_Y = 0.73f;

    private static final float SKIN_START_X = 0.05f;
    private static final float SKIN_OFFSET_X = 0.242f;

    private static final float LABEL_OFFSET_X = 0.03f;
    private static final float SKIN_LABEL_Y = 0.17f;
    private static final float PRICE_LABEL_Y = 0.12f;

    private static final float BUY_WIDTH = 0.12f;
    private static final float BUY_HEIGHT = 0.065f;
    private static final float BUY_OFFSET_X = 0.025f;
    private static final float BUY_POS_Y = 0.02f;

    private static final float TITLE_FONT_SCALE = 0.0042f;
    private static final float SKIN_FONT_SCALE = 0.0022f;
    private static final float PRICE_FONT_SCALE = 0.0020f;
    private static final float BUTTON_FONT_SCALE = 0.0014f;
    private static final float BUY_BUTTON_FONT_SCALE = 0.002f;

    public ShopTropSkinsScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/TropSkinsShop2.png");
    }

    @Override
    public void show() {

        Gdx.input.setInputProcessor(stage);

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        float titleX = w * TITLE_POS_X;
        float titleY = h * TITLE_POS_Y;

        float backWidth = w * BACK_WIDTH;
        float backHeight = h * BACK_HEIGHT;
        float backX = w * BACK_POS_X;
        float backY = h * BACK_POS_Y;

        float cannonBtnWidth = w * CANNON_BTN_WIDTH;
        float cannonBtnHeight = h * CANNON_BTN_HEIGHT;
        float cannonBtnX = w * CANNON_BTN_POS_X;
        float cannonBtnY = h * CANNON_BTN_POS_Y;

        float startX = w * SKIN_START_X;
        float offsetX = w * SKIN_OFFSET_X;

        float skinLabelOffsetX = w * LABEL_OFFSET_X;
        float skinLabelY = h * SKIN_LABEL_Y;
        float priceLabelY = h * PRICE_LABEL_Y;

        float buyBtnWidth = w * BUY_WIDTH;
        float buyBtnHeight = h * BUY_HEIGHT;
        float buyBtnOffsetX = w * BUY_OFFSET_X;
        float buyBtnY = h * BUY_POS_Y;

        Preferences prefs = Gdx.app.getPreferences("abyss_battle_skins");

        String[] skins = {"Skin Bronze", "Skin Silver", "Skin Green", "Skin Ultra"};
        int[] prices = {100, 150, 200, 300};

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * TITLE_FONT_SCALE);

        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(h * SKIN_FONT_SCALE);

        BitmapFont priceFont = new BitmapFont();
        priceFont.getData().setScale(h * PRICE_FONT_SCALE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.GOLD);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);
        Label.LabelStyle priceStyle = new Label.LabelStyle(priceFont, Color.YELLOW);

        Label title = new Label("Skins de Tropas", titleStyle);
        title.setPosition(titleX, titleY);
        stage.addActor(title);

        VisTextButton.VisTextButtonStyle backStyle =
                new VisTextButton.VisTextButtonStyle(
                        VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                );
        backStyle.font = new BitmapFont();
        backStyle.font.getData().setScale(h * BUTTON_FONT_SCALE);
        backStyle.fontColor = Color.WHITE;
        backStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("8E44ADFF"));

        VisTextButton back = new VisTextButton("Regresar", backStyle);
        back.setSize(backWidth, backHeight);
        back.setPosition(backX, backY);
        stage.addActor(back);

        back.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new MainMenuScreen(game));
            }
            return true;
        });

        VisTextButton.VisTextButtonStyle cannonSkinStyle =
                new VisTextButton.VisTextButtonStyle(
                        VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                );
        cannonSkinStyle.font = new BitmapFont();
        cannonSkinStyle.font.getData().setScale(h * BUTTON_FONT_SCALE);
        cannonSkinStyle.fontColor = Color.WHITE;
        cannonSkinStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2980B9FF"));

        VisTextButton cannonSkin = new VisTextButton("Skins de Cañones", cannonSkinStyle);
        cannonSkin.setSize(cannonBtnWidth, cannonBtnHeight);
        cannonSkin.setPosition(cannonBtnX, cannonBtnY);
        stage.addActor(cannonSkin);

        cannonSkin.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new ShopSkinsScreen(game));
            }
            return true;
        });

        for (int i = 0; i < skins.length; i++) {

            float x = startX + i * offsetX;

            String skinName = skins[i];
            int price = prices[i];
            boolean purchased = prefs.getBoolean(skinName, false);

            Label skinLabel = new Label(skinName, skinStyle);
            skinLabel.setPosition(x + skinLabelOffsetX, skinLabelY);
            stage.addActor(skinLabel);

            Label priceLabel = new Label(price + " monedas", priceStyle);
            priceLabel.setPosition(x + skinLabelOffsetX, priceLabelY);
            stage.addActor(priceLabel);

            VisTextButton.VisTextButtonStyle buyStyle =
                    new VisTextButton.VisTextButtonStyle(
                            VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                    );
            buyStyle.font = new BitmapFont();
            buyStyle.font.getData().setScale(h * BUY_BUTTON_FONT_SCALE);
            buyStyle.fontColor = Color.WHITE;
            buyStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF"));

            VisTextButton.VisTextButtonStyle purchasedStyle =
                    new VisTextButton.VisTextButtonStyle(buyStyle);
            purchasedStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("16A085FF"));
            purchasedStyle.fontColor = Color.YELLOW;

            VisTextButton btn;

            if (purchased)
                btn = new VisTextButton("Adquirido", purchasedStyle);
            else
                btn = new VisTextButton("Comprar", buyStyle);

            btn.setSize(buyBtnWidth, buyBtnHeight);
            btn.setPosition(x + buyBtnOffsetX, buyBtnY);

            final VisTextButton finalBtn = btn;

            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (!prefs.getBoolean(skinName, false)) {
                        prefs.putBoolean(skinName, true);
                        prefs.flush();
                    }
                    finalBtn.setText("Adquirido");
                    finalBtn.setStyle(purchasedStyle);
                }
            });

            stage.addActor(btn);
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
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}





