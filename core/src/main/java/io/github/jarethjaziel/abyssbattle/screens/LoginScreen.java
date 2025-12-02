package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
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
import com.kotcrab.vis.ui.widget.VisTextField;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.util.SessionManager;

public class LoginScreen implements Screen {

    private AbyssBattle game;
    private Stage stage;
    private Texture background;

    private VisTextField usernameField;
    private VisTextField passwordField;
    private Label messageLabel;

    private boolean isLoginMode = true; // true = Login, false = Registro

    public LoginScreen(AbyssBattle game) {
        this.game = game;
        stage = new Stage(new ScreenViewport());
        background = new Texture("images/MenuBackGround.png");
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);

        VisTable mainTable = new VisTable();
        mainTable.setFillParent(true);
        stage.addActor(mainTable);


        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);

        BitmapFont normalFont = new BitmapFont();
        normalFont.getData().setScale(1.5f);

        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.CYAN);
        Label.LabelStyle normalStyle = new Label.LabelStyle(normalFont, Color.WHITE);


        Label title = new Label("Iniciar Sesion", titleStyle);
        mainTable.add(title).colspan(2).padBottom(40);
        mainTable.row();


        Label usernameLabel = new Label("Usuario:", normalStyle);
        mainTable.add(usernameLabel).left().padRight(10);

        usernameField = new VisTextField("");
        usernameField.setMessageText("Ingresa tu usuario");
        mainTable.add(usernameField).width(300).height(40);
        mainTable.row();


        Label passwordLabel = new Label("Contraseña:", normalStyle);
        mainTable.add(passwordLabel).left().padRight(10).padTop(20);

        passwordField = new VisTextField("");
        passwordField.setMessageText("Ingresa tu contraseña");
        passwordField.setPasswordMode(true);
        passwordField.setPasswordCharacter('*');
        mainTable.add(passwordField).width(300).height(40).padTop(20);
        mainTable.row();


        messageLabel = new Label("", normalStyle);
        messageLabel.setColor(Color.RED);
        mainTable.add(messageLabel).colspan(2).padTop(20);
        mainTable.row();


        VisTextButton.VisTextButtonStyle buttonStyle = createButtonStyle();

        VisTextButton loginButton = new VisTextButton("Iniciar Sesion", buttonStyle);
        mainTable.add(loginButton).colspan(2).fillX().height(60).padTop(30).width(400);
        mainTable.row();

        VisTextButton toggleButton = new VisTextButton("¿No tienes cuenta? Regístrate", buttonStyle);
        mainTable.add(toggleButton).colspan(2).fillX().height(50).padTop(10).width(400);
        mainTable.row();

        VisTextButton backButton = new VisTextButton("Volver al Menu", buttonStyle);
        mainTable.add(backButton).colspan(2).fillX().height(50).padTop(10).width(400);
        mainTable.row();


        loginButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                handleLoginOrRegister();
            }
        });

        toggleButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                toggleMode(title, loginButton, toggleButton);
            }
        });

        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    private void handleLoginOrRegister() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        // Validaciones
        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Por favor completa todos los campos", Color.RED);
            return;
        }

        if (username.length() < 3) {
            showMessage("El usuario debe tener al menos 3 caracteres", Color.RED);
            return;
        }

        if (password.length() < 4) {
            showMessage("La contraseña debe tener al menos 4 caracteres", Color.RED);
            return;
        }

        if (isLoginMode) {
            // modo loggin
            User user = game.accountSystem.login(username, password);

            if (user != null) {
                SessionManager.getInstance().login(user);
                showMessage("¡Bienvenido " + username + "!", Color.GREEN);

                // Esperar 1 segundo y volver al menú
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        Gdx.app.postRunnable(() -> {
                            game.setScreen(new MainMenuScreen(game));
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                showMessage("Usuario o contraseña incorrectos", Color.RED);
            }

        } else {
            // modo de registro
            boolean success = game.accountSystem.registerUser(username, password);

            if (success) {
                showMessage("¡Cuenta creada! Ya puedes iniciar sesión", Color.GREEN);

                // Cambiar a modo login automáticamente
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);
                        Gdx.app.postRunnable(() -> {
                            isLoginMode = true;
                            usernameField.setText("");
                            passwordField.setText("");
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                showMessage("El usuario ya existe, intenta con otro", Color.RED);
            }
        }
    }

    private void toggleMode(Label title, VisTextButton mainButton, VisTextButton toggleButton) {
        isLoginMode = !isLoginMode;

        if (isLoginMode) {
            title.setText("Iniciar Sesion");
            mainButton.setText("Iniciar Sesion");
            toggleButton.setText("¿No tienes cuenta? Regístrate");
        } else {
            title.setText("Registrarse");
            mainButton.setText("Crear Cuenta");
            toggleButton.setText("¿Ya tienes cuenta? Inicia Sesión");
        }

        usernameField.setText("");
        passwordField.setText("");
        messageLabel.setText("");
    }

    private void showMessage(String text, Color color) {
        messageLabel.setText(text);
        messageLabel.setColor(color);
    }

    private VisTextButton.VisTextButtonStyle createButtonStyle() {
        VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
        );

        style.font = new BitmapFont();
        style.font.getData().setScale(1.5f);
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.YELLOW;

        style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("34495EFF"));
        style.over = VisUI.getSkin().newDrawable("white", Color.valueOf("1ABC9CFF"));
        style.down = VisUI.getSkin().newDrawable("white", Color.valueOf("2ECC71FF"));

        return style;
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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        stage.dispose();
        background.dispose();
    }
}
