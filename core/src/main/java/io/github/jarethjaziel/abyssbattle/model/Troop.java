package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.physics.box2d.Body;

public class Troop extends Entity{

    public Troop(Body body, int initialHealth) { 
        super(body);
        this.health = initialHealth;
    }

    private int health;

    public void receiveDamage(int damage){
        int newHealth = this.health - damage;
        this.health = Math.max(0, newHealth);
    }

    @Override
    public boolean isActive(){
        return health > 0;
    }

    public int getHealth() {
        return health;
    }

}
