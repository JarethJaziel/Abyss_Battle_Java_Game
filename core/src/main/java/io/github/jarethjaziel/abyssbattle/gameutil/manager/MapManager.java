package io.github.jarethjaziel.abyssbattle.gameutil.manager;

import com.badlogic.gdx.Gdx;
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

/**
 * Gestor del mapa de juego (Nivel).
 * <p>
 * Se encarga de cargar el archivo de mapa (.tmx), renderizarlo en pantalla
 * y generar los cuerpos físicos estáticos (colisiones) para Box2D basados en
 * las
 * celdas del mapa.
 */
public class MapManager implements Disposable {

    private static final String TAG = MapManager.class.getSimpleName();
    private static final String COLLISION_LAYER_NAME = "collision";

    private TiledMap map;
    private final OrthogonalTiledMapRenderer renderer;

    /**
     * Constructor del gestor de mapa.
     * <p>
     * Carga el mapa y genera las colisiones físicas inmediatamente.
     *
     * @param world   El mundo físico de Box2D donde se crearán los obstáculos.
     * @param mapPath Ruta del archivo .tmx en los assets.
     */
    public MapManager(World world, String mapPath) {
        map = new TmxMapLoader().load(mapPath);
        renderer = new OrthogonalTiledMapRenderer(map, 1);

        createMapCollisions(world);
    }

    /**
     * Renderiza las capas visuales del mapa.
     *
     * @param camera La cámara del juego para determinar qué parte del mapa dibujar.
     */
    public void render(OrthographicCamera camera) {
        renderer.setView(camera);
        renderer.render();
    }

    /**
     * Lee la capa de colisiones del mapa y crea cuerpos estáticos en Box2D
     * para cada celda ocupada.
     *
     * @param world El mundo físico donde añadir los cuerpos.
     */
    private void createMapCollisions(World world) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(COLLISION_LAYER_NAME);
        
        if (layer == null) {
            Gdx.app.error(TAG, "No se encontró la capa de colisión: " + COLLISION_LAYER_NAME);
            return;
        }
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

    /**
     * Verifica si una coordenada (en Píxeles/Mundo visual) es válida para colocar
     * una tropa.
     *
     * @param x Coordenada X en píxeles.
     * @param y Coordenada Y en píxeles.
     * @return {@code true} si la posición está libre de obstáculos y dentro del
     *         mapa.
     */
    public boolean isValidPlacement(float x, float y) {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(COLLISION_LAYER_NAME);
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