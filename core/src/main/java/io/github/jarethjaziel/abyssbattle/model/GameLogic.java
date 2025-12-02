package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GameState;

public class GameLogic implements Disposable {

    // ------------------------------------------------------------------------
    // FIELDS
    // ------------------------------------------------------------------------
    private List<Player> players = new ArrayList<>();
    private Player currentPlayer;

    private GameState state = GameState.INITIATED;
    private World world;
    private PhysicsFactory physicsFactory;

    private List<Projectile> activeProjectiles = new ArrayList<>();
    private Vector2 lastImpactPosition = new Vector2();

    private float turnTimer = 0f;

    private int troopsToPlace = 0;
    private boolean troopDestroyedInShot = false;
    private boolean lastChanceActive = false;
    private boolean lastChanceUsed = false;

    // ------------------------------------------------------------------------
    // CONSTRUCTORS
    // ------------------------------------------------------------------------
    public GameLogic() {
        this.world = new World(new Vector2(0, 0), true);
        this.physicsFactory = new PhysicsFactory(world);
    }

    public GameLogic(World world) {
        this.world = world;
        this.physicsFactory = new PhysicsFactory(world);
    }

    // ------------------------------------------------------------------------
    // PLAYER & GAME SETUP
    // ------------------------------------------------------------------------
    public void addPlayer(Player p) {
        players.add(p);
    }

    public void startGame() {
        if (players.size() > 0) players.clear();

        Player p1 = new Player(1);
        Player p2 = new Player(2);

        p1.setCannon(physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_1_CANNON_Y));
        p2.setCannon(physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_2_CANNON_Y));

        addPlayer(p1);
        addPlayer(p2);

        setupPlayer2Cannon();

        currentPlayer = p1;
        state = GameState.PLACEMENT_P1;
        troopsToPlace = Constants.MAX_PLAYER_TROOPS;
    }

    private void setupPlayer2Cannon() {
        Cannon c = players.get(1).getCannon();
        c.setMinAngle(180 + Constants.MIN_SHOOT_ANGLE);
        c.setMaxAngle(180 + Constants.MAX_SHOOT_ANGLE);
        c.setAngle(270);
    }

    // ------------------------------------------------------------------------
    // INPUT ACTIONS
    // ------------------------------------------------------------------------
    public void playerShoot(float power) {
        if (!isPlayerTurn()) return;

        Projectile shot = currentPlayer.getCannon().shoot(
                physicsFactory, power, Constants.BULLET_DAMAGE
        );
        registerShoot(shot);
    }

    private boolean isPlayerTurn() {
        return state == GameState.PLAYER_1_TURN || state == GameState.PLAYER_2_TURN;
    }

    public void playerAim(float angle) {
        if (state == GameState.WAITING || isGameOver()) return;
        currentPlayer.getCannon().setAngle(angle);
    }

    public void tryPlaceTroop(float x, float y) {
        if (!isPlacementPhase()) return;

        if (!isValidPlacementTerritory(y)) {
            logInvalidPlacement();
            return;
        }

        Troop t = physicsFactory.createTroop(x, y);
        currentPlayer.addTroop(t);
        troopsToPlace--;

        if (troopsToPlace <= 0) {
            advancePlacementPhase();
        }
    }

    private boolean isPlacementPhase() {
        return state == GameState.PLACEMENT_P1 || state == GameState.PLACEMENT_P2;
    }

    private boolean isValidPlacementTerritory(float y) {
        float mid = Constants.WORLD_HEIGHT / 2f;

        if (state == GameState.PLACEMENT_P1) return y <= mid;
        if (state == GameState.PLACEMENT_P2) return y >= mid;

        return false;
    }

    private void logInvalidPlacement() {
        String p = (state == GameState.PLACEMENT_P1) ? "P1" : "P2";
        Gdx.app.log(p, "Ubicación inválida en territorio enemigo");
    }

    // ------------------------------------------------------------------------
    // MAIN UPDATE LOOP
    // ------------------------------------------------------------------------
    public void update(float delta) {
        if (state == GameState.TURN_TRANSITION) {
            updateTurnTransition(delta);
            return;
        }

        updateProjectiles(delta);
    }

    private void updateTurnTransition(float delta) {
        turnTimer -= delta;
        if (turnTimer <= 0) {
            handleTurnChangeLogic();
        }
    }

    private void updateProjectiles(float delta) {
        Iterator<Projectile> iter = activeProjectiles.iterator();

        while (iter.hasNext()) {
            Projectile p = iter.next();
            p.update(delta);

            if (!p.isFlying()) {
                handleProjectileImpact(p);
                iter.remove();
                return;
            }
        }
    }

    private void handleProjectileImpact(Projectile p) {
        lastImpactPosition = p.getGroundPosition();
        troopDestroyedInShot = false;

        applyAreaDamage(lastImpactPosition, Constants.EXPLOSION_RATIO, p.getDamage());
        world.destroyBody(p.getBody());

        if (checkWinner()) return;

        handleLastChanceAfterShot();

        state = GameState.TURN_TRANSITION;
        turnTimer = Constants.TRANSITION_TIME_TO_WAIT;
    }

    private void handleLastChanceAfterShot() {
        if (state == GameState.LAST_CHANCE && lastChanceActive) {
            currentPlayer = players.get(1);
            state = GameState.PLAYER_2_TURN;
            lastChanceActive = false;
            lastChanceUsed = true;
        }
    }

    // ------------------------------------------------------------------------
    // TURN LOGIC
    // ------------------------------------------------------------------------
    public void changeTurn() {
        if (state == GameState.LAST_CHANCE) {
            currentPlayer = players.get(1);
            return;
        }

        if (currentPlayer == players.get(0)) {
            currentPlayer = players.get((1));
            state = GameState.PLAYER_2_TURN;
        } else {
            currentPlayer = players.get(0);
            state = GameState.PLAYER_1_TURN;
        }
    }

    private void handleTurnChangeLogic() {
        if (state == GameState.LAST_CHANCE) {
            finalizeGame();
            return;
        }

        if (troopDestroyedInShot && enemyHasActiveTroops()) {
            giveBonusShot();
        } else {
            changeTurn();
        }
    }

    private boolean enemyHasActiveTroops() {
        return getEnemyPlayer().getTroopList().stream().anyMatch(Troop::isActive);
    }

    private void giveBonusShot() {
        if (currentPlayer == players.get(0))
            state = GameState.PLAYER_1_TURN;
        else
            state = GameState.PLAYER_2_TURN;
    }

    // ------------------------------------------------------------------------
    // DAMAGE & WIN CHECK
    // ------------------------------------------------------------------------
    private void applyAreaDamage(Vector2 center, float radiusMeters, int maxDamage) {
        float radiusPixels = radiusMeters * Constants.PIXELS_PER_METER;

        for (Troop t : getEnemyPlayer().getTroopList()) {
            if (!t.isActive()) continue;

            float distance = new Vector2(
                    t.getPosX() * Constants.PIXELS_PER_METER,
                    t.getPosY() * Constants.PIXELS_PER_METER
            ).dst(center);

            if (distance <= radiusPixels) {
                float factor = 1f - (distance / radiusPixels);
                int damage = Math.max(1, (int)(maxDamage * factor));

                t.receiveDamage(damage);

                if (!t.isActive()) troopDestroyedInShot = true;
            }
        }
    }

    public boolean checkWinner() {
        Player p1 = players.get(0);
        Player p2 = players.get(1);

        boolean p1Dead = p1.getTroopList().stream().noneMatch(Troop::isActive);
        boolean p2Dead = p2.getTroopList().stream().noneMatch(Troop::isActive);

        if (p1Dead && p2Dead) {
            state = GameState.DRAW;
            return true;
        }

        if (p1Dead) {
            state = GameState.PLAYER_2_WIN;
            return true;
        }

        if (p2Dead) return handleP2EliminatedCase();

        return false;
    }

    private boolean handleP2EliminatedCase() {
        if (lastChanceUsed) {
            finalizeGame();
            return true;
        }

        if (!lastChanceActive) {
            state = GameState.LAST_CHANCE;
            lastChanceActive = true;
        }

        return false;
    }

    private void finalizeGame() {
        Player p1 = players.get(0);
        Player p2 = players.get(1);

        boolean p1Dead = p1.getTroopList().stream().noneMatch(Troop::isActive);
        boolean p2Dead = p2.getTroopList().stream().noneMatch(Troop::isActive);

        if (p1Dead && p2Dead)
            state = GameState.DRAW;
        else if (p2Dead)
            state = GameState.PLAYER_1_WIN;
        else
            state = GameState.PLAYER_1_WIN;
    }

    // ------------------------------------------------------------------------
    // PLACEMENT PHASE
    // ------------------------------------------------------------------------
    private void advancePlacementPhase() {
        if (state == GameState.PLACEMENT_P1) {
            currentPlayer = players.get(1);
            state = GameState.PLACEMENT_P2;
            troopsToPlace = Constants.MAX_PLAYER_TROOPS;
            return;
        }

        if (state == GameState.PLACEMENT_P2) {
            currentPlayer = players.get(0);
            state = GameState.PLAYER_1_TURN;
        }
    }

    // ------------------------------------------------------------------------
    // GETTERS
    // ------------------------------------------------------------------------
    public World getWorld() { return world; }
    public Player getCurrentPlayer() { return currentPlayer; }
    public GameState getState() { return state; }
    public List<Projectile> getActiveProjectiles() { return activeProjectiles; }
    public List<Player> getPlayers() { return players; }
    public Vector2 getLastImpactPosition() { return lastImpactPosition; }
    public int getTroopsToPlace() { return troopsToPlace; }

    public boolean isGameOver() {
        return state == GameState.PLAYER_1_WIN ||
               state == GameState.PLAYER_2_WIN ||
               state == GameState.DRAW;
    }

    private Player getEnemyPlayer() {
        return (currentPlayer == players.get(0)) ? players.get(1) : players.get(0);
    }

    public void registerShoot(Projectile p) {
        activeProjectiles.add(p);
        state = GameState.WAITING;
    }

    // ------------------------------------------------------------------------
    // CLEANUP
    // ------------------------------------------------------------------------
    @Override
    public void dispose() {
        world.dispose();
    }
}
