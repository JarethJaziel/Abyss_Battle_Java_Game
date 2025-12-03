package io.github.jarethjaziel.abyssbattle.gameutil.manager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import io.github.jarethjaziel.abyssbattle.util.Constants;

public class MapManager implements Disposable {

    private TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    public MapManager(World world, String mapPath) {
        // 1. Cargar Mapa
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1);

        // 2. Crear Colisiones
        createMapCollisions(world);
    }

    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    private void createMapCollisions(World world) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("collision");

        float tileSize = layer.getTileWidth();

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) {

                    BodyDef bdef = new BodyDef();
                    bdef.type = BodyDef.BodyType.StaticBody;
                    bdef.position.set((x * tileSize + tileSize / 2) / Constants.PIXELS_PER_METER,
                            (y * tileSize + tileSize / 2) / Constants.PIXELS_PER_METER);

                    Body body = world.createBody(bdef);
                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox((tileSize / 2) / Constants.PIXELS_PER_METER,
                            (tileSize / 2) / Constants.PIXELS_PER_METER);
                    body.createFixture(shape, 1.0f);
                    shape.dispose();
                }
            }
        }
    }

    public boolean isValidPlacement(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("collision");
        if (layer == null)
            return true;

        int cellX = (int) (x / layer.getTileWidth());
        int cellY = (int) (y / layer.getTileHeight());

        if (cellX < 0 || cellX >= layer.getWidth() || cellY < 0 || cellY >= layer.getHeight()) {
            return false;
        }

        boolean thereIsObstacle = (layer.getCell(cellX, cellY) != null);

        return !thereIsObstacle;
    }

    public TiledMap getMap() {
        return map;
    }

    @Override
    public void dispose() {
        map.dispose();
        renderer.dispose();
    }
}