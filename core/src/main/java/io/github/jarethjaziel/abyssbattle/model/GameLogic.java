package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import io.github.jarethjaziel.abyssbattle.database.entities.Stats;
import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

/**
 * Controlador principal de la lógica del juego (Core/Brain).
 * <p>
 * Esta clase actúa como un orquestador (Facade Pattern) que conecta:
 * <ul>
 * <li>El mundo físico (Box2D).</li>
 * <li>La gestión de turnos ({@link TurnManager}).</li>
 * <li>Las reglas de combate ({@link CombatManager}).</li>
 * <li>Las entidades del juego (Jugadores, Proyectiles).</li>
 * </ul>
 */
public class GameLogic implements Disposable {

    private static final String TAG = GameLogic.class.getSimpleName();

    private final World world;
    private final PhysicsFactory physicsFactory;
    private final List<Player> players;
    private final List<Projectile> activeProjectiles;

    // Sub-Systems
    private final TurnManager turnManager;
    private final CombatManager combatManager;

    // State for Renderer
    private Vector2 lastImpactPosition = new Vector2();
    private Stats currentMatchStats;

    /**
     * Constructor principal que permite inyectar un mundo físico existente.
     * Ideal para pruebas o cuando el mundo se gestiona externamente.
     *
     * @param world El mundo de Box2D donde ocurrirá la simulación.
     */
    public GameLogic(World world) {
        this.world = world;
        this.physicsFactory = new PhysicsFactory(world);
        this.players = new ArrayList<>();
        this.activeProjectiles = new ArrayList<>();

        // Initialize Managers
        this.turnManager = new TurnManager(players);
        this.combatManager = new CombatManager();
        this.currentMatchStats = new Stats();
    }

     /**
     * Constructor por defecto. Crea un nuevo mundo físico con gravedad estándar.
     */
    public GameLogic() {
        this.world = new World(new Vector2(0, 0), true);
        this.physicsFactory = new PhysicsFactory(world);
        this.players = new ArrayList<>();
        this.activeProjectiles = new ArrayList<>();

        this.turnManager = new TurnManager(players);
        this.combatManager = new CombatManager();
        this.currentMatchStats = new Stats();
    }

    /**
     * Configura el inicio de la partida: crea jugadores, cañones y establece el estado inicial.
     */
    public void startGame() {
        // Setup Players & Cannons
        Player player1 = new Player(1);
        Player player2 = new Player(2);

        Cannon cannonP1 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_1_CANNON_Y);
        Cannon cannonP2 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_2_CANNON_Y);

        // P2 Config (Mirrored)
        cannonP2.setMinAngle(180 + Constants.MIN_SHOOT_ANGLE);
        cannonP2.setMaxAngle(180 + Constants.MAX_SHOOT_ANGLE);
        cannonP2.setAngle(270);

        player1.setCannon(cannonP1);
        player2.setCannon(cannonP2);

        addPlayer(player1);
        addPlayer(player2);

        turnManager.startPlacementPhase();
    }

    /**
     * Actualiza la lógica del juego en cada frame.
     *
     * @param delta Tiempo transcurrido desde el último frame (segundos).
     */
    public void update(float delta) {
        world.step(1 / 60f, 6, 2);
        turnManager.update(delta);

        Iterator<Projectile> iter = activeProjectiles.iterator();
        while (iter.hasNext()) {
            Projectile p = iter.next();
            p.update(delta);

            if (!p.isFlying()) {
                handleImpact(p);
                iter.remove();
            }
        }
    }

    /**
     * Procesa el impacto de un proyectil: daño, limpieza y condiciones de victoria.
     */
    private void handleImpact(Projectile p) {
        lastImpactPosition = p.getGroundPosition();
        Player enemy = turnManager.getEnemyPlayer();

        DamageReport dmgReport = combatManager.applyAreaDamage(
                lastImpactPosition,
                Constants.EXPLOSION_RATIO,
                p.getDamage(),
                enemy.getTroopList());

        if (turnManager.getCurrentPlayer().getId() == 1) {
            currentMatchStats.addDamage(dmgReport.getTotalDamageDealt());

            if (dmgReport.getTotalDamageDealt() > 0) {
                currentMatchStats.addHit();
            } else {
                currentMatchStats.addMiss();
            }
        }

        world.destroyBody(p.getBody());

        turnManager.handleTurnEnd(dmgReport.killOccurred());
        GameState winState = combatManager.checkWinCondition(players, turnManager.isLastChanceUsed());

        if (winState != null) {
            if (winState == GameState.LAST_CHANCE) {
                if (turnManager.getState() != GameState.LAST_CHANCE) {
                    turnManager.activateLastChance();
                }
            } else {
                // Actual Win/Draw
                turnManager.setState(winState);
                if (winState == GameState.PLAYER_1_WIN) {
                    currentMatchStats.addWin();
                } else {
                    currentMatchStats.addLoss();
                }
                Gdx.app.log(TAG, "Juego Terminado. Resultado: " + winState);
            }
        }

    }

    // --- Actions called by InputController ---
    /**
     * Intenta realizar un disparo con el cañón del jugador actual.
     *
     * @param power Potencia del disparo.
     */
    public void playerShoot(float power) {
        GameState state = turnManager.getState();
        if (state != GameState.PLAYER_1_TURN && state != GameState.PLAYER_2_TURN && state != GameState.LAST_CHANCE) {
            return;
        }

        Cannon cannon = turnManager.getCurrentPlayer().getCannon();
        Projectile newBullet = cannon.shoot(physicsFactory, power, Constants.BULLET_DAMAGE);

        activeProjectiles.add(newBullet);
        turnManager.setWaitingState();

    }

    /**
     * Ajusta el ángulo del cañón del jugador actual.
     * @param angle Nuevo ángulo en grados.
     */
    public void playerAim(float angle) {
        if (isGameOver() || turnManager.getState() == GameState.WAITING)
            return;
        turnManager.getCurrentPlayer().getCannon().setAngle(angle);
    }

    /**
     * Intenta colocar una tropa en el mapa durante la fase de preparación.
     */
    public void tryPlaceTroop(float x, float y) {
        float screenMiddle = Constants.WORLD_HEIGHT / 2;
        GameState s = turnManager.getState();

        if (s == GameState.PLACEMENT_P1 && y > screenMiddle)
            return;
        if (s == GameState.PLACEMENT_P2 && y < screenMiddle)
            return;

        Troop t = physicsFactory.createTroop(x, y);
        turnManager.getCurrentPlayer().addTroop(t);
        turnManager.decreaseTroopsToPlace();
        Gdx.app.log(TAG, "Tropa colocada. Restantes: " + turnManager.getTroopsToPlace());
    }

    // --- Getters & Helpers ---

    public void addPlayer(Player p) {
        players.add(p);
    }

    public World getWorld() {
        return world;
    }

    public GameState getState() {
        return turnManager.getState();
    }

    public Player getCurrentPlayer() {
        return turnManager.getCurrentPlayer();
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }

    public Vector2 getLastImpactPosition() {
        return lastImpactPosition;
    }

    public int getTroopsToPlace() {
        return turnManager.getTroopsToPlace();
    }

    public Stats getMatchStats() {
        return currentMatchStats;
    }

    public boolean isGameOver() {
        GameState s = turnManager.getState();
        return s == GameState.PLAYER_1_WIN || s == GameState.PLAYER_2_WIN || s == GameState.DRAW;
    }

    public String getWinner() {
        if (turnManager.getState() == GameState.PLAYER_1_WIN)
            return "PLAYER 1";
        if (turnManager.getState() == GameState.PLAYER_2_WIN)
            return "PLAYER 2";
        return "DRAW";
    }

    @Override
    public void dispose() {
        world.dispose();
    }
}