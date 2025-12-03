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
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
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
import io.github.jarethjaziel.abyssbattle.util.SkinType;


public class InventoryScreen extends ScreenAdapter {

    private final AbyssBattle game;
    private Stage stage;
    private Texture background;

    // Backend
    private final UserInventorySystem inventorySystem;
    private final User currentUser;

    // UI State: Mapeamos ID de Skin -> Botón (Para actualizar texto "Equipado")
    // Usamos listas separadas por tipo para refrescar solo la sección correcta
    private final Map<Integer, VisTextButton> cannonButtonsMap = new HashMap<>();
    private final Map<Integer, VisTextButton> troopButtonsMap = new HashMap<>();

    // Estilos cacheados
    private Label.LabelStyle sectionTitleStyle;
    private Label.LabelStyle skinNameStyle;
    private VisTextButton.VisTextButtonStyle equipButtonStyle;
    private VisTextButton.VisTextButtonStyle backButtonStyle;

    public InventoryScreen(AbyssBattle game) {
        this.game = game;
        this.inventorySystem = new UserInventorySystem(game.getDbManager());
        this.currentUser = SessionManager.getInstance().getCurrentUser();

        stage = new Stage(new ScreenViewport());
        background = new Texture("images/ShopSkins.png"); // Fondo genérico
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        
        // Preparar Estilos (Fuentes responsivas)
        initStyles();

        // --- LAYOUT PRINCIPAL (TABLA RAÍZ) ---
        VisTable rootTable = new VisTable();
        rootTable.setFillParent(true);
        rootTable.top();
        
        // 1. Cabecera (Título y Botón Regresar)
        VisTable headerTable = new VisTable();
        
        Label titleLabel = new Label("MI INVENTARIO", sectionTitleStyle);
        titleLabel.setColor(Color.GOLD);
        
        VisTextButton backButton = new VisTextButton("Regresar", backButtonStyle);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        headerTable.add(backButton).left().pad(20);
        headerTable.add(titleLabel).expandX().center().padRight(100); // Hack para centrar visualmente
        
        rootTable.add(headerTable).growX().padTop(20).row();

        // 2. Contenedor de Scroll (Para que quepan muchas skins)
        VisTable contentTable = new VisTable();
        contentTable.top();
        contentTable.pad(20);

        // --- SECCIÓN CAÑONES ---
        buildSection(contentTable, SkinType.CANNON, "CAÑONES", cannonButtonsMap);
        
        contentTable.row().padTop(40); // Espacio entre secciones
        
        // --- SECCIÓN TROPAS ---
        buildSection(contentTable, SkinType.TROOP, "TROPAS", troopButtonsMap);

        // Crear el ScrollPane
        VisScrollPane scrollPane = new VisScrollPane(contentTable);
        scrollPane.setScrollingDisabled(true, false); // Solo scroll vertical
        scrollPane.setFadeScrollBars(false);

        // Añadir Scroll al Root
        rootTable.add(scrollPane).grow().padTop(10);

        stage.addActor(rootTable);
    }

    /**
     * Construye una sección completa (Título + Grid de items)
     */
    private void buildSection(VisTable parentTable, SkinType type, String title, Map<Integer, VisTextButton> buttonMap) {
        // 1. Título de Sección
        Label label = new Label(title, sectionTitleStyle);
        label.setColor(Color.CYAN);
        label.setAlignment(Align.left);
        parentTable.add(label).left().padBottom(10).row();

        // 2. Grid de Ítems
        // Usamos una tabla interna para organizar los items en cuadrícula
        VisTable gridTable = new VisTable();
        
        List<Skin> ownedSkins = inventorySystem.getOwnedSkinsByType(currentUser, type);
        Skin currentEquipped = inventorySystem.getEquippedSkin(currentUser, type);
        int equippedId = (currentEquipped != null) ? currentEquipped.getId() : -1;

        if (ownedSkins.isEmpty()) {
            gridTable.add(new Label("No tienes skins de este tipo.", skinNameStyle)).pad(20);
        } else {
            int columns = 0;
            // Loop para crear cada tarjeta de skin
            for (Skin skin : ownedSkins) {
                VisTable itemCard = createItemCard(skin, equippedId, buttonMap);
                
                // Añadir a la tabla grid
                gridTable.add(itemCard).pad(15).width(200); // Ancho fijo por tarjeta
                
                columns++;
                // Romper fila cada 4 elementos (Responsive simple)
                if (columns >= 4) {
                    gridTable.row();
                    columns = 0;
                }
            }
        }

        parentTable.add(gridTable).left().growX().row();
    }

    /**
     * Crea la tarjeta visual de una skin individual (Nombre + Botón)
     */
    private VisTable createItemCard(Skin skin, int equippedId, Map<Integer, VisTextButton> mapToRegister) {
        VisTable card = new VisTable();
        // card.setBackground("window-border"); // Opcional si tienes un drawable de borde

        // Nombre
        Label nameLabel = new Label(skin.getName(), skinNameStyle);
        nameLabel.setWrap(true);
        nameLabel.setAlignment(Align.center);
        
        // Botón
        boolean isEquipped = (skin.getId() == equippedId);
        VisTextButton btn = new VisTextButton("", equipButtonStyle);
        updateButtonState(btn, isEquipped);
        
        // Registrar en el mapa para actualizaciones futuras
        mapToRegister.put(skin.getId(), btn);

        // Lógica del botón
        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleEquipAction(skin, mapToRegister);
            }
        });

        // Layout de la tarjeta
        card.add(nameLabel).width(180).center().padBottom(10).row();
        card.add(btn).width(160).height(50).center();
        
        return card;
    }

    private void handleEquipAction(Skin skinToEquip, Map<Integer, VisTextButton> mapToUpdate) {
        try {
            // 1. Base de Datos
            inventorySystem.equipSkin(currentUser, skinToEquip);
            System.out.println("Equipado: " + skinToEquip.getName());

            // 2. Actualizar UI (Visual)
            // Recorremos SOLO los botones de esta categoría (mapToUpdate)
            for (Map.Entry<Integer, VisTextButton> entry : mapToUpdate.entrySet()) {
                int id = entry.getKey();
                VisTextButton button = entry.getValue();
                
                // Si es el ID que acabamos de equipar -> Estado "Equipado"
                // Si no -> Estado "Equipar"
                updateButtonState(button, id == skinToEquip.getId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonState(VisTextButton btn, boolean isEquipped) {
        if (isEquipped) {
            btn.setText("EQUIPADO");
            btn.setDisabled(true);
            btn.setColor(Color.GREEN); // Tinte verde
        } else {
            btn.setText("EQUIPAR");
            btn.setDisabled(false);
            btn.setColor(Color.WHITE); // Tinte normal
        }
    }

    private void initStyles() {
        float h = Gdx.graphics.getHeight();

        // Fuentes
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * 0.002f); // Escala dinámica
        
        BitmapFont itemFont = new BitmapFont();
        itemFont.getData().setScale(h * 0.0015f);

        // Estilos Labels
        sectionTitleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        skinNameStyle = new Label.LabelStyle(itemFont, Color.WHITE);

        // Estilo Botones
        equipButtonStyle = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        equipButtonStyle.font = itemFont;
        equipButtonStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("34495E"));
        equipButtonStyle.disabled = VisUI.getSkin().newDrawable("white", Color.valueOf("27AE60")); // Verde para equipado

        backButtonStyle = new VisTextButton.VisTextButtonStyle(equipButtonStyle);
        backButtonStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("C0392B")); // Rojo para volver
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.getBatch().begin();
        stage.getBatch().setColor(Color.WHITE);
        stage.getBatch().draw(background, 0, 0, stage.getWidth(), stage.getHeight());
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