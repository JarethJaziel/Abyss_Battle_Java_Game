package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

import io.github.jarethjaziel.abyssbattle.util.Constants;
import io.github.jarethjaziel.abyssbattle.util.GAME_STATE;

public class GameLogic {

    private List<Player> players;
    private Player currentPlayer;
    private GAME_STATE state;

    private List<Projectile> activeProjectiles;
    private World world; 
    private PhysicsFactory physicsFactory;
    
    public GameLogic(World world) {
        this.world = world;
        players = new ArrayList<>();
        activeProjectiles = new ArrayList<>();
        state = GAME_STATE.INITIATED;
        this.physicsFactory = new PhysicsFactory(world);
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void startGame() {
        if (players.size() != 2)
            return;

        currentPlayer = players.get(0);
        state = GAME_STATE.PLAYER_1_TURN;
    }

    public void onCollision(int a, int b) {

    }

    public void playerShoot(float power) {
        if (state != GAME_STATE.PLAYER_1_TURN && state != GAME_STATE.PLAYER_2_TURN) {
            return;
        }
        
        Cannon cannon = currentPlayer.getCannon();
        
        Projectile newBullet = cannon.shoot(physicsFactory, power, Constants.BULLET_DAMAGE);
        
        registerShoot(newBullet);
    }

    public void playerAim(float angle) {
        Cannon cannon = currentPlayer.getCannon();
        cannon.setAngle(angle);
    }

    public boolean checkWinner() {

        Player p1 = players.get(0);
        Player p2 = players.get(1);

        boolean p1AllDead = p1.getTroopList().stream().allMatch(t -> !t.isActive());
        boolean p2AllDead = p2.getTroopList().stream().allMatch(t -> !t.isActive());

        if (p1AllDead && p2AllDead) {
            state = GAME_STATE.DRAW;
            return true;
        }

        if (p1AllDead) {
            state = GAME_STATE.PLAYER_2_WIN;
            return true;
        }

        if (p2AllDead) {
            state = GAME_STATE.PLAYER_1_WIN;
            return true;
        }

        return false;
    }

    public void changeTurn() {
        if (currentPlayer == players.get(0)) {
            currentPlayer = players.get(1);
            state = GAME_STATE.PLAYER_2_TURN;
        } else {
            currentPlayer = players.get(0);
            state = GAME_STATE.PLAYER_1_TURN;
        }
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public GAME_STATE getState() {
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
        state = GAME_STATE.WAITING; 
    }

    public void update(float delta) {
        
        Iterator<Projectile> iter = activeProjectiles.iterator();
        
        while (iter.hasNext()) {
            Projectile p = iter.next();
            
            p.update(delta);

            if (!p.isFlying()) {
                System.out.println("¡Impacto en " + p.getGroundPosition());
                
                applyAreaDamage(p.getGroundPosition(), 2.5f, p.getDamage());
                
                world.destroyBody(p.getBody());
                
                iter.remove();

                if (!checkWinner()) {
                    changeTurn();
                }
            }
        }
    }

    private void applyAreaDamage(Vector2 explosionCenter, float radioMeters, int maxDamage) {
        float radioPixeles = radioMeters * Constants.PIXELS_PER_METER;

        for (Player p : players) {
            for (Troop t : p.getTroopList()) {
                
                if (t.isActive()) {
                    Vector2 posTropa = new Vector2(t.getX(), t.getY());
                    float distancia = posTropa.dst(explosionCenter);

                    if (distancia <= radioPixeles) {
                        float damageFactor = 1.0f - (distancia / radioPixeles);
                        // Asegurar que el daño sea al menos 1 si está en rango
                        if (damageFactor < 0) damageFactor = 0;
                        
                        int finalDamage = (int) (maxDamage * damageFactor);
                        t.receiveDamage(finalDamage);
                        
                        System.out.println("Tropa dañada: " + finalDamage);
                    }
                }
            }
        }
    }

}
