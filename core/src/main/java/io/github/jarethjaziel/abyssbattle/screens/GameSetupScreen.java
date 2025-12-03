package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisSelectBox;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.List;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.systems.UserInventorySystem;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.SessionManager;
import io.github.jarethjaziel.abyssbattle.model.MatchContext;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

public class GameSetupScreen extends ScreenAdapter {

    private final AbyssBattle game;
    private Stage stage;
    private Texture background;

    private final UserInventorySystem inventorySystem;
    private final User currentUser;

    // Componentes de Selección
    private VisSelectBox<String> p2SkinSelect;
    private VisSelectBox<String> cannonSelect;

    // Mapas para recuperar el objeto Skin basado en el nombre seleccionado
    private final Array<Skin> troopSkinsList = new Array<>();
    private final Array<Skin> cannonSkinsList = new Array<>();

    public GameSetupScreen(AbyssBattle game) {
        this.game = game;
        this.inventorySystem = new UserInventorySystem(game.getDbManager());
        this.currentUser = SessionManager.getInstance().getCurrentUser();

        stage = new Stage(new ScreenViewport());
        background = new Texture("images/MenuBackGround.png"); // Reusa tu fondo
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        VisTable root = new VisTable();
        root.setFillParent(true);
        root.center();

        // 1. Título
        Label title = new Label("CONFIGURACIÓN DE PARTIDA", VisUI.getSkin());
        title.setColor(Color.GOLD);
        title.setFontScale(1.5f); // Ajusta según tu fuente
        root.add(title).padBottom(30).colspan(2).row();

        // ---------------------------------------------------------
        // CARGAR DATOS
        // ---------------------------------------------------------

        // A. Skin P1 (Equipada Actualmente)
        Skin p1Equipped = inventorySystem.getEquippedSkin(currentUser, SkinType.TROOP);
        String p1Name = (p1Equipped != null) ? p1Equipped.getName() : "Default";
        int p1SkinId = (p1Equipped != null) ? p1Equipped.getId() : -1;

        // B. Lista de Tropas (Para P2)
        List<Skin> ownedTroops = inventorySystem.getOwnedSkinsByType(currentUser, SkinType.TROOP);
        Array<String> troopNames = new Array<>();
        for (Skin s : ownedTroops) {
            if (s.getId() != p1SkinId) {
                troopSkinsList.add(s);
                troopNames.add(s.getName());
            }
        }

        // C. Lista de Cañones (Para ambos)
        List<Skin> ownedCannons = inventorySystem.getOwnedSkinsByType(currentUser, SkinType.CANNON);
        Array<String> cannonNames = new Array<>();
        for (Skin s : ownedCannons) {
            cannonSkinsList.add(s);
            cannonNames.add(s.getName());
        }

        // ---------------------------------------------------------
        // UI DE SELECCIÓN
        // ---------------------------------------------------------

        // Fila 1: Jugador 1 (Solo lectura)
        root.add(new Label("Tu Skin (P1):", VisUI.getSkin())).right().padRight(10);
        Label p1Label = new Label(p1Name, VisUI.getSkin());
        p1Label.setColor(Color.CYAN);
        root.add(p1Label).left().row();

        // Fila 2: Jugador 2 (Selector)
        root.add(new Label("Skin Oponente (P2):", VisUI.getSkin())).right().padRight(10).padTop(10);
        p2SkinSelect = new VisSelectBox<>();
        p2SkinSelect.setItems(troopNames);
        root.add(p2SkinSelect).left().width(200).padTop(10).row();

        // Fila 3: Cañones (Selector)
        root.add(new Label("Modelo de Cañón:", VisUI.getSkin())).right().padRight(10).padTop(10);
        cannonSelect = new VisSelectBox<>();
        cannonSelect.setItems(cannonNames);
        root.add(cannonSelect).left().width(200).padTop(10).row();

        // ---------------------------------------------------------
        // BOTÓN JUGAR
        // ---------------------------------------------------------
        VisTextButton playBtn = new VisTextButton("¡A LA BATALLA!");
        playBtn.setColor(Color.GREEN);

        playBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                startGame(p1Equipped);
            }
        });

        root.add(playBtn).colspan(2).padTop(40).width(200).height(50).row();

        // Botón Volver
        VisTextButton backBtn = new VisTextButton("Cancelar");
        backBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        root.add(backBtn).colspan(2).padTop(10).width(200);

        stage.addActor(root);
    }

    private void startGame(Skin p1Skin) {
        // Recuperar objetos Skin basados en la selección
        int p2Index = p2SkinSelect.getSelectedIndex();
        Skin p2Skin = troopSkinsList.get(p2Index);

        int cannonIndex = cannonSelect.getSelectedIndex();
        Skin cannonSkin = cannonSkinsList.get(cannonIndex);

        // Crear el contexto
        MatchContext context = new MatchContext(p1Skin, p2Skin, cannonSkin);

        // Ir al juego pasando el contexto
        game.setScreen(new GameScreen(game, context));
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
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }
}