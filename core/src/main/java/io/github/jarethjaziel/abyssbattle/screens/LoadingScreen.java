package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.gameutil.manager.AudioManager;
import io.github.jarethjaziel.abyssbattle.util.GameSkins;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

public class LoadingScreen implements Screen {

    private final AbyssBattle game;
    private Stage stage;
    private VisProgressBar progressBar;
    private VisLabel loadingLabel;

    public LoadingScreen(AbyssBattle game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());

        VisTable table = new VisTable();
        table.setFillParent(true);

        loadingLabel = new VisLabel("Cargando recursos...");
        progressBar = new VisProgressBar(0, 1, 0.01f, false);
        progressBar.setWidth(400);

        table.add(loadingLabel).padBottom(10).row();
        table.add(progressBar).width(300);

        stage.addActor(table);
        // Sprites
        game.assets.load("sprites/cannon_base.png", Texture.class);
        game.assets.load("sprites/projectile.png", Texture.class);
        game.assets.load("sprites/shadow.png", Texture.class);

        // VFX
        game.assets.load("vfx/explosion1.png", Texture.class);
        game.assets.load("vfx/explosion2.png", Texture.class);
        game.assets.load("vfx/explosion3.png", Texture.class);

        // Images
        game.assets.load("images/MenuBackGround.png", Texture.class);
        game.assets.load("images/ShopSkins.png", Texture.class);
        game.assets.load("images/SkinsStock.png", Texture.class);
        game.assets.load("images/game_bg_1.png", Texture.class);

        // Audio
        game.assets.load("sfx/shoot.mp3", Sound.class);
        game.assets.load("sfx/boom.mp3", Sound.class);
        game.assets.load("music/game_music.mp3", Music.class);

        for (GameSkins skinDef : GameSkins.values()) {

            String folder = "";

            if (skinDef.getType() == SkinType.TROOP) {
                folder = "sprites/troop_skin/";
            } else if (skinDef.getType() == SkinType.CANNON) {
                folder = "sprites/cannon_skin/";
            }

            String path = folder + skinDef.getName() + ".png";

            game.assets.load(path, Texture.class);
        }

        AudioManager.getInstance().initialize(game.assets);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1); // Gris oscuro
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (game.assets.update()) {
            game.assets.finishLoading();
            game.setScreen(new MainMenuScreen(game));
            dispose();
        } else {
            float progress = game.assets.getProgress();
            progressBar.setValue(progress);
            loadingLabel.setText("Cargando... " + (int) (progress * 100) + "%");
        }

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

}
