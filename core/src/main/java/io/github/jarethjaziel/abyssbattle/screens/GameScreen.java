package io.github.jarethjaziel.abyssbattle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import io.github.jarethjaziel.abyssbattle.AbyssBattle;
import io.github.jarethjaziel.abyssbattle.model.Cannon;
import io.github.jarethjaziel.abyssbattle.model.GameLogic;
import io.github.jarethjaziel.abyssbattle.model.PhysicsFactory;
import io.github.jarethjaziel.abyssbattle.model.Player;
import io.github.jarethjaziel.abyssbattle.model.Projectile;
import io.github.jarethjaziel.abyssbattle.model.Troop;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

public class GameScreen implements Screen {

    private AbyssBattle game;

    // --- Lógica y Física ---
    private World world;
    private GameLogic gameLogic;
    private PhysicsFactory physicsFactory;
    private Box2DDebugRenderer b2dr;

    // --- Renderizado ---
    private OrthographicCamera camera;
    private Viewport viewport;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    // --- UI Scene2D ---
    private Stage stage;
    private Label statusLabel;
    private Texture blankTexture;

    // --- Menús Overlay ---
    private Stage overlayStage; //
    private Table pauseMenu;
    private Table gameOverMenu;
    private boolean isPaused = false;
    private boolean isGameOver = false;

    // Variables para la mecánica de Drag-and-Shoot
    private boolean isDragging = false;
    private Vector2 dragStart = new Vector2();
    private Vector2 dragCurrent = new Vector2();

    // Para dibujar la línea de mira
    private ShapeRenderer shapeRenderer;
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

        camera.position.set(mapWidth / 2, mapHeight / 2, 0);
        camera.update();

        world = new World(new Vector2(0, 0), true);
        b2dr = new Box2DDebugRenderer();
        gameLogic = new GameLogic(world);
        physicsFactory = new PhysicsFactory(world);

        createMapCollissions();
        startGame();
        setupUI();

        // menu
        overlayStage = new Stage(new FitViewport(Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT));
        createPauseMenu();
        createGameOverMenu();

        shapeRenderer = new ShapeRenderer();

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(overlayStage);
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(new InputAdapter() {

            @Override
            public boolean keyDown(int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    togglePause();
                    return true;
                }
                return false;
            }

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (isPaused || isGameOver) return false;
                return handleTouchDown(screenX, screenY);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (isPaused || isGameOver) return false;
                return handleTouchDragged(screenX, screenY);
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (isPaused || isGameOver) return false;
                return handleTouchUp(screenX, screenY);
            }
        });

        Gdx.input.setInputProcessor(multiplexer);

        Texture t1 = game.assets.get("vfx/explosion1.png");
        Texture t2 = game.assets.get("vfx/explosion2.png");
        Texture t3 = game.assets.get("vfx/explosion3.png");

        Array<TextureRegion> frames = new Array<>();
        frames.add(new TextureRegion(t1));
        frames.add(new TextureRegion(t2));
        frames.add(new TextureRegion(t3));

        explosionAnim = new Animation<>(0.25f, frames, Animation.PlayMode.NORMAL);

        Pixmap pixmap = new Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        blankTexture = new Texture(pixmap);
        pixmap.dispose();

        sfxShoot = game.assets.get("sfx/shoot.mp3", Sound.class);
        sfxBoom = game.assets.get("sfx/boom.mp3", Sound.class);
        bgMusic = game.assets.get("music/game_music.mp3", Music.class);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        stage.getViewport().update(width, height, true);
        overlayStage.getViewport().update(width, height, true);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //  Solo actualizar juego si no está pausado
        if (!isPaused && !isGameOver) {
            gameLogic.update(delta);
            updateUI();
            updateCamera(delta);
            world.step(1 / 60f, 6, 2);
        }

        //  Detectar fin de juego
        checkGameOver();

        camera.update();
        mapRenderer.setView(camera);
        mapRenderer.render();

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        drawEntities();
        game.batch.end();

        if (isDragging && !isPaused && !isGameOver) {
            shapeRenderer.setProjectionMatrix(camera.combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

            shapeRenderer.setColor(Color.YELLOW);

            Cannon cannon = gameLogic.getCurrentPlayer().getCannon();
            float clampedAngle = cannon.getAngle();

            float visualLength = dragStart.dst(dragCurrent);

            if (visualLength > Constants.MAX_AIM_VISION) {
                shapeRenderer.setColor(Color.RED);
                visualLength = Constants.MAX_AIM_VISION;
            }

            float endX = dragStart.x + com.badlogic.gdx.math.MathUtils.cosDeg(clampedAngle) * visualLength;
            float endY = dragStart.y + com.badlogic.gdx.math.MathUtils.sinDeg(clampedAngle) * visualLength;

            shapeRenderer.rectLine(dragStart.x, dragStart.y, endX, endY, 5f);
            shapeRenderer.end();
        }

        b2dr.render(world, camera.combined.cpy().scl(Constants.PIXELS_PER_METER));

        stage.act(delta);
        stage.draw();

        //  Renderizar menús overlay
        if (isPaused || isGameOver) {
            overlayStage.act(delta);
            overlayStage.draw();
        }
    }

   // sistema de pausa

    private void createPauseMenu() {
        pauseMenu = new VisTable();
        pauseMenu.setFillParent(true);
        pauseMenu.setVisible(false);

        // Fondo semi-transparente
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0, 0, 0, 0.7f));
        bgPixmap.fill();
        pauseMenu.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap))));
        bgPixmap.dispose();

        // Título
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.YELLOW);
        Label title = new Label("PAUSA", titleStyle);
        pauseMenu.add(title).padBottom(40);
        pauseMenu.row();

        // Estilo de botones
        VisTextButton.VisTextButtonStyle buttonStyle = createButtonStyle();

        // Botón Continuar
        VisTextButton resumeButton = new VisTextButton("Continuar", buttonStyle);
        resumeButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                togglePause();
            }
        });
        pauseMenu.add(resumeButton).width(300).height(60).pad(10);
        pauseMenu.row();

        // Botón Menú Principal
        VisTextButton menuButton = new VisTextButton("Menu Principal", buttonStyle);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        pauseMenu.add(menuButton).width(300).height(60).pad(10);
        pauseMenu.row();

        // Botón Salir
        VisTextButton exitButton = new VisTextButton("Salir del Juego", buttonStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        pauseMenu.add(exitButton).width(300).height(60).pad(10);

        overlayStage.addActor(pauseMenu);
    }

    private void createGameOverMenu() {
        gameOverMenu = new VisTable();
        gameOverMenu.setFillParent(true);
        gameOverMenu.setVisible(false);

        // Fondo semi-transparente
        Pixmap bgPixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        bgPixmap.setColor(new Color(0, 0, 0, 0.8f));
        bgPixmap.fill();
        gameOverMenu.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(bgPixmap))));
        bgPixmap.dispose();

        // Título (se actualizará según el resultado)
        BitmapFont titleFont = new BitmapFont();
        titleFont.getData().setScale(3f);
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.GOLD);
        Label title = new Label("FIN DEL JUEGO", titleStyle);
        title.setName("gameOverTitle");
        gameOverMenu.add(title).padBottom(40);
        gameOverMenu.row();

        // Estilo de botones
        VisTextButton.VisTextButtonStyle buttonStyle = createButtonStyle();

        // Botón Jugar de Nuevo
        VisTextButton playAgainButton = new VisTextButton("Jugar de Nuevo", buttonStyle);
        playAgainButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });
        gameOverMenu.add(playAgainButton).width(300).height(60).pad(10);
        gameOverMenu.row();

        // Botón Menú Principal
        VisTextButton menuButton = new VisTextButton("Menu Principal", buttonStyle);
        menuButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MainMenuScreen(game));
            }
        });
        gameOverMenu.add(menuButton).width(300).height(60).pad(10);
        gameOverMenu.row();

        // Botón Salir
        VisTextButton exitButton = new VisTextButton("Salir del Juego", buttonStyle);
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
        gameOverMenu.add(exitButton).width(300).height(60).pad(10);

        overlayStage.addActor(gameOverMenu);
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseMenu.setVisible(isPaused);
    }

    private void checkGameOver() {
        GameState state = gameLogic.getState();

        if (!isGameOver && (state == GameState.PLAYER_1_WIN ||
            state == GameState.PLAYER_2_WIN ||
            state == GameState.DRAW)) {

            isGameOver = true;
            gameOverMenu.setVisible(true);

            // Actualizar título según resultado
            Label title = gameOverMenu.findActor("gameOverTitle");
            if (title != null) {
                if (state == GameState.PLAYER_1_WIN) {
                    title.setText("¡VICTORIA JUGADOR 1!");
                    title.setColor(Color.CYAN);
                } else if (state == GameState.PLAYER_2_WIN) {
                    title.setText("¡VICTORIA JUGADOR 2!");
                    title.setColor(Color.RED);
                } else if (state == GameState.DRAW) {
                    title.setText("¡EMPATE!");
                    title.setColor(Color.YELLOW);
                }
            }
        }
    }

    private VisTextButton.VisTextButtonStyle createButtonStyle() {
        VisTextButton.VisTextButtonStyle style = new VisTextButton.VisTextButtonStyle(
            VisUI.getSkin().get("default", VisTextButton.VisTextButtonStyle.class)
        );

        style.font = new BitmapFont();
        style.font.getData().setScale(1.5f);
        style.fontColor = Color.WHITE;
        style.downFontColor = Color.YELLOW;

        style.up = VisUI.getSkin().newDrawable("white", Color.valueOf("34495EFF"));
        style.over = VisUI.getSkin().newDrawable("white", Color.valueOf("1ABC9CFF"));
        style.down = VisUI.getSkin().newDrawable("white", Color.valueOf("2ECC71FF"));

        return style;
    }



    private void startGame() {
        Player p1 = new Player(1);
        Player p2 = new Player(2);

        Cannon c1 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_1_CANNON_Y);
        Cannon c2 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_2_CANNON_Y);
        p1.setCannon(c1);
        p2.setCannon(c2);

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

        statusLabel = new Label("Iniciando...", labelStyle);
        statusLabel.setFontScale(2);

        Table table = new Table();
        table.bottom().padBottom(20);
        table.setFillParent(true);

        table.add(statusLabel).colspan(3).padBottom(20).row();

        stage.addActor(table);
    }

    private void drawEntities() {
        Texture cannonBase = game.assets.get("sprites/cannon_base.png");
        Texture cannonBarrel = game.assets.get("sprites/cannon_skin/cannon_barrel_default.png");
        Texture troopBlue = game.assets.get("sprites/troop_skin/troop_blue.png");
        Texture troopRed = game.assets.get("sprites/troop_skin/troop_red.png");

        for (Player p : gameLogic.getPlayers()) {
            Cannon c = p.getCannon();
            game.batch.draw(cannonBase,
                c.getPosX() * Constants.PIXELS_PER_METER - Constants.CANNON_SIZE / 2,
                c.getPosY() * Constants.PIXELS_PER_METER - Constants.CANNON_SIZE / 2,
                Constants.CANNON_SIZE, Constants.CANNON_SIZE);

            Sprite s = new Sprite(cannonBarrel);
            s.setSize(Constants.CANNON_SIZE, Constants.CANNON_SIZE / 3);
            s.setOrigin(0, s.getHeight() / 2);
            s.setPosition(c.getPosX() * Constants.PIXELS_PER_METER,
                c.getPosY() * Constants.PIXELS_PER_METER - s.getHeight() / 2);
            s.setRotation(c.getAngle());
            s.draw(game.batch);

            Texture troopTex = (p.getId() == 1) ? troopBlue : troopRed;
            for (Troop t : p.getTroopList()) {
                if (t.isActive()) {
                    float troopX = t.getPosX() * Constants.PIXELS_PER_METER - Constants.TROOP_SIZE / 2;
                    float troopY = t.getPosY() * Constants.PIXELS_PER_METER - Constants.TROOP_SIZE / 2;

                    game.batch.draw(troopTex,
                        troopX - Constants.TROOP_SIZE / 2,
                        troopY - Constants.TROOP_SIZE / 2,
                        Constants.TROOP_SIZE, Constants.TROOP_SIZE);

                    float barWidth = Constants.TROOP_SIZE;
                    float barHeight = 5;
                    float yOffset = Constants.TROOP_SIZE / 2 + 10;

                    float healthBarY = (gameLogic.getState() == GameState.PLAYER_1_TURN) ? troopY + yOffset
                        : troopY - yOffset;

                    float healthPct = t.getHealth() / (float) Constants.TROOP_INITIAL_HEALTH;

                    game.batch.setColor(Color.RED);
                    game.batch.draw(blankTexture, troopX, healthBarY, barWidth, barHeight);

                    game.batch.setColor(Color.GREEN);
                    game.batch.draw(blankTexture, troopX, healthBarY, barWidth * healthPct, barHeight);

                    game.batch.setColor(Color.WHITE);
                }
            }
            if (gameLogic.getState() == GameState.TURN_TRANSITION) {

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

        Texture bulletTex = game.assets.get("sprites/projectile.png");
        Texture shadowTex = game.assets.get("sprites/shadow.png");

        for (Projectile p : gameLogic.getActiveProjectiles()) {
            Vector2 groundPos = p.getGroundPosition();

            game.batch.setColor(1, 1, 1, 0.5f);
            game.batch.draw(shadowTex,
                groundPos.x - 10,
                groundPos.y - 5,
                20, 10);
            game.batch.setColor(Color.WHITE);

            game.batch.draw(bulletTex,
                groundPos.x - Constants.BULLET_SIZE / 2,
                groundPos.y + p.getHeight() - Constants.BULLET_SIZE / 2,
                Constants.BULLET_SIZE, Constants.BULLET_SIZE);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        b2dr.dispose();
        stage.dispose();
        overlayStage.dispose();
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
        }
    }

    @Override
    public void hide() {
        dispose();
    }

    private void updateUI() {
        GameState state = gameLogic.getState();

        if (state == GameState.WAITING) {
            statusLabel.setText("Proyectil en el aire...");
            statusLabel.setColor(Color.YELLOW);
        } else if (state == GameState.PLAYER_1_TURN) {
            statusLabel.setText("TURNO JUGADOR 1 (Abajo)");
            statusLabel.setColor(Color.CYAN);
        } else if (state == GameState.PLAYER_2_TURN) {
            statusLabel.setText("TURNO JUGADOR 2 (Arriba)");
            statusLabel.setColor(Color.RED);
        } else if (state == GameState.PLAYER_1_WIN) {
            statusLabel.setText("¡GANÓ JUGADOR 1!");
            statusLabel.setColor(Color.GREEN);
        } else if (state == GameState.PLAYER_2_WIN) {
            statusLabel.setText("¡GANÓ JUGADOR 2!");
            statusLabel.setColor(Color.GREEN);
        } else if (state == GameState.DRAW) {
            statusLabel.setText("¡EMPATE!");
            statusLabel.setColor(Color.YELLOW);
        } else if (state == GameState.TURN_TRANSITION) {
            statusLabel.setText("¡IMPACTO!");
            statusLabel.setColor(Color.CORAL);
        } else if (state == GameState.PLACEMENT_P1) {
            statusLabel.setText("COLOCA TUS TROPAS, JUGADOR 1: " +
                "(" + gameLogic.getTroopsToPlace() + " restantes)");
            statusLabel.setColor(Color.DARK_GRAY);
        } else if (state == GameState.PLACEMENT_P2) {
            statusLabel.setText("COLOCA TUS TROPAS, JUGADOR 2: " +
                "(" + gameLogic.getTroopsToPlace() + " restantes)");
            statusLabel.setColor(Color.DARK_GRAY);
        } else if (state == GameState.LAST_CHANCE) {
            statusLabel.setText("¡ÚLTIMA OPORTUNIDAD JUGADOR 2!");
            statusLabel.setColor(Color.BLACK);
        }
    }

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

        GameState state = gameLogic.getState();

        if (state == GameState.PLACEMENT_P1 || state == GameState.PLACEMENT_P2) {

            if (isValidPlacement(worldCoords.x, worldCoords.y)) {
                gameLogic.tryPlaceTroop(worldCoords.x, worldCoords.y);
                return true;
            } else {
                System.out.println("¡No puedes colocar tropas en el agua u obstáculos!");
                return false;
            }
        }

        if (state != GameState.PLAYER_1_TURN && state != GameState.PLAYER_2_TURN)
            return false;

        Player currentPlayer = gameLogic.getCurrentPlayer();
        Cannon cannon = currentPlayer.getCannon();

        float cannonX = cannon.getPosX() * Constants.PIXELS_PER_METER;
        float cannonY = cannon.getPosY() * Constants.PIXELS_PER_METER;

        float dist = Vector2.dst(worldCoords.x, worldCoords.y, cannonX, cannonY);

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

        Vector2 worldCoordinates = viewport.unproject(new Vector2(screenX, screenY));
        dragCurrent.set(worldCoordinates.x, worldCoordinates.y);

        Vector2 forceVector = new Vector2(dragStart).sub(dragCurrent);

        float angle = forceVector.angleDeg();

        gameLogic.playerAim(angle);

        return true;
    }

    private boolean handleTouchUp(int screenX, int screenY) {
        if (!isDragging)
            return false;

        isDragging = false;

        float distance = dragStart.dst(dragCurrent);

        float power = (distance / Constants.MAX_DRAG_DISTANCE) * 100;

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
        float targetAngle = 0f;

        if (gameLogic.getCurrentPlayer().getId() == 2) {
            targetAngle = 180f;
        }

        currentCameraAngle = MathUtils.lerp(currentCameraAngle, targetAngle, delta * 2f);

        camera.up.set(0, 1, 0);
        camera.direction.set(0, 0, -1);

        camera.rotate(currentCameraAngle);

        camera.update();
    }
}
