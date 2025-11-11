package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public class Cannon extends Entity {

    public Cannon(Body body) {
        super(body);
        // El 'body' que pasaste aquí debe ser 'StaticBody'.
    }

    /**
     * Como el cañón no puede ser destruido,
     * siempre está activo.
     */
    @Override
    public boolean isActive() {
        return true; 
    }

    /**
     * Este es el método 'dispararProyectil' de tu UML.
     * Necesita una referencia al 'World' de Box2D para poder
     * crear el 'Body' del nuevo proyectil.
     */
    public void shoot(World box2DWorld, float angulo, float potencia) {
        
        System.out.println("¡Disparando con ángulo " + angulo + "!");
    }
}