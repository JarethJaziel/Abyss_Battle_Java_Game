package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private int id;
    private Cannon cannon;
    private List<Troop> troopList;

    public Player(int id) {
        this.id = id;
        this.troopList = new ArrayList<>();
    }

    public int getId() { return id; }
    public List<Troop> getTroopList() { return troopList; }

    public void setCannon(Cannon cannon) {
        this.cannon = cannon;
    }

    public Cannon getCannon() {
        return cannon;
    }

    public void placeTroop(Troop troop, float px, float py) {
        float x = px / 100f;  
        float y = py / 100f;
        troop.getBody().setTransform(x, y, 0); 
        troopList.add(troop);
    }

    public void shoot(PhysicsFactory factory, float power, int damage) {
        cannon.shoot(factory, power, damage);
    }

    public void finishTurn() {
        
    }
}
