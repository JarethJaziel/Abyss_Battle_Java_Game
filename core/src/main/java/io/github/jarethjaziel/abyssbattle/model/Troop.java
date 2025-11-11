package io.github.jarethjaziel.abyssbattle.model;

public class Troop {

    private int health;
    private Posicion pos;

    public void receiveDamage(int damage){
        int newHealth = getHealth() - damage;
        setHealth(newHealth);
    }

    public boolean isActive(){
        return health > 0;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

}
