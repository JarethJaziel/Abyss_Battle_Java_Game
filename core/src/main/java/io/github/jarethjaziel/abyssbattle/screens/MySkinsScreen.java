package io.github.jarethjaziel.abyssbattle.screens; // Adjust package as needed

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

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.systems.UserInventorySystem;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.SessionManager;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

public class MySkinsScreen extends ScreenAdapter {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    // Database Systems
    private UserInventorySystem inventorySystem;
    private User currentUser;

    // UI State Management
    private Map<Integer, VisTextButton> buttonMap;

    public MySkinsScreen(AbyssBattle game) {
        this.game = game;
        
        // 1. Initialize DB Connection and User
        this.inventorySystem = new UserInventorySystem(game.getDbManager());
        this.currentUser = SessionManager.getInstance().getCurrentUser();
        this.buttonMap = new HashMap<>();

        stage = new Stage(new ScreenViewport());
        background = new Texture("images/ShopSkins.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        float w = stage.getViewport().getWorldWidth();
        float h = stage.getViewport().getWorldHeight();

        // --- STYLES ---
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * Constants.TITLE_SCALE);

        BitmapFont skinFont = new BitmapFont();
        skinFont.getData().setScale(h * Constants.SKIN_NAME_SCALE);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);
        Label.LabelStyle skinStyle = new Label.LabelStyle(skinFont, Color.BLACK);

        // --- TITLE ---
        Label title = new Label("Inventario de Cañones", titleStyle); // Updated Title
        title.setPosition(w * Constants.TITLE_POS_X, h * Constants.TITLE_POS_Y);
        stage.addActor(title);

        // --- BACK BUTTON ---
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

        // --- NAVIGATION BUTTON (To Troop Skins) ---
        VisTextButton.VisTextButtonStyle tropSkinStyle = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        tropSkinStyle.font = new BitmapFont();
        tropSkinStyle.font.getData().setScale(h * Constants.TROP_BUTTON2_FONT_SCALE);
        tropSkinStyle.fontColor = Color.WHITE;
        tropSkinStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2980B9FF"));

        VisTextButton troopsButton = new VisTextButton("Skins de Tropas", tropSkinStyle);
        troopsButton.setSize(w * Constants.TROP_BUTTON2_WIDTH, h * Constants.TROP_BUTTON2_HEIGHT);
        troopsButton.setPosition(w * Constants.TROP_BUTTON2_POS_X, h * (Constants.TROP_BUTTON2_POS_Y - 0.12f));
        
        troopsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MyTroopSkinsScreen(game));
            }
        });
        stage.addActor(troopsButton);

        // ============================================================
        //     DYNAMIC LOADING LOGIC (CANNONS)
        // ============================================================

        // 1. Fetch Owned Cannons from DB
        List<Skin> ownedSkins = inventorySystem.getOwnedSkinsByType(currentUser, SkinType.CANNON);
        
        // 2. Check what is currently equipped
        Skin currentEquipped = inventorySystem.getEquippedSkin(currentUser, SkinType.CANNON);
        int equippedId = (currentEquipped != null) ? currentEquipped.getId() : -1;

        // 3. Generate UI dynamically
        for (int i = 0; i < ownedSkins.size(); i++) {
            Skin skin = ownedSkins.get(i);
            
            float x = w * Constants.SKIN_START_X + i * w * Constants.SKIN_OFFSET_X;

            // A. Label Name
            Label skinLabel = new Label(skin.getName(), skinStyle);
            skinLabel.setPosition(x + w * Constants.SKIN_LABEL_OFFSET_X, h * Constants.SKIN_LABEL_Y);
            stage.addActor(skinLabel);

            // B. Equip Button
            VisTextButton equipBtn = createEquipButton(h);
            equipBtn.setSize(w * Constants.EQUIP_BUTTON_WIDTH, h * Constants.EQUIP_BUTTON_HEIGHT);
            equipBtn.setPosition(x + w * Constants.EQUIP_BUTTON_OFFSET_X, h * Constants.EQUIP_BUTTON_Y);

            // Set initial state
            boolean isEquipped = (skin.getId() == equippedId);
            updateButtonLook(equipBtn, isEquipped);
            
            buttonMap.put(skin.getId(), equipBtn);

            // C. Listener
            equipBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    try {
                        // Update Database
                        inventorySystem.equipSkin(currentUser, skin);
                        
                        // Update UI
                        refreshButtons(skin.getId());
                        
                        System.out.println("Cañón equipado: " + skin.getName());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });

            stage.addActor(equipBtn);
        }
    }

    // --- HELPER METHODS ---

    private void refreshButtons(int newEquippedId) {
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
        // Optional: Style for disabled state (greenish)
        style.disabled = VisUI.getSkin().newDrawable("white", Color.valueOf("27AE60FF")); 
        return new VisTextButton("", style);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().setColor(Color.WHITE);
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
        background.dispose();
    }
}