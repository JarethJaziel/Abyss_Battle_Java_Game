package io.github.jarethjaziel.abyssbattle.gameutil.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.jarethjaziel.abyssbattle.gameutil.manager.MapManager;
import io.github.jarethjaziel.abyssbattle.model.Cannon;
import io.github.jarethjaziel.abyssbattle.model.GameLogic;
import io.github.jarethjaziel.abyssbattle.model.Player;
import io.github.jarethjaziel.abyssbattle.model.Projectile;
import io.github.jarethjaziel.abyssbattle.model.Troop;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

public class GameRenderer implements Disposable {

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;
    private final Box2DDebugRenderer b2dr;
    private final GameLogic logic;
    private final MapManager mapManager;

    // Texturas Cacheadas (Para rendimiento)
    private final Texture cannonBase, cannonBarrel, troopBlue, troopRed, projectileTex, shadowTex, blankTexture;
    private final Animation<TextureRegion> explosionAnim;

    private float currentCameraAngle = 0f;
    private float explosionTimer = 0f;

    public GameRenderer(SpriteBatch batch, AssetManager assets, MapManager mapManager, GameLogic logic) {
        this.batch = batch;
        this.logic = logic;
        this.mapManager = mapManager;

        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);

        // Centrar cámara inicialmente
        camera.position.set(Constants.WORLD_WIDTH / 2f, Constants.WORLD_HEIGHT / 2f, 0);
        camera.update();

        this.shapeRenderer = new ShapeRenderer();
        this.b2dr = new Box2DDebugRenderer();

        // Cargar texturas una sola vez
        this.cannonBase = assets.get("sprites/cannon_base.png");
        this.cannonBarrel = assets.get("sprites/cannon_skin/cannon_barrel_default.png");
        this.troopBlue = assets.get("sprites/troop_skin/troop_blue.png");
        this.troopRed = assets.get("sprites/troop_skin/troop_red.png");
        this.projectileTex = assets.get("sprites/projectile.png");
        this.shadowTex = assets.get("sprites/shadow.png");

        // Crear animación
        Texture t1 = assets.get("vfx/explosion1.png");
        Texture t2 = assets.get("vfx/explosion2.png");
        Texture t3 = assets.get("vfx/explosion3.png");
        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(t1));
        frames.add(new TextureRegion(t2));
        frames.add(new TextureRegion(t3));
        explosionAnim = new Animation<>(0.25f, frames, Animation.PlayMode.NORMAL);

        Pixmap pixmap = new Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        this.blankTexture = new Texture(pixmap);
        pixmap.dispose();
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void updateCamera(float delta) {
        float targetAngle = (logic.getCurrentPlayer().getId() == 2) ? 180f : 0f;
        currentCameraAngle = MathUtils.lerp(currentCameraAngle, targetAngle, delta * 2f);
        camera.up.set(0, 1, 0);
        camera.direction.set(0, 0, -1);
        camera.rotate(currentCameraAngle);
        camera.update();
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        b2dr.dispose();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    private void drawEntities(float delta) {
        for (Player p : logic.getPlayers()) {
            Cannon c = p.getCannon();
            batch.draw(cannonBase,
                    c.getPosX() * Constants.PIXELS_PER_METER - Constants.CANNON_SIZE / 2,
                    c.getPosY() * Constants.PIXELS_PER_METER - Constants.CANNON_SIZE / 2,
                    Constants.CANNON_SIZE, Constants.CANNON_SIZE);

            Sprite s = new Sprite(cannonBarrel);
            s.setSize(Constants.CANNON_SIZE, Constants.CANNON_SIZE / 3);
            s.setOrigin(0, s.getHeight() / 2);
            s.setPosition(c.getPosX() * Constants.PIXELS_PER_METER,
                    c.getPosY() * Constants.PIXELS_PER_METER - s.getHeight() / 2);
            s.setRotation(c.getAngle());
            s.draw(batch);

            Texture troopTex = (p.getId() == 1) ? troopBlue : troopRed;
            for (Troop t : p.getTroopList()) {
                if (t.isActive()) {
                    float troopX = t.getPosX() * Constants.PIXELS_PER_METER - Constants.TROOP_SIZE / 2;
                    float troopY = t.getPosY() * Constants.PIXELS_PER_METER - Constants.TROOP_SIZE / 2;

                    batch.draw(troopTex,
                            troopX - Constants.TROOP_SIZE / 2,
                            troopY - Constants.TROOP_SIZE / 2,
                            Constants.TROOP_SIZE, Constants.TROOP_SIZE);

                    float barWidth = Constants.TROOP_SIZE;
                    float barHeight = 5;
                    float yOffset = Constants.TROOP_SIZE / 2 + 10;

                    float healthBarY = (logic.getState() == GameState.PLAYER_1_TURN) ? troopY + yOffset
                            : troopY - yOffset;

                    float healthPct = t.getHealth() / (float) Constants.TROOP_INITIAL_HEALTH;

                    batch.setColor(Color.RED);
                    batch.draw(blankTexture, troopX, healthBarY, barWidth, barHeight);

                    batch.setColor(Color.GREEN);
                    batch.draw(blankTexture, troopX, healthBarY, barWidth * healthPct, barHeight);

                    batch.setColor(Color.WHITE);
                }
            }
            if (logic.getState() == GameState.TURN_TRANSITION) {

                explosionTimer += Gdx.graphics.getDeltaTime();
                TextureRegion currentFrame = explosionAnim.getKeyFrame(explosionTimer, false);
                Vector2 pos = logic.getLastImpactPosition();

                float size = Constants.EXPLOSION_SIZE;
                batch.draw(currentFrame,
                        pos.x - size / 2,
                        pos.y - size / 2,
                        size, size);
            } else {
                explosionTimer = 0;
            }
        }

        for (Projectile p : logic.getActiveProjectiles()) {
            Vector2 groundPos = p.getGroundPosition();

            batch.setColor(1, 1, 1, 0.5f);
            batch.draw(shadowTex,
                    groundPos.x - 10,
                    groundPos.y - 5,
                    20, 10);
            batch.setColor(Color.WHITE);

            batch.draw(projectileTex,
                        groundPos.y + p.getHeight() - Constants.BULLET_SIZE / 2,
                        groundPos.x - Constants.BULLET_SIZE / 2,
                    //groundPos.x - Constants.BULLET_SIZE / 2,
                    //groundPos.y + p.getHeight() - Constants.BULLET_SIZE / 2,
                    Constants.BULLET_SIZE, Constants.BULLET_SIZE);
        }
    }

    public void drawAimLine(Vector2 start, Vector2 current) {
        // 1. Configurar ShapeRenderer
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // 2. Obtener datos de la lógica (La verdad absoluta del ángulo)
        Cannon cannon = logic.getCurrentPlayer().getCannon();
        float clampedAngle = cannon.getAngle();

        // 3. Calcular distancia usando los PARÁMETROS (no dragStart/dragCurrent)
        float visualLength = start.dst(current);

        // 4. Determinar color y clampear longitud visual
        if (visualLength > Constants.MAX_AIM_VISION) {
            shapeRenderer.setColor(Color.RED);
            visualLength = Constants.MAX_AIM_VISION;
        } else {
            shapeRenderer.setColor(Color.YELLOW);
        }

        // 5. Calcular punto final basado en el ángulo del cañón
        // Usamos MathUtils de LibGDX
        float endX = start.x + MathUtils.cosDeg(clampedAngle) * visualLength;
        float endY = start.y + MathUtils.sinDeg(clampedAngle) * visualLength;

        // 6. Dibujar línea
        // Grosor de 5 pixeles (ajusta según necesites, o usa una constante)
        shapeRenderer.rectLine(start.x, start.y, endX, endY, 5f);

        shapeRenderer.end();
    }

    public void render(float delta) {
        mapManager.render(camera);
        //b2dr.render(logic.getWorld(), camera.combined.cpy().scl(Constants.PIXELS_PER_METER));
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawEntities(delta);
        batch.end();
    }

}
