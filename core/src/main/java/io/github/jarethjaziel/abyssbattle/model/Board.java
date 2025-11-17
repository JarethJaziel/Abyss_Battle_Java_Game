package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;

public class Board {

    private float width;
    private float height;

    private List<Troop> troops;

    public Board(float width, float height) {
        this.width = width;
        this.height = height;
        this.troops = new ArrayList<>();
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    /**
     * Verifica si una posición está fuera de los límites del mapa.
     */
    public boolean isOutOfBounds(float x, float y) {
        return x < 0 || y < 0 || x > width || y > height;
    }

    /**
     * Agrega una tropa al tablero.
     */
    public void addTroop(Troop troop) {
        troops.add(troop);
    }

    /**
     * Devuelve todas las tropas (si quieres mostrarlas o contarlas).
     */
    public List<Troop> getTroops() {
        return troops;
    }

    /**
     * Busca si hay una tropa cerca de la posición dada.
     * Esto se usa para detectar colisiones simples.
     * 
     * @param x posición X del proyectil
     * @param y posición Y del proyectil
     * @param radius rango de colisión
     */
    public Troop getTroopAt(float x, float y, float radius) {

        for (Troop t : troops) {
            if (!t.isActive()) continue;

            Vector2 pos = t.getBody().getPosition();

            float dx = pos.x - x;
            float dy = pos.y - y;

            if ((dx * dx + dy * dy) <= (radius * radius)) {
                return t;
            }
        }

        return null;
    }

    /**
     * Cuenta cuántas tropas siguen vivas.
     */
    public int getAliveTroops() {
        int count = 0;

        for (Troop t : troops) {
            if (t.isActive()) count++;
        }

        return count;
    }
}

