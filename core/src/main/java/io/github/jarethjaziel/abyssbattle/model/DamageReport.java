package io.github.jarethjaziel.abyssbattle.model;
/**
 * DTO (Data Transfer Object) inmutable que encapsula los resultados de un cálculo de daño.
 * <p>
 * Se utiliza para transportar la información resultante del {@link CombatManager} hacia la
 * {@link GameLogic}, permitiendo actualizar las estadísticas y el flujo de turnos sin
 * acoplar la lógica de combate con el estado del juego.
 */
public class DamageReport {

    /** Indica si el ataque resultó en la destrucción de al menos una tropa enemiga. */
    private boolean killOccurred;

    /** La cantidad total de daño infligido sumando todos los objetivos afectados. */
    private int totalDamageDealt;

    /**
     * Construye un nuevo reporte de daño.
     *
     * @param killOccurred     Verdadero si hubo al menos una baja.
     * @param totalDamageDealt Suma total del daño aplicado.
     */
    public DamageReport(boolean killOccurred, int totalDamageDealt) {
        this.killOccurred = killOccurred;
        this.totalDamageDealt = totalDamageDealt;
    }

    /**
     * Verifica si se eliminó alguna tropa en este evento de daño.
     *
     * @return true si hubo bajas, false en caso contrario.
     */
    public boolean killOccurred() {
        return killOccurred;
    }

    /**
     * Obtiene el daño total infligido en este evento.
     *
     * @return Cantidad de daño total.
     */
    public int getTotalDamageDealt() {
        return totalDamageDealt;
    }
}