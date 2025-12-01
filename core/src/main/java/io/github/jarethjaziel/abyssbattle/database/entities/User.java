package io.github.jarethjaziel.abyssbattle.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Date;

@DatabaseTable(tableName = "users")
public class User {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(unique = true)
    private String username;

    @DatabaseField
    private String passwordHash;

    @DatabaseField
    private Date createdAt;

    @DatabaseField(defaultValue = "0")
    private int coins;

    // RELACIÓN UNO A UNO:
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private Stats stats;

    public User() {}

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = new Date();
        this.stats = new Stats(); // Stats vacías al inicio
        final int INITIAL_COINS = 100;
        this.coins = INITIAL_COINS;
    }
    
    public void setStats(Stats stats) { this.stats = stats; }
    public Stats getStats() { return stats; }

    public int getCoins(){ return coins;  }
    public void addCoins(int amount){ this.coins += amount; }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setNewPassword(String rawPassword) {
        String encriptada = PasswordUtils.hash(rawPassword); 
        this.passwordHash = encriptada;
    }

    @Override
    public String toString() {
        return "User ID: "+ id + "\nusername: " + username + "\npasswordHash: " + passwordHash + "\ncreatedAt: "
                + createdAt + "\ncoins: " + coins + "\nstats:" + stats;
    }
    
}