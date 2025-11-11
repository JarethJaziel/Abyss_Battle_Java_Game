package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.physics.box2d.Body;

public class Projectile extends Entity {

    private final int damage; 
    private boolean active;

    public Projectile(Body body, int damage) {
        super(body); 
        
        this.damage = damage;
        this.active = true;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void destroy() {
        this.active = false;
    }
}