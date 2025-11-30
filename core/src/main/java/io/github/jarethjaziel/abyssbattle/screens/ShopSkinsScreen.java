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
        background = new Texture("images/SkinsShop2.png");
    }

   @Override
public void show() {
    Gdx.input.setInputProcessor(stage);

    // ======== PREFERENCIAS (para guardar compras) ========
    Preferences prefs = Gdx.app.getPreferences("abyss_battle_skins");

    // Lista de skins y precios
    String[] skins = { "Skin Roja", "Skin Azul", "Skin Verde" };
    int[] prices = { 100, 150, 200 };

    // ======== ESTILOS ========
    BitmapFont titleFont = new BitmapFont();
    titleFont.getData().setScale(3f);
    Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.GOLD);

    BitmapFont skinFont = new BitmapFont();
    skinFont.getData().setScale(2f);
    Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.WHITE);

    // Estilo Botones Buy
    VisTextButton.VisTextButtonStyle buyStyle = new VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
    );

    buyStyle.font = new BitmapFont();
    buyStyle.font.getData().setScale(1.8f);
    buyStyle.fontColor = Color.WHITE;

    buyStyle.up   = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF"));
    buyStyle.over = VisUI.getSkin().newDrawable("white", Color.valueOf("34495EFF"));
    buyStyle.down = VisUI.getSkin().newDrawable("white", Color.valueOf("27AE60FF"));

    // Estilo Purchased
    VisTextButton.VisTextButtonStyle purchasedStyle = new VisTextButton.VisTextButtonStyle(buyStyle);
    purchasedStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("16A085FF"));
    purchasedStyle.fontColor = Color.YELLOW;


    // ======== TABLA PRINCIPAL ========
    VisTable table = new VisTable(true);
    table.setFillParent(true);
    table.pad(20);
    stage.addActor(table);

    Label title = new Label("Tienda de Skins", titleStyle);
    table.add(title).padBottom(60);
    table.row();


    // ===============================
    //     GENERAR SKINS DINÁMICAS
    // ===============================
    for (int i = 0; i < skins.length; i++) {
        String skinName = skins[i];
        int price = prices[i];

        boolean purchased = prefs.getBoolean(skinName, false);

        VisTable row = new VisTable(true);

        Label skinLabel = new Label(skinName + " - " + price + " monedas", skinStyle);
        row.add(skinLabel).left().padRight(25);

        // Botón Buy o Purchased
        VisTextButton buyButton;

        if (purchased) {
            buyButton = new VisTextButton("Purchased", purchasedStyle);
        } else {
            buyButton = new VisTextButton("Buy", buyStyle);
        }

        // Listener de compra
        buyButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                if (!prefs.getBoolean(skinName, false)) {
                    // Guardar compra
                    prefs.putBoolean(skinName, true);
                    prefs.flush();
                }

                // Cambiar botón a Purchased
                buyButton.setText("Purchased");
                buyButton.setStyle(purchasedStyle);
            }
        });

        row.add(buyButton).width(220);

        table.add(row).pad(15);
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
    table.add(back).width(300);

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
