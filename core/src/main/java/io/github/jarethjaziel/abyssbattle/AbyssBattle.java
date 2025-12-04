package io.github.jarethjaziel.abyssbattle;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.kotcrab.vis.ui.VisUI;

import io.github.jarethjaziel.abyssbattle.database.DatabaseManager;
import io.github.jarethjaziel.abyssbattle.screens.LoadingScreen;

import java.sql.SQLException;

/**
 * Clase principal del juego (Entry Point).
 * <p>
 * Extiende de {@link Game} y actúa como el gestor central del ciclo de vida de
 * la aplicación.
 * Es responsable de inicializar y mantener los recursos globales (Assets, Base
 * de Datos, Batch)
 * y delegar la lógica de renderizado a la pantalla activa (Screen).
 */
public class AbyssBattle extends Game {

    /** Etiqueta para filtrar logs en la consola. */
    private static final String TAG = AbyssBattle.class.getSimpleName();

    /**
     * Batch compartido para dibujar texturas de manera eficiente.
     * <p>
     * <b>Nota:</b> Es público por conveniencia en LibGDX, pero debería accederse
     * con cuidado.
     */
    public SpriteBatch batch;

    /**
     * Gestor central de recursos (imágenes, sonidos, skins) para carga asíncrona.
     */
    public AssetManager assets;

    /** Gestor de la conexión a la base de datos local. */
    private DatabaseManager dbManager;

    @Override
    public void create() {
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }

        Gdx.app.log(TAG, "Iniciando Abyss Battle...");
        try {
            dbManager = new DatabaseManager();
            dbManager.connect();
            Gdx.app.log(TAG, "Sistema de base de datos inicializado correctamente");
        } catch (SQLException e) {
            Gdx.app.error(TAG, "ERROR FATAL: No se pudo conectar a la base de datos", e);
            Gdx.app.exit();
            return;
        }

        assets = new AssetManager();

        batch = new SpriteBatch();

        setScreen(new LoadingScreen(this));
    }

    /**
     * Libera los recursos al cerrar la aplicación para evitar fugas de memoria.
     */
    @Override
    public void dispose() {
        Gdx.app.log(TAG, "Cerrando Abyss Battle...");
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

        if (dbManager != null) {
            dbManager.close();
            Gdx.app.log(TAG, "Base de datos cerrada correctamente");
        }

        Gdx.app.log(TAG, "Abyss Battle cerrado correctamente");
    }

    public DatabaseManager getDbManager() {
        return dbManager;
    }

}
