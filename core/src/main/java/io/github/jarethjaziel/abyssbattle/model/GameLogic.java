package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

public class GameLogic {
    private World world;
    private Board board;
    private Player player1;
    private Player player2;
    private Player activePlayer;
    private DamageSystem damageSystem;
    private Projectile physics;
    private boolean gameOver;

    private final List<Projectile> activeProjectiles;

    public GameLogic(World world, Board board, Player player1, Player player2) {
        this.world = world;
        this.board = board;
        this.player1 = player1;
        this.player2 = player2;
        this.activePlayer = player1;
        this.physics = new ProjectilePhysics();
        this.damageSystem = new DamageSystem();
        this.activeProjectiles = new ArrayList<>();
        this.gameOver = false;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void switchTurn() {
        activePlayer = (activePlayer == player1 ? player2 : player1);
    }

    /**
     * Crear y disparar un proyectil.
     */
    public void shoot(Cannon cannon, float angle, float power) {
        Trajectory trajectory = physics.computeTrajectory(angle, power);

        Projectile projectile = ProjectileFactory.createProjectile(world, angle, power);
        activeProjectiles.add(projectile);
    }

    /**
     * Llamado cada frame desde el motor del juego.
     */
    public void update(float deltaTime) {
        if (gameOver) return;

        List<Projectile> toRemove = new ArrayList<>();

        for (Projectile p : activeProjectiles) {
            if (!p.isActive()) {
                toRemove.add(p);
                continue;
            }

            // Revisar si golpeó algo
            checkProjectileCollisions(p);
        }

        activeProjectiles.removeAll(toRemove);
    }

    /**
     * Revisa colisiones del proyectil contra tropas y terreno.
     */
    private void checkProjectileCollisions(Projectile projectile) {

        Vector2 pos = projectile.getBody().getPosition();

        Tile tile = board.getTileAtWorld(pos.x, pos.y);
        if (tile == null) {
            // Fuera del área → destruir
            projectile.destroy();
            return;
        }

        // Colisión con tropa
        if (tile.getTroop() != null && tile.getTroop().isActive()) {
            damageSystem.applyDirectHit(tile.getTroop());
            projectile.destroy();
            return;
        }

        // Área de daño
        if (!projectile.isActive()) {
            damageSystem.applyAreaDamage(board, pos.x, pos.y, 2);
        }
    }

    public boolean checkVictory() {
        if (player1.getAliveTroops() == 0 || player2.getAliveTroops() == 0) {
            gameOver = true;
            return true;
        }
        return false;
    }

}
