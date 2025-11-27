package io.github.jarethjaziel.abyssbattle.database.entities;

import java.util.Date;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable; 

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
    
}