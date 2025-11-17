package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private final String name;
    private int gold;
    private int elixir;

    // Por ejemplo, tropas que posee en el mapa
    private List<Entity> units;

    public Player(String name, int gold, int elixir) {
        this.name = name;
        this.gold = gold;
        this.elixir = elixir;
        this.units = new ArrayList<>();
    }

    public String getName() { 
        return name; 
        }
    public int getGold() { 
        return gold; 
        }
    public int getElixir() { 
        return elixir; 
        }

    public void addGold(int amount) { 
        this.gold += amount; 
        }
    public void spendGold(int amount) { 
        this.gold -= amount; 
        }

    public void addElixir(int amount) { 
        this.elixir += amount; 
        }
    public void spendElixir(int amount) { 
        this.elixir -= amount; 
        }

    public void addUnit(Entity e) { 
        units.add(e); 
        }
    public void removeUnit(Entity e) { 
        units.remove(e); 
        }

    public List<Entity> getUnits() { return units; }
}
