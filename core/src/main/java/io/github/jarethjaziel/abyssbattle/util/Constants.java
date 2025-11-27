package io.github.jarethjaziel.abyssbattle.util;

public class Constants {

    public static final float WORLD_WIDTH = 736;
    public static final float WORLD_HEIGHT = 1104;
    public static final int TILE_SIZE = 32;

    //Physics constants
    public static final float PIXELS_PER_METER = 100f; // Pixels Per Meter
    public static final float BULLET_SIZE = 0.01f * PIXELS_PER_METER;
    public static final float TROOP_SIZE = 0.8f * PIXELS_PER_METER;
    public static final float CANNON_SIZE = 1.0f * PIXELS_PER_METER;

    //Game constants
    public static final int TROOP_INITIAL_HEALTH = 100;
    public static final float MIN_SHOOT_ANGLE = 30f;
    public static final float MAX_SHOOT_ANGLE = 180f - MIN_SHOOT_ANGLE;
    public static final int BULLET_DAMAGE = 100;

}
