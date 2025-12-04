package io.github.jarethjaziel.abyssbattle.database.entities;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable; 

/**
 * Entidad que representa las estadísticas de combate de un jugador.
 * <p>
 * Esta clase cumple un doble rol en la arquitectura actual:
 * 1. <b>Persistencia:</b> Mapea la tabla "stats" para guardar el historial del jugador.
 * 2. <b>Sesión (DTO):</b> Se usa en memoria (GameLogic) para acumular los datos de la partida en curso.
 */
@DatabaseTable(tableName = "stats")
public class Stats {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private int played;

    @DatabaseField
    private int won;

    @DatabaseField
    private int lost;

    @DatabaseField
    private int hits;

    @DatabaseField
    private int misses;

    @DatabaseField
    private int damageTotal;
    
    @DatabaseField 
    private Date lastPlayed;

    /**
     * Constructor por defecto. Inicializa todos los contadores a 0 y la fecha al momento actual.
     */
    public Stats() {
        this.lastPlayed = new Date();
        this.played = 0;
        this.won = 0;
        this.lost = 0;
        this.hits = 0;
        this.misses = 0;
        this.damageTotal = 0;
    }

    public void addWin() { this.won++; this.played++; }
    public void addLoss() { this.lost++; this.played++; }
    public void addHit(){ this.hits++; }
    public void addMiss(){ this.misses++; }
    public void addDamage(int damage){ this.damageTotal += damage; }
    public void setLastPlayed(Date date) { this.lastPlayed = date; }

    public int getId() { 
        return id;
    }
    
    public int getPlayed() {
        return played;
    }

    public int getWon() {
        return won;
    }

    public int getLost() {
        return lost;
    }

    public int getHits() {
        return hits;
    }

    public int getMisses() {
        return misses;
    }

    public int getDamageTotal() {
        return damageTotal;
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public void setWon(int won) {
        this.won = won;
    }

    public void setLost(int lost) {
        this.lost = lost;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

    public void setMisses(int misses) {
        this.misses = misses;
    }

    public void setDamageTotal(int damageTotal) {
        this.damageTotal = damageTotal;
    }

    @Override
    public String toString() {
        return "Stats [played=" + played + ", won=" + won + ", lost=" + lost + ", hits=" + hits + ", misses=" + misses
                + ", damageTotal=" + damageTotal + ", lastPlayed=" + lastPlayed + "]";
    }
    
}