package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.physics.box2d.World;

public class Player {

    private int id;
    private String name;
    private List<Troop> troopList;

    public Player(int id, String name) {
        this.id = id;
        this.name = name;
        this.troopList = new ArrayList<>();
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<Troop> getTroopList() { return troopList; }

    public void placeTroop(Troop troop, float px, float py) {
        float x = px / 100f;  
        float y = py / 100f;
        troop.getBody().setTransform(x, y, 0); 
        troopList.add(troop);
    }

    public void shoot(Cannon cannon, World world, float angle, float power) {
        cannon.shoot(world, angle, power);
    }

    public void finishTurn() {
        
    }
}
