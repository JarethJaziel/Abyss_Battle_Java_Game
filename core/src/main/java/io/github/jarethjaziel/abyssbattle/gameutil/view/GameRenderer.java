package io.github.jarethjaziel.abyssbattle.gameutil.view;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.jarethjaziel.abyssbattle.model.GameLogic;
import io.github.jarethjaziel.abyssbattle.util.Constants;

public class GameRenderer implements Disposable{

    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final ShapeRenderer shapeRenderer;
    private final Box2DDebugRenderer b2dr;
    private final GameLogic logic;
    
    // Texturas Cacheadas (Para rendimiento)
    private final Texture cannonBase, cannonBarrel, troopBlue, troopRed, projectileTex, shadowTex;
    private final Animation<TextureRegion> explosionAnim;
    
    private float currentCameraAngle = 0f;
    private float explosionTimer = 0f;

    public GameRenderer(SpriteBatch batch, AssetManager assets, TiledMap map, GameLogic logic) {
        this.batch = batch;
        this.logic = logic;
        
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
    }

    public Viewport getViewport() {
        return viewport;
    }

    public void updateCamera(float delta) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateCamera'");
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        b2dr.dispose();
    }

}
