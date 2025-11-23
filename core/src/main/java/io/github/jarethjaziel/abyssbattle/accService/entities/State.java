package io.github.jarethjaziel.abyssbattle.accService.entities;

import java.time.LocalDateTime;

public class State {
    private int userId;
    private int played;
    private int won;
    private int lost;
    private int hits;
    private int misses;
    private int damageTotal;
    private LocalDateTime lastPlayed;

    public State() {}

    public State(int userId, int played, int won, int lost, int hits, int misses, int dmg, LocalDateTime lastPlayed) {
        this.userId = userId;
        this.played = played;
        this.won = won;
        this.lost = lost;
        this.hits = hits;
        this.misses = misses;
        this.damageTotal = dmg;
        this.lastPlayed = lastPlayed;
    }

    public int getUserId() {
         return userId; 
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
    public LocalDateTime getLastPlayed() {
         return lastPlayed; 
         }

    public void setUserId(int userId) {
         this.userId = userId; 
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
    public void setLastPlayed(LocalDateTime lastPlayed) { 
        this.lastPlayed = lastPlayed; 
        }
}
