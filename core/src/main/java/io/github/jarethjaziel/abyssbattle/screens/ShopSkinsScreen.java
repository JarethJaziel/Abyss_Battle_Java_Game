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
import io.github.jarethjaziel.abyssbattle.database.SessionManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.systems.ShopSystem;
import io.github.jarethjaziel.abyssbattle.database.systems.UserInventorySystem;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.PurchaseResult;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

public class ShopSkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    // --- SISTEMAS DE BACKEND ---
    private ShopSystem shopSystem;
    private UserInventorySystem inventorySystem;
    private User currentUser;
    
    // UI Elements dinámicos
    private Label coinsLabel;

    public ShopSkinsScreen(AbyssBattle game) {
        this.game = game;
        
        // 1. Inicializar conexión con DB y Usuario
        this.shopSystem = new ShopSystem(game.getDbManager());
        this.inventorySystem = new UserInventorySystem(game.getDbManager());
        this.currentUser = SessionManager.getInstance().getCurrentUser();

        stage = new Stage(new ScreenViewport());
        background = new Texture("images/ShopSkins.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        // --- ESTILOS ---
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * Constants.TITLE_SCALE);
        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(h * Constants.SKIN_NAME_SCALE);
        BitmapFont priceFont = new BitmapFont();
        priceFont.getData().setScale(h * Constants.PRICE_SCALE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.GOLD);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);
        Label.LabelStyle priceStyle = new Label.LabelStyle(priceFont, Color.YELLOW);

        // --- TÍTULO ---
        Label title = new Label("Tienda de Cañones", titleStyle);
        title.setPosition(w * Constants.TITLE_POS_X, h * Constants.TITLE_POS_Y);
        stage.addActor(title);
        
        // --- MOSTRAR MONEDAS DEL USUARIO (NUEVO) ---
        updateCoinsLabel(w, h, priceStyle); // Método helper abajo

        // --- BOTÓN REGRESAR ---
        setupBackButton(w, h);

        // --- BOTÓN IR A TROPAS ---
        setupTroopShopButton(w, h); // Botón para ir a la otra tienda

        // ============================================================
        //     CARGA DINÁMICA DE LA TIENDA (CAÑONES)
        // ============================================================

        // 1. Obtener Skins de la DB (Solo Cañones)
        List<Skin> skinsDisponibles = shopSystem.getSkinsByType(SkinType.CANNON);

        // 2. Generar UI
        for (int i = 0; i < skinsDisponibles.size(); i++) {
            Skin skin = skinsDisponibles.get(i);
            
            float x = w * Constants.SKIN_START_X + i * w * Constants.SKIN_OFFSET_X;

            // A. Nombre
            Label skinLabel = new Label(skin.getName(), skinStyle);
            skinLabel.setPosition(x + w * Constants.SKIN_LABEL_OFFSET_X, h * Constants.SKIN_LABEL_Y);
            stage.addActor(skinLabel);

            // B. Precio
            Label priceLabel = new Label(skin.getPrice() + " G", priceStyle);
            priceLabel.setPosition(x + w * Constants.SKIN_LABEL_OFFSET_X, h * Constants.PRICE_LABEL_Y);
            stage.addActor(priceLabel);

            // C. Verificar si ya la tiene
            boolean owned = inventorySystem.doesUserOwnSkin(currentUser, skin.getId());

            // D. Botón de Compra
            VisTextButton btn = createShopButton(h, owned);
            btn.setSize(w * Constants.BUY_BUTTON_WIDTH, h * Constants.BUY_BUTTON_HEIGHT);
            btn.setPosition(x + w * Constants.BUY_BUTTON_OFFSET_X, h * Constants.BUY_BUTTON_Y);

            // E. Lógica del Click
            btn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (owned) return; // Si ya la tiene, no hace nada

                    // INTENTAR COMPRAR EN DB
                    PurchaseResult result = shopSystem.buySkin(currentUser, skin.getId());

                    if (result == PurchaseResult.SUCCESS) {
                        System.out.println("¡Compra exitosa!");
                        
                        // Actualizar Botón visualmente
                        btn.setText("Adquirido");
                        // Cambiar estilo a "purchased" (verde)
                        updateButtonStyle(btn, true, h);
                        
                        // Actualizar etiqueta de monedas
                        coinsLabel.setText("Monedas: " + currentUser.getCoins());
                        
                    } else if (result == PurchaseResult.INSUFFICIENT_FUNDS) {
                        System.out.println("Fondos insuficientes");
                        // Aquí podrías mostrar un Dialog de error
                    } else {
                        System.out.println("Error en la compra: " + result);
                    }
                }
            });

            stage.addActor(btn);
        }
    }

    // --- MÉTODOS AUXILIARES UI ---

    private void updateCoinsLabel(float w, float h, Label.LabelStyle style) {
        if (coinsLabel == null) {
            coinsLabel = new Label("", style);
            coinsLabel.setPosition(w * 0.05f, h * 0.9f); // Arriba a la izquierda
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
        style.font.getData().setScale(h * Constants.BUY_BUTTON_FONT_SCALE);
        
        if (owned) {
            style.fontColor = Color.YELLOW;
            style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("16A085FF")); // Verde
            btn.setDisabled(true);
        } else {
            style.fontColor = Color.WHITE;
            style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF")); // Azul oscuro
            btn.setDisabled(false);
        }
        btn.setStyle(style);
    }

    private void setupBackButton(float w, float h) {
        VisTextButton.VisTextButtonStyle backStyle = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        backStyle.font = new BitmapFont();
        backStyle.font.getData().setScale(h * Constants.BACK_BUTTON_FONT_SCALE);
        backStyle.fontColor = Color.WHITE;
        backStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("8E44ADFF"));

        VisTextButton back = new VisTextButton("Regresar", backStyle);
        back.setSize(w * Constants.BACK_BUTTON_WIDTH, h * Constants.BACK_BUTTON_HEIGHT);
        back.setPosition(w * Constants.BACK_BUTTON_POS_X, h * Constants.BACK_BUTTON_POS_Y);

        back.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        stage.addActor(back);
    }

    private void setupTroopShopButton(float w, float h) {
        VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        style.font = new BitmapFont();
        style.font.getData().setScale(h * Constants.TROP_BUTTON_FONT_SCALE);
        style.fontColor = Color.WHITE;
        style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2980B9FF"));

        VisTextButton btn = new VisTextButton("Skins de Tropas", style);
        btn.setSize(w * Constants.TROP_BUTTON_WIDTH, h * Constants.TROP_BUTTON_HEIGHT);
        btn.setPosition(w * Constants.TROP_BUTTON_POS_X, h * Constants.TROP_BUTTON_POS_Y);

        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Asumo que tienes otra pantalla para comprar tropas
                 game.setScreen(new ShopTropSkinsScreen(game)); 
            }
        });
        stage.addActor(btn);
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