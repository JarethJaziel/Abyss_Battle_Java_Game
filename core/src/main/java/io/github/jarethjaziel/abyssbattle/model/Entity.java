package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.physics.box2d.Body;

public abstract class Entity {

    Body body;

    public Entity(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    // El GameLogic revisará esto en cada frame
    public abstract boolean isActive();

}
