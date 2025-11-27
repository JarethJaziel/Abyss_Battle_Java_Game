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

    // RELACIÓN UNO A UNO:
    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private Stats stats;

    public User() {}

    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = new Date();
        this.stats = new Stats(); // Stats vacías al inicio
    }
    
    public void setStats(Stats stats) { this.stats = stats; }
    public Stats getStats() { return stats; }
}