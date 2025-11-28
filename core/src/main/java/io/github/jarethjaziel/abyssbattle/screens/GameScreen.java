package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.model.Cannon;
import io.github.jarethjaziel.abyssbattle.model.GameLogic;
import io.github.jarethjaziel.abyssbattle.model.PhysicsFactory;
import io.github.jarethjaziel.abyssbattle.model.Player;
import io.github.jarethjaziel.abyssbattle.model.Projectile;
import io.github.jarethjaziel.abyssbattle.model.Troop;
import io.github.jarethjaziel.abyssbattle.util.Constants;

public class GameScreen implements Screen{

    private AbyssBattle game;
    
    // --- Lógica y Física ---
    private World world;
    private GameLogic gameLogic;
    private PhysicsFactory physicsFactory;
    private Box2DDebugRenderer b2dr; // Para ver las cajas verdes (debug)

    // --- Renderizado ---
    private OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;
    
    // --- UI Scene2D ---
    private Stage stage;
    private Slider angleSlider;
    private Slider powerSlider;
    
    // --- Audio ---
    private Music bgMusic;
    private Sound sfxShoot;
    private Sound sfxBoom;
    


    public GameScreen(AbyssBattle game) {
        this.game = game;
    }

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT, camera);
        map = new TmxMapLoader().load("maps/game_bg_1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1);  

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        float mapWidth = layer.getWidth() * layer.getTileWidth();
        float mapHeight = layer.getHeight() * layer.getTileHeight();

        //viewport = new FitViewport(mapWidth, mapHeight, camera);
        
        camera.position.set(mapWidth / 2, mapHeight / 2, 0);
        camera.update();

        // 3. Iniciar Box2D y Lógica
        world = new World(new Vector2(0, 0), true); // Gravedad 0 (Top Down)
        b2dr = new Box2DDebugRenderer();
        gameLogic = new GameLogic(world);
        physicsFactory = new PhysicsFactory(world);

        // 4. Crear Colisiones del Mapa (Muros/Río)
        createMapCollissions();
        
        // 5. Inicializar Jugadores y Entidades
        startGame();

        // 6. Configurar UI
        setupUI();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage); // UI primero
        Gdx.input.setInputProcessor(multiplexer);

        sfxShoot = game.assets.get("sfx/shoot.mp3", Sound.class);
        sfxBoom = game.assets.get("sfx/boom.mp3", Sound.class);
        bgMusic = game.assets.get("music/game_music.mp3", Music.class);
        bgMusic.setLooping(true);
        bgMusic.setVolume(0.5f);
        bgMusic.play();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameLogic.update(delta);
        world.step(1/60f, 6, 2);

        camera.update();
        mapRenderer.setView(camera); 
        mapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        drawEntities();
        game.batch.end();

        // Render Debug Box2D (Opcional, quítalo después)
        b2dr.render(world, camera.combined.cpy().scl(Constants.PIXELS_PER_METER));

        // 6. Render UI
        stage.act(delta);
        stage.draw();
    }

    private void startGame() {
        Player p1 = new Player(1);
        Player p2 = new Player(2);

        Cannon c1 = physicsFactory.createCannon(Constants.WORLD_WIDTH/2, 3 * Constants.TILE_SIZE);
        Cannon c2 = physicsFactory.createCannon(Constants.WORLD_WIDTH/2, Constants.WORLD_HEIGHT - (3 * Constants.TILE_SIZE));
        c2.setAngle(270);
        p1.setCannon(c1);
        p2.setCannon(c2);

        Troop t1 = physicsFactory.createTroop(10* Constants.TILE_SIZE, 
                                            Constants.WORLD_HEIGHT/2 - Constants.TILE_SIZE*8);
        Troop t2 = physicsFactory.createTroop(10* Constants.TILE_SIZE,  
                                            Constants.WORLD_HEIGHT - Constants.TILE_SIZE*5);
        
        p1.addTroop(t1);
        p2.addTroop(t2);

        gameLogic.addPlayer(p1);
        gameLogic.addPlayer(p2);
        gameLogic.startGame();
    }

    private void createMapCollissions() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("collision");
        if (layer == null) return;
        
        float tileSize = layer.getTileWidth();
        
        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) {
                    
                    BodyDef bdef = new BodyDef();
                    bdef.type = BodyDef.BodyType.StaticBody;
                    // Posición central del tile
                    bdef.position.set((x * tileSize + tileSize/2) / Constants.PIXELS_PER_METER, 
                                      (y * tileSize + tileSize/2) / Constants.PIXELS_PER_METER);
                    
                    Body body = world.createBody(bdef);
                    PolygonShape shape = new PolygonShape();
                    shape.setAsBox((tileSize/2) / Constants.PIXELS_PER_METER, 
                                   (tileSize/2) / Constants.PIXELS_PER_METER);
                    body.createFixture(shape, 1.0f);
                    shape.dispose();
                }
            }
        }
    }

    private void setupUI() {
        stage = new Stage(new FitViewport(1280, 720));
        
        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        
        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        
        final Label angleLabel = new Label("Angulo: 90", labelStyle);
        final Label powerLabel = new Label("Potencia: 50", labelStyle);
        
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font = new BitmapFont();
        btnStyle.fontColor = Color.YELLOW;
        
        TextButton fireBtn = new TextButton("¡DISPARAR!", btnStyle);
        fireBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float power = 25f;
                gameLogic.playerShoot(power);
                sfxShoot.play();
            }
        });
        
        table.add(angleLabel).pad(10);
        table.add(fireBtn).pad(10);
        table.add(powerLabel).pad(10);
        
        stage.addActor(table);
    }

    private void drawEntities() {
        // --- A. JUGADORES (Cañones y Tropas) ---
        Texture cannonBase = game.assets.get("sprites/cannon_base.png");
        Texture cannonBarrel = game.assets.get("sprites/cannon_barrel.png");
        Texture troopBlue = game.assets.get("sprites/troop_blue.png");
        Texture troopRed = game.assets.get("sprites/troop_red.png");

        for (Player p : gameLogic.getPlayers()) {
            // Dibujar Cañón
            Cannon c = p.getCannon();
            // Base
            game.batch.draw(cannonBase,
                            c.getPosX() * Constants.PIXELS_PER_METER - Constants.CANNON_SIZE/2,
                            c.getPosY() * Constants.PIXELS_PER_METER - Constants.CANNON_SIZE/2,
                             Constants.CANNON_SIZE, Constants.CANNON_SIZE);
            // Barril Rotatorio
            Sprite s = new Sprite(cannonBarrel);
            s.setSize(Constants.CANNON_SIZE, Constants.CANNON_SIZE/3); // Ajusta tamaño
            s.setOrigin(0, s.getHeight()/2); // Pivote a la izquierda centro
            s.setPosition(c.getPosX() * Constants.PIXELS_PER_METER, 
                          c.getPosY() * Constants.PIXELS_PER_METER - s.getHeight()/2);
            s.setRotation(c.getAngle()); // Ángulo
            s.draw(game.batch);

            // Dibujar Tropas
            Texture troopTex = (p.getId() == 1) ? troopBlue : troopRed;
            for (Troop t : p.getTroopList()) {
                if(t.isActive()) {
                    game.batch.draw(troopTex,
                                    t.getPosX()* Constants.PIXELS_PER_METER  - Constants.TROOP_SIZE/2,
                                    t.getPosY()* Constants.PIXELS_PER_METER - Constants.TROOP_SIZE/2,
                                     Constants.TROOP_SIZE, Constants.TROOP_SIZE);
                }
            }
        }

        // --- B. PROYECTILES ---
        Texture bulletTex = game.assets.get("sprites/projectile.png");
        Texture shadowTex = game.assets.get("sprites/shadow.png");

        for (Projectile p : gameLogic.getActiveProjectiles()) {
            Vector2 groundPos = p.getGroundPosition();
            
            // 1. Sombra (Siempre en el suelo)
            game.batch.setColor(1, 1, 1, 0.5f); // Transparente
            game.batch.draw(shadowTex, 
                            groundPos.x - 10, 
                            groundPos.y - 5, 
                            20, 10);
            game.batch.setColor(Color.WHITE);

            // 2. Bala (Suelo + Altura)
            game.batch.draw(bulletTex, 
                          groundPos.x - Constants.BULLET_SIZE/2, 
                          groundPos.y + p.getHeight() - Constants.BULLET_SIZE/2, 
                          Constants.BULLET_SIZE, Constants.BULLET_SIZE);
        }
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
        world.dispose();
        b2dr.dispose();
        stage.dispose();
    }

    @Override
    public void hide() {
        dispose();
    }
 
}
