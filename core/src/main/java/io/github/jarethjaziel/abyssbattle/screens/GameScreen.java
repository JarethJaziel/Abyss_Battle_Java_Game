package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen{

    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera camera;
    private Viewport viewport;
    // Variable para guardar la capa lógica de colisión
    private TiledMapTileLayer collisionLayer;

    public static final float WORLD_WIDTH = 736;  // Ancho del mundo en píxeles
    public static final float WORLD_HEIGHT = 1104; // Alto del mundo en píxeles
    public static final int TILE_SIZE = 32;


    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        map = new TmxMapLoader().load("maps/game_bg_1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);  

        collisionLayer = (TiledMapTileLayer) map.getLayers().get("collision");
        
        if (collisionLayer == null) {
            Gdx.app.error("GameScreen", "¡No se pudo encontrar la capa de Tiled llamada 'collision'!");
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        
        mapRenderer.setView(camera); 

        mapRenderer.render(); 

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pause'");
    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'resume'");
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'hide'");
    }
 
}
