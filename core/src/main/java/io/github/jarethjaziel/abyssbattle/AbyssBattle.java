package io.github.jarethjaziel.abyssbattle;

import java.sql.SQLException;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.j256.ormlite.dao.Dao;
import com.kotcrab.vis.ui.VisUI;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.Stats;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;
import io.github.jarethjaziel.abyssbattle.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AbyssBattle extends Game {

    private DatabaseManager dbManager;
    private User user;

    public SpriteBatch batch;
    public AssetManager assets;

    @Override
    public void create() {
        VisUI.load();
        dbManager = new DatabaseManager();
        try {
            dbManager.connect();
        } catch (SQLException e) {
            System.out.println("Error al conectar a la base de datos: " + e.getMessage());
        }
        
        try {
            user = getUserDao().queryForId(1);
        } catch (SQLException e) {
            user = null;
        }

        Gdx.app.log("DB", "Usuario cargado: " + user);

        batch = new SpriteBatch();
        assets = new AssetManager();

        // 1. Cargar Imágenes
        assets.load("sprites/cannon_base.png", Texture.class);
        assets.load("sprites/cannon_barrel.png", Texture.class);
        assets.load("sprites/projectile.png", Texture.class);
        assets.load("sprites/shadow.png", Texture.class);
        assets.load("sprites/troop_blue.png", Texture.class); // P1
        assets.load("sprites/troop_red.png", Texture.class);  // P2
        
        // 2. Cargar Sonidos y Música
        assets.load("music/game_music.mp3", Music.class);
        assets.load("sfx/boom.mp3", Sound.class);
        assets.load("sfx/shoot.mp3", Sound.class);

        assets.finishLoading();

        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose () {
        VisUI.dispose();
        super.dispose(); 
        dbManager.close();
    }

    public Dao<User, Integer> getUserDao(){
        return dbManager.getUserDao();
    }

    public Dao<Stats, Integer> getStatsDao() {
        return dbManager.getStatsDao();
    }

    public Dao<Skin, Integer> getSkinDao() {
        return dbManager.getSkinDao();
    }

    public Dao<UserSkin, Integer> getUserSkinDao() {
        return dbManager.getUserSkinDao();
    }

    public User getUser() {
        return user;
    }

}