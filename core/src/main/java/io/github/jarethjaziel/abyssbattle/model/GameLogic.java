package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class GameLogic {

    private static final float GRAVITY = 9.88f;

    private World world;
    private Board board;

    private Player player1;
    private Player player2;
    private Player activePlayer;

    private DamageSystem damageSystem;
    private ProjectilePhysics physics;

    private boolean gameOver;

    private List<Projectile> activeProjectiles;

    public GameLogic(World world, Board board, Player p1, Player p2) {
        this.world = world;
        this.board = board;

        this.player1 = p1;
        this.player2 = p2;
        this.activePlayer = p1;

        this.damageSystem = new DamageSystem();
        this.physics = new ProjectilePhysics(GRAVITY);

        this.activeProjectiles = new ArrayList<>();
        this.gameOver = false;
    }

    public void switchTurn() {
        activePlayer = (activePlayer == player1 ? player2 : player1);
    }

    public void shoot(float angle, float power) {

        Cannon cannon = activePlayer.getCannon();
        Vector2 pos = cannon.getBody().getPosition();

        Projectile p = physics.createProjectile(
                world,
                pos.x, pos.y,
                angle, power
        );

        activeProjectiles.add(p);
    }

    public void update(float deltaTime) {

        if (gameOver) return;

        List<Projectile> toRemove = new ArrayList<>();

        for (Projectile p : activeProjectiles) {

            if (!p.isActive()) {
                toRemove.add(p);
                continue;
            }

            checkProjectileCollision(p);
        }

        activeProjectiles.removeAll(toRemove);
    }

    private void checkProjectileCollision(Projectile p) {

    Vector2 pos = p.getBody().getPosition();

    // 1. Fuera del tablero
    if (board.isOutOfBounds(pos.x, pos.y)) {
        p.destroy();
        return;
    }

    // 2. Colisión contra tropas
    Troop hitTroop = board.getTroopAt(pos.x, pos.y, 0.5f);
    if (hitTroop != null) {
        damageSystem.applyDirectHit(hitTroop, p.getDamage());
        p.destroy();
        return;
    }

    // 3. Daño en área si el proyectil ya está destruido
    if (!p.isActive()) {
        damageSystem.applyAreaDamage(board, pos.x, pos.y, 2f);
    }
}


    public boolean checkVictory() {
        if (!player1.isAlive() || !player2.isAlive()) {
            gameOver = true;
            return true;
        }
        return false;
    }
}
