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

     public float getX() {
        return body.getPosition().x;
    }

    public float getY() {
        return body.getPosition().y;
    }

     public void setX(float x) {
        body.getPosition().x = x;
    }

    public void setY(float y) {
        body.getPosition().y = y;
    }

    // El GameLogic revisará esto en cada frame
    public abstract boolean isActive();

}
