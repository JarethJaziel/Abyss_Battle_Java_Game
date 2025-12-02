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
import io.github.jarethjaziel.abyssbattle.util.Constants;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;

public class ShopSkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    

    public ShopSkinsScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/SkinsShop2.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        Preferences prefs = Gdx.app.getPreferences("abyss_battle_skins");

        String[] skins = { "Skin Bronze", "Skin Plata", "Skin Verde", "Skin Azul" };
        int[] prices = { 100, 150, 200, 250 };

        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * Constants.TITLE_SCALE);

        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(h * Constants.SKIN_NAME_SCALE);

        BitmapFont priceFont = new BitmapFont();
        priceFont.getData().setScale(h * Constants.PRICE_SCALE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.GOLD);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);
        Label.LabelStyle priceStyle = new Label.LabelStyle(priceFont, Color.YELLOW);

        Label title = new Label("Tienda de Skins", titleStyle);
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
        tropSkinStyle.font.getData().setScale(h * Constants.TROP_BUTTON_FONT_SCALE);
        tropSkinStyle.fontColor = Color.WHITE;
        tropSkinStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2980B9FF"));

        VisTextButton tropSkin = new VisTextButton("Skins de Tropas", tropSkinStyle);
        tropSkin.setSize(w * Constants.TROP_BUTTON_WIDTH, h * Constants.TROP_BUTTON_HEIGHT);
        tropSkin.setPosition(w * Constants.TROP_BUTTON_POS_X, h * Constants.TROP_BUTTON_POS_Y);
        stage.addActor(tropSkin);

        tropSkin.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new ShopTropSkinsScreen(game));
            }
            return true;
        });

        for (int i = 0; i < skins.length; i++) {
            float x = w * Constants.SKIN_START_X + i * w * Constants.SKIN_OFFSET_X;

            String skinName = skins[i];
            int price = prices[i];

            boolean purchased = prefs.getBoolean(skinName, false);

            Label skinLabel = new Label(skinName, skinStyle);
            skinLabel.setPosition(x + w * Constants.SKIN_LABEL_OFFSET_X, h * Constants.SKIN_LABEL_Y);
            stage.addActor(skinLabel);

            Label priceLabel = new Label(price + " monedas", priceStyle);
            priceLabel.setPosition(x + w * Constants.SKIN_LABEL_OFFSET_X, h * Constants.PRICE_LABEL_Y);
            stage.addActor(priceLabel);

            VisTextButton.VisTextButtonStyle buyStyle =
                    new VisTextButton.VisTextButtonStyle(
                            VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                    );

            buyStyle.font = new BitmapFont();
            buyStyle.font.getData().setScale(h * Constants.BUY_BUTTON_FONT_SCALE);
            buyStyle.fontColor = Color.WHITE;
            buyStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF"));

            VisTextButton.VisTextButtonStyle purchasedStyle = new VisTextButton.VisTextButtonStyle(buyStyle);
            purchasedStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("16A085FF"));
            purchasedStyle.fontColor = Color.YELLOW;

            VisTextButton btn;

            if (purchased)
                btn = new VisTextButton("Adquirido", purchasedStyle);
            else
                btn = new VisTextButton("Comprar", buyStyle);

            btn.setSize(w * Constants.BUY_BUTTON_WIDTH, h * Constants.BUY_BUTTON_HEIGHT);
            btn.setPosition(x + w * Constants.BUY_BUTTON_OFFSET_X, h * Constants.BUY_BUTTON_Y);

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



