package io.github.jarethjaziel.abyssbattle.model;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {

        Object a = contact.getFixtureA().getBody().getUserData();
        Object b = contact.getFixtureB().getBody().getUserData();

        if (a instanceof Projectile && b instanceof Troop) {
            Troop troop = (Troop) b;
            Projectile proj = (Projectile) a;

            troop.receiveDamage(proj.getDamage());
            proj.destroy();
        }

        if (b instanceof Projectile && a instanceof Troop) {
            Troop troop = (Troop) a;
            Projectile proj = (Projectile) b;

            troop.receiveDamage(proj.getDamage());
            proj.destroy();
        }
    }

    @Override 
    public void endContact(Contact contact) {

    }
    @Override 
    public void preSolve(Contact c, Manifold oldManifold) {

    }
    @Override 
    public void postSolve(Contact c, ContactImpulse impulse) {

    }
}
