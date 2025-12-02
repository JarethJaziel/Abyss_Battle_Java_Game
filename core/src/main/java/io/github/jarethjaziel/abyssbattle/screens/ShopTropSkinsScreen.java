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

    public ShopTropSkinsScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/TropSkinsShop2.png");
    }


    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        Preferences prefs = Gdx.app.getPreferences("abyss_battle_skins");

        String[] skins = {"Skin Bronze", "Skin Plata", "Skin Verde", "Skin Ultra"};
        int[] prices = {100, 150, 200, 250};

        // ===== FUENTES =====
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);

        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(1.2f);

        BitmapFont priceFont = new BitmapFont();
        priceFont.getData().setScale(1.0f);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.GOLD);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);
        Label.LabelStyle priceStyle = new Label.LabelStyle(priceFont, Color.YELLOW);

        Label title = new Label("Tienda de Skins", titleStyle);
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
        back.setSize(150, 60);
        back.setPosition(stage.getWidth() - 180, stage.getHeight() - 80);
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
        cannonSkinStyle.font.getData().setScale(1.1f);
        cannonSkinStyle.fontColor = Color.WHITE;
        cannonSkinStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2980B9FF"));

        VisTextButton cannonSkin = new VisTextButton("Skins de Cañones", cannonSkinStyle);
        cannonSkin.setSize(180, 60);
        cannonSkin.setPosition(stage.getWidth() - 200, stage.getHeight() - 150);
        stage.addActor(cannonSkin);

        cannonSkin.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                game.setScreen(new ShopSkinsScreen(game));
            }
            return true;
        });

        int startX = 45;
        int offsetX = 155;

        for (int i = 0; i < skins.length; i++) {
            int x = startX + i * offsetX;

            String skinName = skins[i];
            int price = prices[i];

            boolean purchased = prefs.getBoolean(skinName, false);

            Label skinLabel = new Label(skinName, skinStyle);
            skinLabel.setPosition(x, 90);
            stage.addActor(skinLabel);

            Label priceLabel = new Label(price + " monedas", priceStyle);
            priceLabel.setPosition(x, 60);
            stage.addActor(priceLabel);

            VisTextButton.VisTextButtonStyle buyStyle =
                    new VisTextButton.VisTextButtonStyle(
                            VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
                    );
            buyStyle.font = new BitmapFont();
            buyStyle.font.getData().setScale(1.0f);
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

            btn.setSize(90, 30);
            btn.setPosition(x, 10);

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
