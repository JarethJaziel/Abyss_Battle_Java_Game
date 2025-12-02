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

    private List<Player> players;
    private Player currentPlayer;
    private GameState state;

    private List<Projectile> activeProjectiles;
    private World world;
    private PhysicsFactory physicsFactory;

    private float turnTimer = 0f;
    private Vector2 lastImpactPosition = new Vector2();

    private int troopsToPlace = 0;
    private boolean troopDestroyedInShot = false;
    private boolean lastChanceActive = false;
    private boolean lastChanceUsed = false;

    public GameLogic() {
        world = new World(new Vector2(0, 0), true);
        players = new ArrayList<>();
        activeProjectiles = new ArrayList<>();
        state = GameState.INITIATED;
        this.physicsFactory = new PhysicsFactory(world);
    }

    public GameLogic(World world) {
        this.world = world;
        players = new ArrayList<>();
        activeProjectiles = new ArrayList<>();
        state = GameState.INITIATED;
        this.physicsFactory = new PhysicsFactory(world);
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void startGame() {
        if (players.size() != 2)
            return;

        // Configurar dirección del cañón del segundo jugador.
        players.get(1)
                .getCannon()
                .setMinAngle(180 + Constants.MIN_SHOOT_ANGLE);

        players.get(1)
                .getCannon()
                .setMaxAngle(180 + Constants.MAX_SHOOT_ANGLE);

        players.get(1)
                .getCannon()
                .setAngle(270);

        currentPlayer = players.get(0);
        state = GameState.PLACEMENT_P1;
        troopsToPlace = Constants.MAX_PLAYER_TROOPS;
    }

    public void onCollision(int a, int b) {

    }

    public World getWorld() {
        return world;
    }

    public void playerShoot(float power) {
        if (state != GameState.PLAYER_1_TURN && state != GameState.PLAYER_2_TURN) {
            return;
        }

        Cannon cannon = currentPlayer.getCannon();

        Projectile newBullet = cannon.shoot(physicsFactory, power, Constants.BULLET_DAMAGE);

        registerShoot(newBullet);
    }

    public void playerAim(float angle) {
        if (state == GameState.WAITING || isGameOver()) {
            return;
        }
        Cannon cannon = currentPlayer.getCannon();
        cannon.setAngle(angle);
    }

    public void tryPlaceTroop(float x, float y) {
        if (state != GameState.PLACEMENT_P1 && state != GameState.PLACEMENT_P2) {
            return;
        }

        float screenMiddle = Constants.WORLD_HEIGHT / 2;

        if (state == GameState.PLACEMENT_P1 && y > screenMiddle) {
            Gdx.app.log("P1", "¡No puedes colocar en territorio enemigo!");
            return;
        }

        if (state == GameState.PLACEMENT_P2 && y < screenMiddle) {
            Gdx.app.log("P2", "¡No puedes colocar en territorio enemigo!");
            return;
        }

        Troop t = physicsFactory.createTroop(x, y);
        currentPlayer.addTroop(t);
        troopsToPlace--;

        System.out.println("Tropa colocada. Restan: " + troopsToPlace);

        if (troopsToPlace <= 0) {
            advancePlacementPhase();
        }
    }

    public int getTroopsToPlace() {
        return troopsToPlace;
    }

    public boolean checkWinner() {

        Player p1 = players.get(0);
        Player p2 = players.get(1);

        boolean p1AllDead = p1.getTroopList().stream().allMatch(t -> !t.isActive());
        boolean p2AllDead = p2.getTroopList().stream().allMatch(t -> !t.isActive());

        if (p1AllDead && p2AllDead) {
            state = GameState.DRAW;
            return true;
        }

        if (p1AllDead) {
            state = GameState.PLAYER_2_WIN;
            return true;
        }

        if (p2AllDead) {
            // Si ya se usó la última oportunidad: finalizar
            if (lastChanceUsed) {
                finalizeGame();
                return true;
            }
            // Si ya hay una última chance activa pendiente, no re-activarla
            if (!lastChanceActive) {
                System.out.println("¡P2 ha perdido todas sus tropas! Activando ÚLTIMA OPORTUNIDAD.");
                state = GameState.LAST_CHANCE;
                lastChanceActive = true;
            }
            // devolvemos false porque el juego no termina todavía — queda dar la última
            // chance
            return false;
        }

        return false;
    }

    public void changeTurn() {

        if (state == GameState.LAST_CHANCE) {
            // Forzamos el turno al Jugador 2 para su tiro final
            currentPlayer = players.get(1);
            System.out.println("Turno FINAL de JUGADOR 2");
            return;
        }

        if (currentPlayer == players.get(0)) {
            currentPlayer = players.get(1);
            state = GameState.PLAYER_2_TURN;
        } else {
            currentPlayer = players.get(0);
            state = GameState.PLAYER_1_TURN;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GameState getState() {
        return state;
    }

    public List<Projectile> getActiveProjectiles() {
        return activeProjectiles;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void registerShoot(Projectile p) {
        activeProjectiles.add(p);
        state = GameState.WAITING;
    }

    public void update(float delta) {

        if (state == GameState.TURN_TRANSITION) {
            turnTimer -= delta;

            if (turnTimer <= 0) {
                handleTurnChangeLogic();
            }
            return;
        }

        Iterator<Projectile> iter = activeProjectiles.iterator();

        while (iter.hasNext()) {
            Projectile p = iter.next();

            p.update(delta);

            if (!p.isFlying()) {
                System.out.println("¡Impacto en " + p.getGroundPosition());
                lastImpactPosition = p.getGroundPosition();

                troopDestroyedInShot = false;

                applyAreaDamage(lastImpactPosition, Constants.EXPLOSION_RATIO, p.getDamage());

                world.destroyBody(p.getBody());

                iter.remove();

                if (checkWinner()) {
                    return;
                }

                if (state == GameState.LAST_CHANCE && lastChanceActive) {
                    // Forzamos que P2 tome el turno final UNA VEZ
                    currentPlayer = players.get(1);
                    state = GameState.PLAYER_2_TURN;
                    lastChanceActive = false;
                    // marcamos que la oportunidad está en proceso (evitar reactivar)
                    lastChanceUsed = true;
                    return;
                }

                state = GameState.TURN_TRANSITION;
                turnTimer = Constants.TRANSITION_TIME_TO_WAIT;
            }

        }
    }

    private void finalizeGame() {
        Player p1 = players.get(0);
        Player p2 = players.get(1);

        boolean p1AllDead = p1.getTroopList().stream().allMatch(t -> !t.isActive());
        boolean p2AllDead = p2.getTroopList().stream().allMatch(t -> !t.isActive());

        if (p1AllDead && p2AllDead) {
            state = GameState.DRAW; // P2 logró matar a P1 con su último tiro
        } else if (p2AllDead) {
            state = GameState.PLAYER_1_WIN; // P2 falló su último tiro
        } else {
            // Caso raro: P2 revivió? Asumimos P1 gana si P2 sigue muerto
            state = GameState.PLAYER_1_WIN;
        }
    }

    private void handleTurnChangeLogic() {

        // CASO ESPECIAL: Si estábamos en "Última Oportunidad" y se acabó el tiempo
        if (state == GameState.LAST_CHANCE) {
            // Ya tiró P2, revisamos si logró empatar o perdió
            finalizeGame();
            return;
        }

        if (troopDestroyedInShot) {
            System.out.println("¡TROPA DESTRUIDA! TIRO DE BONIFICACIÓN.");

            // Solo conceder bono si el enemigo aún tiene tropas activas
            boolean enemyHasTroops = getEnemyPlayer().getTroopList().stream().anyMatch(t -> t.isActive());
            if (enemyHasTroops) {
                if (currentPlayer == players.get(0)) {
                    state = GameState.PLAYER_1_TURN;
                } else {
                    state = GameState.PLAYER_2_TURN;
                }
            } else {
                changeTurn(); // o pasar turno normalmente (según flujo que quieras)
            }
        } else {
            changeTurn();
        }
    }

    public Vector2 getLastImpactPosition() {
        return lastImpactPosition;
    }

    public boolean isGameOver() {
        return state == GameState.PLAYER_1_WIN ||
                state == GameState.PLAYER_2_WIN ||
                state == GameState.DRAW;
    }

    private void applyAreaDamage(Vector2 explosionCenter, float radioMeters, int maxDamage) {
        float radioPixeles = radioMeters * Constants.PIXELS_PER_METER;
        for (Troop t : getEnemyPlayer().getTroopList()) {
            if (t.isActive()) {

                Vector2 posTropa = new Vector2(
                        t.getPosX() * Constants.PIXELS_PER_METER,
                        t.getPosY() * Constants.PIXELS_PER_METER);
                float distancia = posTropa.dst(explosionCenter);
                if (distancia <= radioPixeles) {
                    float damageFactor = 1.0f - (distancia / radioPixeles);
                    // Asegurar que el daño sea al menos 1 si está en rango
                    if (damageFactor < 0)
                        damageFactor = 0;

                    int finalDamage = (int) (maxDamage * damageFactor);
                    t.receiveDamage(finalDamage);

                    System.out.println("Tropa dañada: " + finalDamage);
                    if (!t.isActive()) {
                        troopDestroyedInShot = true;
                        System.out.println("¡Enemigo abatido!");
                    }
                }
            }
        }
    }

    private void advancePlacementPhase() {
        if (state == GameState.PLACEMENT_P1) {
            // Terminó P1, sigue P2
            state = GameState.PLACEMENT_P2;
            currentPlayer = players.get(1);
            troopsToPlace = Constants.MAX_PLAYER_TROOPS; // Reiniciar contador para el P2
            System.out.println("Fase de Colocación: JUGADOR 2 (Haz click en la derecha)");
        } else if (state == GameState.PLACEMENT_P2) {
            // Terminó P2, ¡EMPIEZA EL JUEGO REAL!
            state = GameState.PLAYER_1_TURN;
            currentPlayer = players.get(0);
            System.out.println("¡Colocación terminada! INICIA EL COMBATE");
        }
    }

    private Player getEnemyPlayer() {
        return (currentPlayer == players.get(0)) ? players.get(1) : players.get(0);
    }


    public void initializePlayersAndCannons() {
        Player p1 = new Player(1);
        Player p2 = new Player(2);

        Cannon c1 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_1_CANNON_Y);
        Cannon c2 = physicsFactory.createCannon(Constants.CANNON_X, Constants.PLAYER_2_CANNON_Y);
        p1.setCannon(c1);
        p2.setCannon(c2);
        
        addPlayer(p1);
        addPlayer(p2);
    }

    @Override
    public void dispose() {
        world.dispose();
    }

}
