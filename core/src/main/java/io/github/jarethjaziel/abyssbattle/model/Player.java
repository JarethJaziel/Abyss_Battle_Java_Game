package io.github.jarethjaziel.abyssbattle.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un jugador en la partida.
 * <p>
 * Esta entidad agrupa los recursos controlables por el usuario o la IA,
 * incluyendo su identificación, su cañón principal y sus tropas desplegadas.
 */
public class Player {

    /** Identificador único del jugador (ej. 1 para P1, 2 para P2). */
    private int id;

    /** El cañón principal asignado a este jugador. */
    private Cannon cannon;

    /** Lista de tropas activas que pertenecen a este jugador. */
    private List<Troop> troopList;

    /**
     * Crea un nuevo jugador.
     *
     * @param id El ID numérico del jugador.
     */
    public Player(int id) {
        this.id = id;
        this.troopList = new ArrayList<>();
    }

    public int getId() { return id; }
    public List<Troop> getTroopList() { return troopList; }

    public void setCannon(Cannon cannon) {
        this.cannon = cannon;
    }

    public Cannon getCannon() {
        return cannon;
    }

    public void addTroop(Troop troop){
        troopList.add(troop);
    }

    /**
     * Delega la acción de disparo al cañón del jugador.
     *
     * @param factory Fábrica física necesaria para crear el proyectil.
     * @param power   Potencia del disparo (0-100).
     * @param damage  Daño base del proyectil.
     */
    public void shoot(PhysicsFactory factory, float power, int damage) {
        cannon.shoot(factory, power, damage);
    }

}
