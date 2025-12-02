package io.github.jarethjaziel.abyssbattle.util;

public class Constants {

    public static final float WORLD_WIDTH = 736;
    public static final float WORLD_HEIGHT = 1104;
    public static final int TILE_SIZE = 32;

    //Physics constants
    public static final float PIXELS_PER_METER = 32f; // Pixels Per Meter
    public static final float BULLET_SIZE = 0.5f * PIXELS_PER_METER;
    public static final float TROOP_SIZE = 1.2f * PIXELS_PER_METER;
    public static final float CANNON_SIZE = 1.8f * PIXELS_PER_METER;
    public static final float EXPLOSION_RATIO = 2.5f;
    public static final float EXPLOSION_SIZE = EXPLOSION_RATIO*PIXELS_PER_METER;

    //Game constants
    public static final int MAX_PLAYER_TROOPS = 3;
    public static final int TROOP_INITIAL_HEALTH = 100;
    public static final float MIN_SHOOT_ANGLE = 60f;
    public static final float MAX_SHOOT_ANGLE = 180f - MIN_SHOOT_ANGLE;
    public static final int BULLET_DAMAGE = 100;
    public static final float TRANSITION_TIME_TO_WAIT = 2.0f;
    public static final int CANNON_X = (int) (12*PIXELS_PER_METER);
    public static final int PLAYER_1_CANNON_Y = (int) (4*PIXELS_PER_METER);
    public static final int PLAYER_2_CANNON_Y = (int) (WORLD_HEIGHT - PLAYER_1_CANNON_Y);
    //Aim
    public static final float MAX_AIM_POWER = 38;
    public static final float MIN_AIM_POWER = 30;
    public static final float AIM_DEADZONE = 10;
    public static final float MAX_AIM_VISION = 150;
    public static final float MAX_DRAG_DISTANCE = 300f;
    public static final int DEFAULT_TROOP_SKIN_ID = 1;
    public static final int DEFAULT_CANNON_SKIN_ID = 2;

}
