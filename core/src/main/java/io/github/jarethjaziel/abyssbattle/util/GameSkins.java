package io.github.jarethjaziel.abyssbattle.util;

public enum GameSkins {
    TROOP_BLUE("troop_blue", 0, SkinType.TROOP),
    TROOP_RED("troop_red", 0, SkinType.TROOP),
    
    TROOP_GREEN("troop_green", 500, SkinType.TROOP),
    TROOP_SILVER("troop_silver", 2500, SkinType.TROOP),
    TROOP_ULTRA("troop_ultra", 5000, SkinType.TROOP), 

    CANNON_DEFAULT("cannon_barrel_default", 0, SkinType.CANNON),

    CANNON_BLUE("cannon_barrel_blue", 500, SkinType.CANNON),
    CANNON_GREEN("cannon_barrel_green", 500, SkinType.CANNON),
    CANNON_BRONZE("cannon_barrel_bronze", 1500, SkinType.CANNON),
    CANNON_SILVER("cannon_barrel_silver", 3000, SkinType.CANNON);

    private final String name;
    private final int price;
    private final SkinType type;

    GameSkins(String name, int price, SkinType type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public SkinType getType() { return type; }
}