package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

public class World {

    private Board board;
    private final List<Entity> entities;
    private List<Projectile> projectiles;
    private DamageSystem damageSystem;
    private ProjectilePhysics physics;

    public World(int width, int height) {
        this.board = new Board(width, height);
        this.entities = new ArrayList<>();
        this.projectiles = new ArrayList<>();
        this.damageSystem = new DamageSystem();
        this.physics = new ProjectilePhysics();
    }

    public void addEntity(Entity e) {
        entities.add(e);
        board.placeEntity(e);
    }

    public void addProjectile(Projectile p) {
        projectiles.add(p);
    }

    public void update(double deltaTime) {
        // Actualizar físicas de proyectiles
        physics.updateProjectiles(projectiles, deltaTime);

        // Detectar colisiones y aplicar daño
        damageSystem.processDamage(entities, projectiles);

        // Actualizar entidades (por ejemplo, tropas moviéndose)
        for (Entity e : entities) {
            e.update(deltaTime);
        }
    }

    public Board getBoard() { return board; }
    public List<Entity> getEntities() { return entities; }
    public List<Projectile> getProjectiles() { return projectiles; }
}
