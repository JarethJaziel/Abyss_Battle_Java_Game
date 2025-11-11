package io.github.jarethjaziel.abyssbattle;

import com.badlogic.gdx.Game;
import com.kotcrab.vis.ui.VisUI;

import io.github.jarethjaziel.abyssbattle.screens.MainMenuScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class AbyssBattle extends Game {
    @Override
    public void create() {
        VisUI.load();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void dispose () {
        VisUI.dispose();
        
        super.dispose(); 
    }

}