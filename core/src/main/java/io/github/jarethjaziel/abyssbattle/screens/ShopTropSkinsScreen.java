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
import io.github.jarethjaziel.abyssbattle.util.Constants;

public class ShopTropSkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;


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

        float titleX = w * Constants.TITLE_POS_X;
        float titleY = h * Constants.TITLE_POS_Y;

        float backWidth = w * Constants.BACK_WIDTH;
        float backHeight = h * Constants.BACK_HEIGHT;
        float backX = w * Constants.BACK_POS_X;
        float backY = h * Constants.BACK_POS_Y;

        float cannonBtnWidth = w * Constants.CANNON_BTN_WIDTH;
        float cannonBtnHeight = h * Constants.CANNON_BTN_HEIGHT;
        float cannonBtnX = w * Constants.CANNON_BTN_POS_X;
        float cannonBtnY = h * Constants.CANNON_BTN_POS_Y;

        float startX = w * Constants.SKIN_START_X;
        float offsetX = w * Constants.SKIN_OFFSET_X;

        float skinLabelOffsetX = w * Constants.LABEL_OFFSET_X;
        float skinLabelY = h * Constants.SKIN_LABEL_Y;
        float priceLabelY = h * Constants.PRICE_LABEL_Y;

        float buyBtnWidth = w * Constants.BUY_WIDTH;
        float buyBtnHeight = h * Constants.BUY_HEIGHT;
        float buyBtnOffsetX = w * Constants.BUY_OFFSET_X;
        float buyBtnY = h * Constants.BUY_POS_Y;

        Preferences prefs = Gdx.app.getPreferences("abyss_battle_skins");

        String[] skins = {"Skin Bronze", "Skin Silver", "Skin Green", "Skin Ultra"};
        int[] prices = {100, 150, 200, 300};

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * Constants.TITLE_FONT_SCALE);

        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(h * Constants.SKIN_FONT_SCALE);

        BitmapFont priceFont = new BitmapFont();
        priceFont.getData().setScale(h * Constants.PRICE_FONT_SCALE);

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
        backStyle.font.getData().setScale(h * Constants.BUTTON_FONT_SCALE_SHOP);
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
        cannonSkinStyle.font.getData().setScale(h * Constants.BUTTON_FONT_SCALE_SHOP);
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
            buyStyle.font.getData().setScale(h * Constants.BUY_BUTTON_FONT_SCALE);
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





