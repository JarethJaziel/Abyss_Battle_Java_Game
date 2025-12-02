package io.github.jarethjaziel.abyssbattle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.database.systems.AccountSystem;
import io.github.jarethjaziel.abyssbattle.screens.MainMenuScreen;

import java.sql.SQLException;

public class AbyssBattle extends Game {

    public SpriteBatch batch;
    public AssetManager assets;

    // Sistema de Base de Datos
    public DatabaseManager dbManager;
    public AccountSystem accountSystem;

    @Override
    public void create() {
        // 1. Inicializar VisUI primero
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        System.out.println("🎮 Iniciando Abyss Battle...");

        // 2. Inicializar Base de Datos y Sistema de Cuentas
        try {
            dbManager = new DatabaseManager();
            dbManager.connect();
            accountSystem = new AccountSystem(dbManager);
            System.out.println("✅ Sistema de base de datos inicializado correctamente");
        } catch (SQLException e) {
            System.err.println("❌ ERROR FATAL: No se pudo conectar a la base de datos");
            e.printStackTrace();
            Gdx.app.exit();
            return;
        }

        // 3. Cargar Assets
        assets = new AssetManager();

        // Sprites
        assets.load("sprites/cannon_base.png", Texture.class);
        assets.load("sprites/cannon_barrel.png", Texture.class);
        assets.load("sprites/projectile.png", Texture.class);
        assets.load("sprites/shadow.png", Texture.class);
        assets.load("sprites/troop_blue.png", Texture.class);
        assets.load("sprites/troop_red.png", Texture.class);

        // VFX
        assets.load("vfx/explosion1.png", Texture.class);
        assets.load("vfx/explosion2.png", Texture.class);
        assets.load("vfx/explosion3.png", Texture.class);

        // Images
        assets.load("images/MenuBackGround.png", Texture.class);
        assets.load("images/ShopSkins.jpeg", Texture.class);
        assets.load("images/SkinsShop2.png", Texture.class);
        assets.load("images/SkinsStock.png", Texture.class);
        assets.load("images/game_bg_1.png", Texture.class);

        // Audio
        assets.load("sfx/shoot.mp3", Sound.class);
        assets.load("sfx/boom.mp3", Sound.class);
        assets.load("music/game_music.mp3", Music.class);

        // Esperar a que carguen todos los assets
        assets.finishLoading();
        System.out.println("✅ Assets cargados correctamente");

        // 4. Inicializar SpriteBatch
        batch = new SpriteBatch();

        // 5. Ir al menú principal
        System.out.println("✅ Todo listo, mostrando menú principal");
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        System.out.println("🛑 Cerrando Abyss Battle...");

        if (batch != null) {
            batch.dispose();
        }

        if (assets != null) {
            assets.dispose();
        }

        if (VisUI.isLoaded()) {
            VisUI.dispose();
        }

        // Cerrar conexión a base de datos
        if (dbManager != null) {
            dbManager.close();
            System.out.println("✅ Base de datos cerrada correctamente");
        }

        System.out.println("✅ Abyss Battle cerrado correctamente");
    }
}
