package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
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
import io.github.jarethjaziel.abyssbattle.util.GAME_STATE;

public class GameScreen implements Screen {

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
    private Label statusLabel; // Para decir "Turno Jugador 1"
    private Texture blankTexture;

    // Variables para la mecánica de Drag-and-Shoot
    private boolean isDragging = false;
    private Vector2 dragStart = new Vector2(); // Donde está el cañón
    private Vector2 dragCurrent = new Vector2(); // Donde está el mouse

    // Para dibujar la línea de mira
    private ShapeRenderer shapeRenderer;
    // Variable para la animación de la cámara
    private float currentCameraAngle = 0f;

    // --- Animation ---
    private Animation<TextureRegion> explosionAnim;
    private float explosionTimer = 0;

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
        shapeRenderer = new ShapeRenderer();

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(0);
        float mapWidth = layer.getWidth() * layer.getTileWidth();
        float mapHeight = layer.getHeight() * layer.getTileHeight();

        // viewport = new FitViewport(mapWidth, mapHeight, camera);

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

        shapeRenderer = new ShapeRenderer(); // Inicializar el dibujante de líneas

        // IMPORTANTE: Cambiamos el InputProcessor.
        // Ahora seremos nosotros (GameScreen) quienes escuchemos los clicks,
        // además de la UI (stage) por si hay botones de pausa, etc.
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage); // La UI tiene prioridad
        multiplexer.addProcessor(new InputAdapter() { // Nuestro detector de mouse

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                return handleTouchDown(screenX, screenY);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                return handleTouchDragged(screenX, screenY);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                return handleTouchUp(screenX, screenY);
            }
        });

        Gdx.input.setInputProcessor(multiplexer);

        Texture t1 = game.assets.get("vfx/explosion1.png"); // O la ruta que tengas
        Texture t2 = game.assets.get("vfx/explosion2.png");
        Texture t3 = game.assets.get("vfx/explosion3.png");

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(t1));
        frames.add(new TextureRegion(t2));
        frames.add(new TextureRegion(t3));

        // 0.15f es la velocidad (cambia de imagen cada 0.15 segundos)
        explosionAnim = new Animation<>(0.25f, frames, Animation.PlayMode.NORMAL);

        Pixmap pixmap = new Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        blankTexture = new Texture(pixmap);
        pixmap.dispose();

        sfxShoot = game.assets.get("sfx/shoot.mp3", Sound.class);
        sfxBoom = game.assets.get("sfx/boom.mp3", Sound.class);
        bgMusic = game.assets.get("music/game_music.mp3", Music.class);
        // bgMusic.setLooping(true);
        // bgMusic.setVolume(0.5f);
        // bgMusic.play();

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);

        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        gameLogic.update(delta);
        updateUI();
        updateCamera(delta);
        world.step(1 / 60f, 6, 2);

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();

        drawEntities();
        game.batch.end();

        if (isDragging) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            // Color: Rojo si está al máximo, Amarillo si es suave
            shapeRenderer.setColor(Color.YELLOW);

            Cannon cannon = gameLogic.getCurrentPlayer().getCannon();
            float clampedAngle = cannon.getAngle(); // Este ángulo YA respeta los límites (min/max)

            // CALCULAR LONGITUD DE LA LÍNEA (FUERZA)
            float visualLength = dragStart.dst(dragCurrent);

            // Opcional: Limitar visualmente el largo para que no sea infinita
            if (visualLength > Constants.MAX_AIM_VISION) {
                shapeRenderer.setColor(Color.RED);
                visualLength = Constants.MAX_AIM_VISION;
            }

            // CONVERTIR ÁNGULO + LARGO A COORDENADAS (X, Y)
            float endX = dragStart.x + com.badlogic.gdx.math.MathUtils.cosDeg(clampedAngle) * visualLength;
            float endY = dragStart.y + com.badlogic.gdx.math.MathUtils.sinDeg(clampedAngle) * visualLength;

            shapeRenderer.rectLine(dragStart.x, dragStart.y, endX, endY, 5f);
            shapeRenderer.end();
        }

        // Render Debug Box2D
        b2dr.render(world, camera.combined.cpy().scl(Constants.PIXELS_PER_METER));

        // 6. Render UI
        stage.act(delta);
        stage.draw();
    }

    private void startGame() {
        Player p1 = new Player(1);
        Player p2 = new Player(2);

        Cannon c1 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_1_CANNON_Y);
        Cannon c2 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_2_CANNON_Y);
        p1.setCannon(c1);
        p2.setCannon(c2);

        /*
         * Troop t1 = physicsFactory.createTroop(Constants.CANNON_X,
         * Constants.PLAYER_1_CANNON_Y - Constants.TILE_SIZE * 3);
         * Troop t2 = physicsFactory.createTroop(Constants.CANNON_X,
         * Constants.PLAYER_2_CANNON_Y + Constants.TILE_SIZE * 3);
         * 
         * p1.addTroop(t1);
         * p2.addTroop(t2);
         */

        gameLogic.addPlayer(p1);
        gameLogic.addPlayer(p2);
        gameLogic.startGame();
    }

    private void createMapCollissions() {
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("collision");
        if (layer == null)
            return;

        float tileSize = layer.getTileWidth();

        for (int x = 0; x < layer.getWidth(); x++) {
            for (int y = 0; y < layer.getHeight(); y++) {
                if (layer.getCell(x, y) != null) {

                    BodyDef bdef = new BodyDef();
                    bdef.type = BodyDef.BodyType.StaticBody;
                    // Posición central del tile
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

    private void setupUI() {
        stage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT));

        Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = getColoredDrawable(10, 10, Color.DARK_GRAY);
        sliderStyle.knob = getColoredDrawable(20, 40, Color.YELLOW);

        // --- 4. ETIQUETA DE ESTADO ---
        statusLabel = new Label("Iniciando...", labelStyle);
        statusLabel.setFontScale(2); // Hacerlo grande

        // --- TABLA DE ORGANIZACIÓN ---
        Table table = new Table();
        table.bottom().padBottom(20);
        table.setFillParent(true);

        // Fila 1: Estado
        table.add(statusLabel).colspan(3).padBottom(20).row();

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
                    c.getPosX() * Constants.PIXELS_PER_METER - Constants.CANNON_SIZE / 2,
                    c.getPosY() * Constants.PIXELS_PER_METER - Constants.CANNON_SIZE / 2,
                    Constants.CANNON_SIZE, Constants.CANNON_SIZE);
            // Barril Rotatorio
            Sprite s = new Sprite(cannonBarrel);
            s.setSize(Constants.CANNON_SIZE, Constants.CANNON_SIZE / 3); // Ajusta tamaño
            s.setOrigin(0, s.getHeight() / 2); // Pivote a la izquierda centro
            s.setPosition(c.getPosX() * Constants.PIXELS_PER_METER,
                    c.getPosY() * Constants.PIXELS_PER_METER - s.getHeight() / 2);
            s.setRotation(c.getAngle()); // Ángulo
            s.draw(game.batch);

            // Dibujar Tropas
            Texture troopTex = (p.getId() == 1) ? troopBlue : troopRed;
            for (Troop t : p.getTroopList()) {
                if (t.isActive()) {
                    float troopX = t.getPosX() * Constants.PIXELS_PER_METER - Constants.TROOP_SIZE / 2;
                    float troopY = t.getPosY() * Constants.PIXELS_PER_METER - Constants.TROOP_SIZE / 2;

                    game.batch.draw(troopTex,
                            troopX - Constants.TROOP_SIZE / 2,
                            troopY - Constants.TROOP_SIZE / 2,
                            Constants.TROOP_SIZE, Constants.TROOP_SIZE);

                    float barWidth = Constants.TROOP_SIZE; // Mismo ancho que la tropa
                    float barHeight = 5; // 5 pixeles de alto
                    float yOffset = Constants.TROOP_SIZE / 2 + 10; // 10 de márgen

                    float healthBarY = (gameLogic.getState() == GAME_STATE.PLAYER_1_TURN) ? troopY + yOffset
                            : troopY - yOffset;

                    float healthPct = t.getHealth() / (float) Constants.TROOP_INITIAL_HEALTH;

                    game.batch.setColor(Color.RED);
                    game.batch.draw(blankTexture, troopX, healthBarY, barWidth, barHeight);

                    game.batch.setColor(Color.GREEN);
                    game.batch.draw(blankTexture, troopX, healthBarY, barWidth * healthPct, barHeight);

                    game.batch.setColor(Color.WHITE);
                }
            }
            if (gameLogic.getState() == GAME_STATE.TURN_TRANSITION) {

                explosionTimer += Gdx.graphics.getDeltaTime();
                TextureRegion currentFrame = explosionAnim.getKeyFrame(explosionTimer, false);
                Vector2 pos = gameLogic.getLastImpactPosition();

                float size = Constants.EXPLOSION_SIZE;
                game.batch.draw(currentFrame,
                        pos.x - size / 2,
                        pos.y - size / 2,
                        size, size);
            } else {
                explosionTimer = 0;
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
                    groundPos.x - Constants.BULLET_SIZE / 2,
                    groundPos.y + p.getHeight() - Constants.BULLET_SIZE / 2,
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
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    private void updateUI() {
        // 1. Obtener estado actual
        GAME_STATE state = gameLogic.getState();

        // 2. Texto informativo
        if (state == GAME_STATE.WAITING) {
            statusLabel.setText("Proyectil en el aire...");
            statusLabel.setColor(Color.YELLOW);
        } else if (state == GAME_STATE.PLAYER_1_TURN) {
            statusLabel.setText("TURNO JUGADOR 1 (Abajo)");
            statusLabel.setColor(Color.CYAN);
        } else if (state == GAME_STATE.PLAYER_2_TURN) {
            statusLabel.setText("TURNO JUGADOR 2 (Arriba)");
            statusLabel.setColor(Color.RED);
        } else if (state == GAME_STATE.PLAYER_1_WIN) {
            statusLabel.setText("¡GANÓ JUGADOR 1!");
            statusLabel.setColor(Color.GREEN);
        } else if (state == GAME_STATE.PLAYER_2_WIN) {
            statusLabel.setText("¡GANÓ JUGADOR 2!");
            statusLabel.setColor(Color.GREEN);
        } else if (state == GAME_STATE.DRAW) {
            statusLabel.setText("¡EMPATE!");
            statusLabel.setColor(Color.YELLOW);
        } else if (state == GAME_STATE.TURN_TRANSITION) {
            statusLabel.setText("¡IMPACTO!");
            statusLabel.setColor(Color.CORAL);
        } else if (state == GAME_STATE.PLACEMENT_P1) {
            statusLabel.setText("COLOCA TUS TROPAS, JUGADOR 1: " +
                    "(" + gameLogic.getTroopsToPlace() + " restantes)");
            statusLabel.setColor(Color.DARK_GRAY);
        } else if (state == GAME_STATE.PLACEMENT_P2) {
            statusLabel.setText("COLOCA TUS TROPAS, JUGADOR 2: " +
                    "(" + gameLogic.getTroopsToPlace() + " restantes)");
            statusLabel.setColor(Color.DARK_GRAY);
        } else if (state == GAME_STATE.LAST_CHANCE) {
            statusLabel.setText("¡ÚLTIMA OPORTUNIDAD JUGADOR 2!");
            statusLabel.setColor(Color.BLACK);
        }

    }

    // Método mágico para crear texturas de colores sólidos para la UI
    private TextureRegionDrawable getColoredDrawable(int width, int height, Color color) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();
        return drawable;
    }

    private boolean handleTouchDown(int screenX, int screenY) {
        Vector2 worldCoords = viewport.unproject(new Vector2(screenX, screenY));

        GAME_STATE state = gameLogic.getState();

        if (state == GAME_STATE.PLACEMENT_P1 || state == GAME_STATE.PLACEMENT_P2) {

            if (isValidPlacement(worldCoords.x, worldCoords.y)) {
                gameLogic.tryPlaceTroop(worldCoords.x, worldCoords.y);
                return true;
            } else {
                System.out.println("¡No puedes colocar tropas en el agua u obstáculos!");
                return false;
            }
        }

        if (state != GAME_STATE.PLAYER_1_TURN && state != GAME_STATE.PLAYER_2_TURN)
            return false;

        Player currentPlayer = gameLogic.getCurrentPlayer();
        Cannon cannon = currentPlayer.getCannon();

        float cannonX = cannon.getPosX() * Constants.PIXELS_PER_METER;
        float cannonY = cannon.getPosY() * Constants.PIXELS_PER_METER;

        float dist = Vector2.dst(worldCoords.x, worldCoords.y, cannonX, cannonY);

        // Si hizo click cerca del cañón (ej. 50 pixeles de radio), iniciamos arrastre
        if (dist < 50) {
            isDragging = true;
            dragStart.set(cannonX, cannonY);
            dragCurrent.set(worldCoords.x, worldCoords.y);
            return true;
        }
        return false;
    }

    private boolean handleTouchDragged(int screenX, int screenY) {
        if (!isDragging)
            return false;

        // Actualizar posición actual del mouse
        Vector2 worldCoordinates = viewport.unproject(new Vector2(screenX, screenY));
        dragCurrent.set(worldCoordinates.x, worldCoordinates.y);

        // --- CÁLCULO DE ÁNGULO ---
        // En mecánica "Resortera", jalamos al lado contrario.
        // Vector desde el Mouse -> Cañón
        Vector2 forceVector = new Vector2(dragStart).sub(dragCurrent);

        // angle() devuelve grados (0-360)
        float angle = forceVector.angle();

        // Actualizamos visualmente el cañón en tiempo real
        gameLogic.playerAim(angle);

        return true;
    }

    private boolean handleTouchUp(int screenX, int screenY) {
        if (!isDragging)
            return false;

        isDragging = false;

        // --- CÁLCULO DE POTENCIA ---
        // Distancia entre el cañón y donde soltaste el mouse
        float distance = dragStart.dst(dragCurrent);

        // Convertir distancia en pixeles a Potencia (0-100)
        // Digamos que arrastrar 200 pixeles es el 100% de potencia
        float maxDragDistance = 300f;
        float power = (distance / maxDragDistance) * 100;

        if (power < Constants.AIM_DEADZONE) {
            return true;
        }

        if (power < Constants.MIN_AIM_POWER) {
            power = Constants.MIN_AIM_POWER;
        }

        if (power > Constants.MAX_AIM_POWER) {
            power = Constants.MAX_AIM_POWER;
        }

        gameLogic.playerShoot(power);
        sfxShoot.play();

        return true;
    }

    private boolean isValidPlacement(float x, float y) {
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

    private void updateCamera(float delta) {
        // 1. Determinar el ángulo objetivo según de quién sea el turno
        float targetAngle = 0f; // Por defecto Jugador 1 (0 grados)

        if (gameLogic.getCurrentPlayer().getId() == 2) {
            targetAngle = 180f; // Jugador 2 (180 grados, mundo invertido)
        }

        // 2. Mover el ángulo actual hacia el objetivo suavemente
        // El '5f' es la velocidad de giro. Entre más alto, más rápido.
        // MathUtils.lerp acerca el primer valor al segundo valor poco a poco.
        currentCameraAngle = com.badlogic.gdx.math.MathUtils.lerp(currentCameraAngle, targetAngle, delta * 2f);

        // 3. Aplicar rotación a la cámara
        // TRUCO: Primero reseteamos la cámara para que mire "normal" (Norte)
        camera.up.set(0, 1, 0);
        camera.direction.set(0, 0, -1);

        // Luego aplicamos la rotación calculada
        camera.rotate(currentCameraAngle);

        // 4. ¡IMPORTANTE! Actualizar la cámara
        camera.update();
    }
}
