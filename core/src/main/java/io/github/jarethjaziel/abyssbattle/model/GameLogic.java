package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

public class GameLogic implements Disposable {

    private final World world;
    private final PhysicsFactory physicsFactory;
    private final List<Player> players;
    private final List<Projectile> activeProjectiles;
    
    // Sub-Systems
    private final TurnManager turnManager;
    private final CombatManager combatManager;
    
    // State for Renderer
    private Vector2 lastImpactPosition = new Vector2();
    //private final MatchStats currentMatchStats; 

    public GameLogic(World world) {
        this.world = world;
        this.physicsFactory = new PhysicsFactory(world);
        this.players = new ArrayList<>();
        this.activeProjectiles = new ArrayList<>();
        
        // Initialize Managers
        this.turnManager = new TurnManager(players);
        this.combatManager = new CombatManager();
        //this.currentMatchStats = new MatchStats();
    }

    public GameLogic() {
        this.world = new World(new Vector2(0, 0), true);
        this.physicsFactory = new PhysicsFactory(world);
        this.players = new ArrayList<>();
        this.activeProjectiles = new ArrayList<>();
        
        // Initialize Managers
        this.turnManager = new TurnManager(players);
        this.combatManager = new CombatManager();
        //this.currentMatchStats = new MatchStats();
    }

    public void startGame() {
        // Setup Players & Cannons
        Player p1 = new Player(1);
        Player p2 = new Player(2);
        
        Cannon c1 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_1_CANNON_Y);
        Cannon c2 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_2_CANNON_Y);
        
        // P2 Config (Mirrored)
        c2.setMinAngle(180 + Constants.MIN_SHOOT_ANGLE);
        c2.setMaxAngle(180 + Constants.MAX_SHOOT_ANGLE);
        c2.setAngle(270);

        p1.setCannon(c1);
        p2.setCannon(c2);
        
        addPlayer(p1);
        addPlayer(p2);

        turnManager.startPlacementPhase();
    }

    public void update(float delta) {
        world.step(1/60f, 6, 2);
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

    private void handleImpact(Projectile p) {
        lastImpactPosition = p.getGroundPosition();
        Player enemy = turnManager.getEnemyPlayer();

        // 1. Apply Damage
        boolean troopKilled = combatManager.applyAreaDamage(
                lastImpactPosition, 
                Constants.EXPLOSION_RATIO, 
                p.getDamage(), 
                enemy.getTroopList()
        );
        
        // Track Stats (Damage) - Simplified
        if (turnManager.getCurrentPlayer().getId() == 1) {
             // You can calculate specific damage here if needed for precise stats
            //currentMatchStats.addDamage(p.getDamage()); // Estimation
        }

        // 2. Cleanup Physics
        world.destroyBody(p.getBody());

        // 3. Check Win Conditions
        GameState winState = combatManager.checkWinCondition(players, turnManager.isLastChanceUsed());

        if (winState != null) {
            // Logic for triggering Last Chance specific transition
            if (winState == GameState.LAST_CHANCE) {
                if (turnManager.getState() != GameState.LAST_CHANCE) {
                    turnManager.activateLastChance();
                }
            } else {
                // Actual Win/Draw
                turnManager.setState(winState);
            }
            return;
        }

        // 4. Continue Game (Switch Turn)
        turnManager.handleTurnEnd(troopKilled);
    }

    // --- Actions called by InputController ---

    public void playerShoot(float power) {
        // Validate State
        GameState s = turnManager.getState();
        if (s != GameState.PLAYER_1_TURN && s != GameState.PLAYER_2_TURN && s != GameState.LAST_CHANCE) {
            return;
        }

        Cannon cannon = turnManager.getCurrentPlayer().getCannon();
        Projectile newBullet = cannon.shoot(physicsFactory, power, Constants.BULLET_DAMAGE);
        
        activeProjectiles.add(newBullet);
        turnManager.setWaitingState();
        
        // Track Stats (Shots)
        if (turnManager.getCurrentPlayer().getId() == 1) {
            //currentMatchStats.addShot();
        }
    }

    public void playerAim(float angle) {
        if (isGameOver() || turnManager.getState() == GameState.WAITING) return;
        turnManager.getCurrentPlayer().getCannon().setAngle(angle);
    }

    public void tryPlaceTroop(float x, float y) {
        // Delegate placement validation logic to GameScreen/MapManager mostly, 
        // here we just create the body.
        
        // Validating territory
        float screenMiddle = Constants.WORLD_HEIGHT / 2;
        GameState s = turnManager.getState();
        
        if (s == GameState.PLACEMENT_P1 && y > screenMiddle) return;
        if (s == GameState.PLACEMENT_P2 && y < screenMiddle) return;

        Troop t = physicsFactory.createTroop(x, y);
        turnManager.getCurrentPlayer().addTroop(t);
        turnManager.decreaseTroopsToPlace();
    }

    // --- Getters & Helpers ---
    
    public void addPlayer(Player p) { players.add(p); }
    public World getWorld() { return world; }
    public GameState getState() { return turnManager.getState(); }
    public Player getCurrentPlayer() { return turnManager.getCurrentPlayer(); }
    public List<Player> getPlayers() { return players; }
    public List<Projectile> getActiveProjectiles() { return activeProjectiles; }
    public Vector2 getLastImpactPosition() { return lastImpactPosition; }
    public int getTroopsToPlace() { return turnManager.getTroopsToPlace(); }
    //public MatchStats getMatchStats() { return currentMatchStats; }
    
    public boolean isGameOver() {
        GameState s = turnManager.getState();
        return s == GameState.PLAYER_1_WIN || s == GameState.PLAYER_2_WIN || s == GameState.DRAW;
    }
    
    public String getWinner() {
        if (turnManager.getState() == GameState.PLAYER_1_WIN) return "PLAYER 1";
        if (turnManager.getState() == GameState.PLAYER_2_WIN) return "PLAYER 2";
        return "DRAW";
    }

    @Override
    public void dispose() {
        world.dispose();
    }
}