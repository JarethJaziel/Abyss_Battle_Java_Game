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
    public static final int DEFAULT_TROOP_SKIN_ID = 0;
    public static final int DEFAULT_CANNON_SKIN_ID = 0;

    //Screens constants
    public static final float TITLE_SCALE_FACTOR = 0.0090f;
    public static final float TITLE_BOTTOM_PADDING = 0.08f;
    public static final float TITLE_LEFT_PADDING = 0.36f;

    public static final float BUTTON_FONT_SCALE_MENU = 0.0032f;
    public static final float BUTTON_WIDTH_PERCENT = 0.28f;
    public static final float BUTTON_HEIGHT_PERCENT = 0.12f;

    public static final float BUTTON_LEFT_PADDING = 0.42f;
    public static final float BUTTON_BOTTOM_PADDING = 0.04f;

    public static final float TITLE_POS_X = 0.35f;
    public static final float TITLE_POS_Y = 0.86f;

    public static final float BACK_WIDTH = 0.11f;
    public static final float BACK_HEIGHT = 0.085f;
    public static final float BACK_POS_X = 0.87f;
    public static final float BACK_POS_Y = 0.86f;

    public static final float CANNON_BTN_WIDTH = 0.13f;
    public static final float CANNON_BTN_HEIGHT = 0.075f;
    public static final float CANNON_BTN_POS_X = 0.84f;
    public static final float CANNON_BTN_POS_Y = 0.73f;

    public static final float SKIN_START_X = 0.05f;
    public static final float SKIN_OFFSET_X = 0.242f;

    public static final float LABEL_OFFSET_X = 0.03f;
    public static final float SKIN_LABEL_Y = 0.17f;
    public static final float PRICE_LABEL_Y = 0.12f;

    public static final float BUY_WIDTH = 0.12f;
    public static final float BUY_HEIGHT = 0.065f;
    public static final float BUY_OFFSET_X = 0.025f;
    public static final float BUY_POS_Y = 0.02f;

    public static final float TITLE_FONT_SCALE = 0.0042f;
    public static final float SKIN_FONT_SCALE = 0.0022f;
    public static final float PRICE_FONT_SCALE = 0.0020f;
    public static final float BUTTON_FONT_SCALE_SHOP = 0.0014f;
    public static final float BUY_BUTTON_FONT_SCALE = 0.002f;

    public static final float TITLE_SCALE = 0.0042f;
    public static final float SKIN_NAME_SCALE = 0.0022f;
    public static final float PRICE_SCALE = 0.0020f;

    public static final float BACK_BUTTON_FONT_SCALE = 0.0014f;
    public static final float BACK_BUTTON_WIDTH = 0.11f;
    public static final float BACK_BUTTON_HEIGHT = 0.085f;
    public static final float BACK_BUTTON_POS_X = 0.87f;
    public static final float BACK_BUTTON_POS_Y = 0.86f;

    public static final float TROP_BUTTON_FONT_SCALE = 0.0014f;
    public static final float TROP_BUTTON_WIDTH = 0.13f;
    public static final float TROP_BUTTON_HEIGHT = 0.075f;
    public static final float TROP_BUTTON_POS_X = 0.84f;
    public static final float TROP_BUTTON_POS_Y = 0.73f;

    public static final float BUY_BUTTON_WIDTH = 0.12f;
    public static final float BUY_BUTTON_HEIGHT = 0.065f;
    public static final float BUY_BUTTON_OFFSET_X = 0.025f;
    public static final float BUY_BUTTON_Y = 0.02f;

    public static final float CANNON_BUTTON_FONT_SCALE = 0.0014f;
    public static final float CANNON_BUTTON_WIDTH = 0.13f;
    public static final float CANNON_BUTTON_HEIGHT = 0.075f;
    public static final float CANNON_BUTTON_POS_X = 0.84f;
    public static final float CANNON_BUTTON_POS_Y = 0.86f;

    public static final float EQUIP_BUTTON_FONT_SCALE = 0.002f;
    public static final float EQUIP_BUTTON_WIDTH = 0.12f;
    public static final float EQUIP_BUTTON_HEIGHT = 0.065f;
    public static final float EQUIP_BUTTON_OFFSET_X = 0.025f;
    public static final float EQUIP_BUTTON_Y = 0.02f;
    public static final float SKIN_LABEL_OFFSET_X = 0.03f;
    public static final float TROP_BUTTON2_FONT_SCALE = 0.0014f;
    public static final float TROP_BUTTON2_WIDTH = 0.13f;
    public static final float TROP_BUTTON2_HEIGHT = 0.075f;
    public static final float TROP_BUTTON2_POS_X = 0.84f;
    public static final float TROP_BUTTON2_POS_Y = 0.86f; 
}
