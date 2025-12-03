package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisDialog;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.SessionManager;

public class MainMenuScreen implements Screen {

    private final AbyssBattle game;
    private Stage stage;
    private Texture background;

    // Elementos UI
    private VisTextButton loginButton;

    public MainMenuScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/MenuBackGround.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        // --- LAYOUT PRINCIPAL ---
        VisTable root = new VisTable();
        root.setFillParent(true);
        root.pad(20);
        
        root.right().padRight(60);
        
        Label titleLabel = createTitleLabel();
        
        titleLabel.setAlignment(Align.right); 
        root.add(titleLabel).padBottom(50).row();

        Table buttonTable = new Table();
        
        // A. Botón Principal (JUGAR) - ROJO INTENSO
        // Color.valueOf("003300") es un verde  agradable, o usa Color.RED
        VisTextButton playBtn = createButton("¡A LA BATALLA!", Color.valueOf("003300"), 1.2f);
        playBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (checkLogin()) game.setScreen(new GameSetupScreen(game));
            }
        });
        buttonTable.add(playBtn).width(300).height(80).padBottom(20).row();

        // B. Fila de Gestión (TIENDA | INVENTARIO) - GRISES
        Table managementRow = new Table();
        
        // Usamos Color.GRAY o DARK_GRAY
        VisTextButton shopBtn = createButton("Tienda", Color.DARK_GRAY, 0.8f);
        shopBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (checkLogin()) game.setScreen(new ShopScreen(game));
            }
        });

        VisTextButton invBtn = createButton("Inventario", Color.DARK_GRAY, 0.8f);
        invBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (checkLogin()) game.setScreen(new InventoryScreen(game));
            }
        });

        managementRow.add(shopBtn).width(140).height(60).padRight(20);
        managementRow.add(invBtn).width(140).height(60);
        
        buttonTable.add(managementRow).padBottom(20).row();

        // C. Botón de Perfil / Stats (Naranja para resaltar diferente)
        VisTextButton statsBtn = createButton("Mi Perfil & Stats", Color.BROWN, 0.8f);
        statsBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                if (checkLogin()) {
                    game.setScreen(new ProfileScreen(game));
                }
            }
        });
        buttonTable.add(statsBtn).width(300).height(60).padBottom(40).row();

        // D. Fila de Sistema (LOGIN | SALIR)
        Table systemRow = new Table();
        
        loginButton = createButton("Iniciar Sesión", Color.LIGHT_GRAY, 0.7f);
        updateLoginButtonText();
        loginButton.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                handleLoginAction();
            }
        });

        VisTextButton exitBtn = createButton("Salir", Color.FIREBRICK, 0.7f);
        exitBtn.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });

        systemRow.add(loginButton).width(140).padRight(20);
        systemRow.add(exitBtn).width(140);
        
        buttonTable.add(systemRow);

        // Añadir la tabla de botones al root (Alineada a la derecha por el root.right())
        root.add(buttonTable);

        stage.addActor(root);
    }

    // --- MÉTODOS HELPERS DE UI ---

    private Label createTitleLabel() {
        BitmapFont font = new BitmapFont();
        font.getData().setScale(Gdx.graphics.getHeight() * 0.005f); 
        Label.LabelStyle style = new Label.LabelStyle(font, Color.CYAN);
        Label label = new Label("ABYSS BATTLE", style);
        
        // Animación "Pulse"
        label.setOrigin(Align.center);
        label.addAction(Actions.forever(
            Actions.sequence(
                Actions.scaleTo(1.05f, 1.05f, 1f),
                Actions.scaleTo(1f, 1f, 1f)
            )
        ));
        return label;
    }

    private VisTextButton createButton(String text, Color color, float fontScale) {
        VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class));
        
        style.font = new BitmapFont();
        style.font.getData().setScale(Gdx.graphics.getHeight() * 0.002f * fontScale);
        style.fontColor = Color.WHITE;
        
        // Fondo normal
        style.up = VisUI.getSkin().newDrawable("white", color);
        
        // Brillo al pasar mouse (color más claro)
        style.over = VisUI.getSkin().newDrawable("white", color.cpy().lerp(Color.WHITE, 0.2f)); 
        
        // Oscuro al click
        style.down = VisUI.getSkin().newDrawable("white", color.cpy().mul(0.8f)); 

        return new VisTextButton(text, style);
    }

    // --- LÓGICA DE LOGIN ---

    private void handleLoginAction() {
        if (SessionManager.getInstance().isLoggedIn()) {
            SessionManager.getInstance().logout();
            updateLoginButtonText();
        } else {
            game.setScreen(new LoginScreen(game));
        }
    }

    private void updateLoginButtonText() {
        if (SessionManager.getInstance().isLoggedIn()) {
            loginButton.setText("Cerrar Sesión");
            loginButton.setColor(Color.SALMON); 
        } else {
            loginButton.setText("Iniciar Sesión");
            loginButton.setColor(Color.LIGHT_GRAY);
        }
    }

    private boolean checkLogin() {
        if (SessionManager.getInstance().isLoggedIn()) {
            return true;
        } else {
            showLoginWarning();
            return false;
        }
    }

    private void showLoginWarning() {
        VisDialog dialog = new VisDialog("Acceso Restringido");
        dialog.text("Debes iniciar sesión para acceder a esta sección.");
        dialog.button("Entendido");
        dialog.pack();
        dialog.centerWindow();
        dialog.show(stage);
    }

    // --- STANDARD METHODS ---

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
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
    
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}