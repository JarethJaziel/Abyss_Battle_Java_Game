package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String name;
    private List<Troop> troops;
    private Cannon cannon;

    public Player(String name, Cannon cannon) {
        this.name = name;
        this.cannon = cannon;
        this.troops = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Cannon getCannon() {
        return cannon;
    }

    public void addTroop(Troop troop) {
        troops.add(troop);
    }

    public List<Troop> getTroops() {
        return troops;
    }

    public int getAliveTroops() {
        int alive = 0;
        for (Troop t : troops) {
            if (t.isActive()) alive++;
        }
        return alive;
    }

    public boolean isAlive() {
        return getAliveTroops() > 0;
    }
}
