package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.physics.box2d.Body;
/**
 * Clase base abstracta para todas las entidades físicas del juego.
 * <p>
 * Envuelve un cuerpo de Box2D ({@link Body}) y proporciona métodos comunes
 * para acceder a su posición y estado en el mundo físico.
 */
public abstract class Entity {

    /** * El cuerpo físico de Box2D asociado a esta entidad. 
     * Se mantiene protected para que las subclases puedan acceder directamente si es necesario,
     * aunque se recomienda usar {@link #getBody()}.
     */
    Body body;

    /**
     * Constructor base.
     *
     * @param body El cuerpo físico (Box2D) que dará vida a esta entidad.
     */
    protected Entity(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public float getPosX(){
        return body.getPosition().x;
    }

    public float getPosY(){
        return body.getPosition().y;
    }
    /**
     * Define si la entidad está activa y debe ser procesada por la lógica del juego.
     * <p>
     * GameLogic revisará esto en cada frame. Si devuelve false, la entidad podría
     * ser ignorada o eliminada de las listas de actualización.
     *
     * @return true si la entidad está viva/activa.
     */
    public abstract boolean isActive();

}
