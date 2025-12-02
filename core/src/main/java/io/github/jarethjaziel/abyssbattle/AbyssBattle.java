package io.github.jarethjaziel.abyssbattle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.screens.LoadingScreen;

import java.sql.SQLException;

public class AbyssBattle extends Game {

    public SpriteBatch batch;
    public AssetManager assets;

    // Sistema de Base de Datos
    private DatabaseManager dbManager;


    @Override
    public void create() {
        // 1. Inicializar VisUI primero
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        System.out.println("Iniciando Abyss Battle...");

        // 2. Inicializar Base de Datos y Sistema de Cuentas
        try {
            dbManager = new DatabaseManager();
            dbManager.connect();
            System.out.println("Sistema de base de datos inicializado correctamente");
        } catch (SQLException e) {
            System.err.println("ERROR FATAL: No se pudo conectar a la base de datos");
            e.printStackTrace();
            Gdx.app.exit();
            return;
        }

        // 3. Cargar Assets
        assets = new AssetManager();

        batch = new SpriteBatch();

        setScreen(new LoadingScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        System.out.println("Cerrando Abyss Battle...");
        super.dispose();

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
            System.out.println("Base de datos cerrada correctamente");
        }

        System.out.println("Abyss Battle cerrado correctamente");
    }

    

    public DatabaseManager getDbManager() {
        return dbManager;
    }

}
