package io.github.jarethjaziel.abyssbattle.gameutil.manager;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;

public class MapManager implements Disposable {

    private TiledMap map;
    

    public MapManager(World world, String string) {
        
    }


    public TiledMap getMap() {
        return map;
    }


    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'dispose'");
    }
}