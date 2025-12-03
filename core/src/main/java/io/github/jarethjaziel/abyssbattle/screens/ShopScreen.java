package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import java.util.List;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.systems.ShopSystem;
import io.github.jarethjaziel.abyssbattle.database.systems.UserInventorySystem;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.SessionManager;
import io.github.jarethjaziel.abyssbattle.util.PurchaseResult;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

public class ShopScreen extends ScreenAdapter {

    private final AbyssBattle game;
    private Stage stage;
    private Texture background;

    private final ShopSystem shopSystem;
    private final User currentUser;

    // UI Elements
    private Label coinsLabel;

    // Estilos
    private Label.LabelStyle sectionTitleStyle;
    private Label.LabelStyle itemNameStyle;
    private Label.LabelStyle priceStyle;
    private VisTextButton.VisTextButtonStyle buyButtonStyle;
    private VisTextButton.VisTextButtonStyle backButtonStyle;

    public ShopScreen(AbyssBattle game) {
        this.game = game;
        this.shopSystem = new ShopSystem(game.getDbManager());
        this.currentUser = SessionManager.getInstance().getCurrentUser();

        stage = new Stage(new ScreenViewport());
        background = new Texture("images/ShopSkins.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        initStyles();

        // ROOT TABLE
        VisTable rootTable = new VisTable();
        rootTable.setFillParent(true);
        rootTable.top();

        // 1. CABECERA
        VisTable headerTable = new VisTable();

        VisTextButton backButton = new VisTextButton("Regresar", backButtonStyle);
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });

        Label titleLabel = new Label("TIENDA DE SKINS", sectionTitleStyle);
        titleLabel.setColor(Color.GOLD);

        // Monedas en la esquina derecha
        coinsLabel = new Label("Monedas: " + currentUser.getCoins(), priceStyle);
        coinsLabel.setColor(Color.YELLOW);

        headerTable.add(backButton).left().pad(20);
        headerTable.add(titleLabel).expandX().center();
        headerTable.add(coinsLabel).right().pad(20);

        rootTable.add(headerTable).growX().padTop(20).row();

        // 2. CONTENIDO (SCROLL)
        VisTable contentTable = new VisTable();
        contentTable.top().pad(20);

        // SECCIÓN CAÑONES
        buildShopSection(contentTable, SkinType.CANNON, "CAÑONES");
        contentTable.row().padTop(40);

        // SECCIÓN TROPAS
        buildShopSection(contentTable, SkinType.TROOP, "TROPAS");

        VisScrollPane scrollPane = new VisScrollPane(contentTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);

        rootTable.add(scrollPane).grow().padTop(10);
        stage.addActor(rootTable);
    }

    private void buildShopSection(VisTable parentTable, SkinType type, String title) {
        // Título Sección
        Label label = new Label(title, sectionTitleStyle);
        label.setColor(Color.CYAN);
        label.setAlignment(Align.left);
        parentTable.add(label).left().padBottom(15).row();

        // Grid
        VisTable gridTable = new VisTable();
        List<Skin> skinsForSale = shopSystem.getSkinsByType(type);
        UserInventorySystem invSystem = new UserInventorySystem(game.getDbManager());
        int columns = 0;
        for (Skin skin : skinsForSale) {
            boolean owned = invSystem.doesUserOwnSkin(currentUser, skin.getId());
            VisTable card = createShopCard(skin, owned);

            gridTable.add(card).pad(15).width(200);

            columns++;
            if (columns >= 4) {
                gridTable.row();
                columns = 0;
            }
        }
        parentTable.add(gridTable).left().growX().row();
    }

    private VisTable createShopCard(Skin skin, boolean owned) {
        VisTable card = new VisTable();

        // Nombre
        Label nameLabel = new Label(skin.getName(), itemNameStyle);
        nameLabel.setWrap(true);
        nameLabel.setAlignment(Align.center);

        // Precio
        Label priceLabel = new Label(skin.getPrice() + " G", priceStyle);
        priceLabel.setAlignment(Align.center);

        // Botón
        VisTextButton btn = new VisTextButton(owned ? "ADQUIRIDO" : "COMPRAR", buyButtonStyle);
        updateButtonLook(btn, owned);

        btn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (owned || btn.isDisabled())
                    return;

                handlePurchase(skin, btn);
            }
        });

        card.add(nameLabel).width(180).center().padBottom(5).row();
        card.add(priceLabel).center().padBottom(10).row();
        card.add(btn).width(160).height(50).center();

        return card;
    }

    private void handlePurchase(Skin skin, VisTextButton btn) {
        PurchaseResult result = shopSystem.buySkin(currentUser, skin.getId());

        if (result == PurchaseResult.SUCCESS) {
            System.out.println("Compra exitosa!");

            coinsLabel.setText("Monedas: " + currentUser.getCoins());

            coinsLabel.clearActions();
            coinsLabel.setColor(Color.GREEN);
            coinsLabel.addAction(Actions.color(Color.YELLOW, 0.5f)); // Vuelve a amarillo en 0.5s

            btn.setText("ADQUIRIDO");
            updateButtonLook(btn, true);

        } else if (result == PurchaseResult.INSUFFICIENT_FUNDS) {
            coinsLabel.clearActions();

            coinsLabel.setColor(Color.RED);

            coinsLabel.addAction(Actions.sequence(
                    Actions.moveBy(5, 0, 0.05f), // Pequeña vibración (shake) opcional
                    Actions.moveBy(-10, 0, 0.05f),
                    Actions.moveBy(5, 0, 0.05f),
                    Actions.color(Color.YELLOW, 1.0f) // Tardar 1 segundo en volver al color normal
            ));

            showErrorDialog("Fondos Insuficientes", "Necesitas más monedas para comprar esta skin.");
        }
    }

    private void showErrorDialog(String title, String msg) {
        VisDialog dialog = new VisDialog(title);
        dialog.text(msg);
        dialog.button("Entendido");
        dialog.pack();
        dialog.centerWindow();
        dialog.show(stage);
    }

    private void updateButtonLook(VisTextButton btn, boolean owned) {
        if (owned) {
            btn.setColor(Color.GREEN);
            btn.setDisabled(true);
        } else {
            btn.setColor(Color.WHITE);
            btn.setDisabled(false);
        }
    }

    private void initStyles() {
        float h = Gdx.graphics.getHeight();
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(h * 0.002f);

        BitmapFont itemFont = new BitmapFont();
        itemFont.getData().setScale(h * 0.0015f);

        sectionTitleStyle = new Label.LabelStyle(titleFont, Color.WHITE);
        itemNameStyle = new Label.LabelStyle(itemFont, Color.WHITE);
        priceStyle = new Label.LabelStyle(itemFont, Color.YELLOW);

        buyButtonStyle = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        buyButtonStyle.font = itemFont;
        buyButtonStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("2C3E50"));
        buyButtonStyle.disabled = VisUI.getSkin().newDrawable("white", Color.valueOf("16A085")); // Verde adquirido

        backButtonStyle = new VisTextButton.VisTextButtonStyle(buyButtonStyle);
        backButtonStyle.up = VisUI.getSkin().newDrawable("white", Color.valueOf("C0392B"));
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