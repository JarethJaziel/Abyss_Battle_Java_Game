package io.github.jarethjaziel.abyssbattle.screens;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import io.github.jarethjaziel.abyssbattle.util.SkinType;
import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.database.SessionManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.systems.UserInventorySystem;

public class MyTroopSkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;
    
    private UserInventorySystem inventorySystem;
    private User currentUser;

    // Lista para actualizar botones visualmente después del click
    private Map<Integer, VisTextButton> buttonMap;

    public MyTroopSkinsScreen(AbyssBattle game) {
        this.game = game;
        this.inventorySystem = new UserInventorySystem(game.getDbManager());
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        this.buttonMap = new HashMap<>();

        stage = new Stage(new ScreenViewport());
        background = new Texture("images/TropSkinsShop2.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        // --- ESTILOS (Igual que antes) ---
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * Constants.TITLE_SCALE);
        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(h * Constants.SKIN_NAME_SCALE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);

        // --- TÍTULO ---
        Label title = new Label("Inventario de Tropas", titleStyle);
        title.setPosition(w * Constants.TITLE_POS_X, h * Constants.TITLE_POS_Y);
        stage.addActor(title);

        // --- BOTÓN REGRESAR ---
        setupBackButton(w, h); // (Extraje código para limpieza, ver abajo)

        // --- BOTÓN IR A CAÑONES ---
        setupCannonLinkButton(w, h);

        // ============================================================
        //     LÓGICA DE CARGA DINÁMICA DE SKINS DESDE DB
        // ============================================================

        // 1. Obtener datos reales
        List<Skin> ownedSkins = inventorySystem.getOwnedSkinsByType(currentUser, SkinType.TROOP);
        Skin currentEquipped = inventorySystem.getEquippedSkin(currentUser, SkinType.TROOP);
        int equippedId = (currentEquipped != null) ? currentEquipped.getId() : -1;

        // 2. Generar UI por cada skin que tengo
        for (int i = 0; i < ownedSkins.size(); i++) {
            Skin skin = ownedSkins.get(i);
            
            float x = w * Constants.SKIN_START_X + i * w * Constants.SKIN_OFFSET_X;

            // A. Nombre de la Skin
            Label skinLabel = new Label(skin.getName(), skinStyle);
            skinLabel.setPosition(x + w * Constants.SKIN_LABEL_OFFSET_X, h * Constants.SKIN_LABEL_Y);
            stage.addActor(skinLabel);

            // B. Botón de Equipar
            VisTextButton equipBtn = createEquipButton(h);
            equipBtn.setSize(w * Constants.EQUIP_BUTTON_WIDTH, h * Constants.EQUIP_BUTTON_HEIGHT);
            equipBtn.setPosition(x + w * Constants.EQUIP_BUTTON_OFFSET_X, h * Constants.EQUIP_BUTTON_Y);

            // Texto inicial
            boolean isEquipped = (skin.getId() == equippedId);
            updateButtonLook(equipBtn, isEquipped);
            
            // Guardamos referencia para actualizar luego
            buttonMap.put(skin.getId(), equipBtn);

            // C. Listener del botón
            equipBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        // 1. Guardar en Base de Datos
                        inventorySystem.equipSkin(currentUser, skin);
                        
                        // 2. Actualizar UI Visualmente
                        refreshButtons(skin.getId());
                        
                        System.out.println("Skin equipada: " + skin.getName());
                        
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            stage.addActor(equipBtn);
        }
    }

    // --- MÉTODOS AUXILIARES PARA LIMPIEZA ---

    private void refreshButtons(int newEquippedId) {
        // Recorre todos los botones y pone "Equipar" salvo al nuevo "Equipado"
        for (Map.Entry<Integer, VisTextButton> entry : buttonMap.entrySet()) {
            int skinId = entry.getKey();
            VisTextButton btn = entry.getValue();
            updateButtonLook(btn, skinId == newEquippedId);
        }
    }

    private void updateButtonLook(VisTextButton btn, boolean isEquipped) {
        if (isEquipped) {
            btn.setText("Equipado");
            btn.setDisabled(true);    
        } else {
            btn.setText("Equipar");
            btn.setDisabled(false);
        }
        btn.setColor(Color.WHITE);
    }

    private VisTextButton createEquipButton(float h) {
        VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        style.font = new BitmapFont();
        style.font.getData().setScale(h * Constants.EQUIP_BUTTON_FONT_SCALE);
        style.fontColor = Color.WHITE;
        style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50FF"));
        style.disabled = VisUI.getSkin().newDrawable("white", Color.valueOf("27AE60FF")); // Color para "Equipado"
        return new VisTextButton("", style);
    }

    private void setupBackButton(float w, float h) {
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
    }
    
    private void setupCannonLinkButton(float w, float h) {
         VisTextButton.VisTextButtonStyle cannonSkinStyle =

                new VisTextButton.VisTextButtonStyle(

                        VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)

                );



        cannonSkinStyle.font = new BitmapFont();

        cannonSkinStyle.font.getData().setScale(h * Constants.CANNON_BUTTON_FONT_SCALE);

        cannonSkinStyle.fontColor = Color.WHITE;

        cannonSkinStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2980B9FF"));



        VisTextButton cannonButton = new VisTextButton("Skins de Cañones", cannonSkinStyle);

        cannonButton.setSize(w * Constants.CANNON_BUTTON_WIDTH, h * Constants.CANNON_BUTTON_HEIGHT);

        cannonButton.setPosition(w * Constants.CANNON_BUTTON_POS_X, h * (Constants.CANNON_BUTTON_POS_Y - 0.12f));

        stage.addActor(cannonButton);



        cannonButton.addListener(event -> {

            if (event.toString().equals("touchDown")) {

                game.setScreen(new MySkinsScreen(game));

            }

            return true;

        });    
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().setColor(Color.WHITE);
        stage.getBatch().draw(background, 0, 0, stage.getViewport().getWorldWidth(), stage.getViewport().getWorldHeight());
        stage.getBatch().end();

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }
}
