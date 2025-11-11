package io.github.jarethjaziel.abyssbattle;

import com.badlogic.gdx.Game;

import io.github.jarethjaziel.abyssbattle.screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AbyssBattle extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}