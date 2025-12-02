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

import java.util.List;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.systems.ShopSystem;
import io.github.jarethjaziel.abyssbattle.database.systems.UserInventorySystem;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.SessionManager;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.PurchaseResult;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

public class ShopTropSkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    // --- SISTEMAS DE BASE DE DATOS ---
    private ShopSystem shopSystem;
    private UserInventorySystem inventorySystem;
    private User currentUser;

    // UI Dinámica
    private Label coinsLabel;

    public ShopTropSkinsScreen(AbyssBattle game) {
        this.game = game;
        
        // 1. Inicializar conexión
        this.shopSystem = new ShopSystem(game.getDbManager());
        this.inventorySystem = new UserInventorySystem(game.getDbManager());
        this.currentUser = SessionManager.getInstance().getCurrentUser();

        stage = new Stage(new ScreenViewport());
        background = new Texture("images/TropSkinsShop2.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        // --- FUENTES Y ESTILOS ---
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * Constants.TITLE_FONT_SCALE);

        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(h * Constants.SKIN_FONT_SCALE);

        BitmapFont priceFont = new BitmapFont();
        priceFont.getData().setScale(h * Constants.PRICE_FONT_SCALE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.GOLD);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);
        Label.LabelStyle priceStyle = new Label.LabelStyle(priceFont, Color.YELLOW);

        // --- TÍTULO ---
        Label title = new Label("Skins de Tropas", titleStyle);
        title.setPosition(w * Constants.TITLE_POS_X, h * Constants.TITLE_POS_Y);
        stage.addActor(title);

        // --- ETIQUETA DE MONEDAS (NUEVO) ---
        updateCoinsLabel(w, h, priceStyle);

        // --- BOTÓN REGRESAR ---
        setupBackButton(w, h);

        // --- BOTÓN IR A CAÑONES ---
        setupCannonShopButton(w, h);

        // ============================================================
        //     LOGICA DE CARGA DINÁMICA (TROPAS)
        // ============================================================

        // 1. Obtener Skins de la DB (Solo Tropas)
        List<Skin> skinsDisponibles = shopSystem.getSkinsByType(SkinType.TROOP);

        // 2. Generar UI
        for (int i = 0; i < skinsDisponibles.size(); i++) {
            Skin skin = skinsDisponibles.get(i);

            float x = w * Constants.SKIN_START_X + i * w * Constants.SKIN_OFFSET_X;

            // A. Nombre
            Label skinLabel = new Label(skin.getName(), skinStyle);
            skinLabel.setPosition(x + w * Constants.LABEL_OFFSET_X, h * Constants.SKIN_LABEL_Y);
            stage.addActor(skinLabel);

            // B. Precio
            Label priceLabel = new Label(skin.getPrice() + " monedas", priceStyle);
            priceLabel.setPosition(x + w * Constants.LABEL_OFFSET_X, h * Constants.PRICE_LABEL_Y);
            stage.addActor(priceLabel);

            // C. Verificar Propiedad (Con manejo de excepción seguro)
            boolean owned = inventorySystem.doesUserOwnSkin(currentUser, skin.getId());

            // D. Crear Botón
            VisTextButton btn = createShopButton(h, owned);
            btn.setSize(w * Constants.BUY_WIDTH, h * Constants.BUY_HEIGHT);
            btn.setPosition(x + w * Constants.BUY_OFFSET_X, h * Constants.BUY_POS_Y);
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (owned || btn.getText().toString().equals("Adquirido")) return;

                    // INTENTO DE COMPRA
                    PurchaseResult result = shopSystem.buySkin(currentUser, skin.getId());

                    if (result == PurchaseResult.SUCCESS) {
                        System.out.println("Skin comprada: " + skin.getName());
                        
                        // Actualizar Botón
                        btn.setText("Adquirido");
                        updateButtonStyle(btn, true, h);
                        
                        // Actualizar Monedas
                        coinsLabel.setText("Monedas: " + currentUser.getCoins());
                        
                    } else if (result == PurchaseResult.INSUFFICIENT_FUNDS) {
                        System.out.println("No tienes suficiente dinero.");
                    } else {
                        System.out.println("Error en la compra: " + result);
                    }
                }
            });

            stage.addActor(btn);
        }
    }

    // --- MÉTODOS AUXILIARES ---

    private void updateCoinsLabel(float w, float h, Label.LabelStyle style) {
        if (coinsLabel == null) {
            coinsLabel = new Label("", style);
            // Posición sugerida: Esquina superior izquierda
            coinsLabel.setPosition(w * 0.05f, h * 0.9f); 
            stage.addActor(coinsLabel);
        }
        coinsLabel.setText("Monedas: " + currentUser.getCoins());
    }

    private VisTextButton createShopButton(float h, boolean owned) {
        VisTextButton btn = new VisTextButton(owned ? "Adquirido" : "Comprar");
        updateButtonStyle(btn, owned, h);
        return btn;
    }

    private void updateButtonStyle(VisTextButton btn, boolean owned, float h) {
        VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        
        style.font = new BitmapFont();
        style.font.getData().setScale(h * Constants.BUY_BUTTON_FONT_SCALE); // Asegúrate de tener esta constante o usa una genérica
        
        if (owned) {
            style.fontColor = Color.YELLOW;
            style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("16A085FF")); // Verde
            btn.setDisabled(true);
        } else {
            style.fontColor = Color.WHITE;
            style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF")); // Azul
            btn.setDisabled(false);
        }
        btn.setStyle(style);
    }

    private void setupBackButton(float w, float h) {
        VisTextButton.VisTextButtonStyle backStyle = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        backStyle.font = new BitmapFont();
        backStyle.font.getData().setScale(h * Constants.BUTTON_FONT_SCALE_SHOP);
        backStyle.fontColor = Color.WHITE;
        backStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("8E44ADFF"));

        VisTextButton back = new VisTextButton("Regresar", backStyle);
        back.setSize(w * Constants.BACK_WIDTH, h * Constants.BACK_HEIGHT);
        back.setPosition(w * Constants.BACK_POS_X, h * Constants.BACK_POS_Y);
        stage.addActor(back);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    private void setupCannonShopButton(float w, float h) {
        VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        style.font = new BitmapFont();
        style.font.getData().setScale(h * Constants.BUTTON_FONT_SCALE_SHOP);
        style.fontColor = Color.WHITE;
        style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2980B9FF"));

        VisTextButton btn = new VisTextButton("Skins de Cañones", style);
        btn.setSize(w * Constants.CANNON_BTN_WIDTH, h * Constants.CANNON_BTN_HEIGHT);
        btn.setPosition(w * Constants.CANNON_BTN_POS_X, h * Constants.CANNON_BTN_POS_Y);
        stage.addActor(btn);

        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new ShopSkinsScreen(game));
            }
        });
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
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
        background.dispose();
    }
}