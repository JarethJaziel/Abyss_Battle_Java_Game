package io.github.jarethjaziel.abyssbattle.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import io.github.jarethjaziel.abyssbattle.util.Constants;

import java.util.Date;

/**
 * Entidad que representa a un jugador registrado en el sistema.
 * <p>
 * Almacena las credenciales, el progreso económico (monedas) y mantiene
 * una relación con sus estadísticas de combate.
 */
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

    @DatabaseField
    private int coins;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, foreignAutoCreate = true)
    private Stats stats;

    /**
     * Constructor vacío requerido por ORMLite.
     */
    public User() {
        createdAt = new Date();
        coins = Constants.NEW_USER_INITIAL_COINS;
    }

    /**
     * Crea un nuevo usuario con credenciales.
     *
     * @param username     Nombre único del usuario.
     * @param passwordHash Contraseña ya encriptada (Hash).
     */
    public User(String username, String passwordHash) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = new Date();
        this.stats = new Stats(); // Stats vacías al inicio
        coins = Constants.NEW_USER_INITIAL_COINS;
    }

    public void setStats(Stats stats) { this.stats = stats; }
    public Stats getStats() { return stats; }

    public int getCoins(){ return coins;  }
    /**
     * Añade monedas al saldo del usuario.
     * @param amount Cantidad a añadir (debe ser positiva).
     */
    public void addCoins(int amount){ 
        this.coins += amount;
    }

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

    /**
     * Descuenta monedas por una compra.
     * NO verifica fondos negativos aquí (eso debe hacerlo el ShopSystem).
     * @param amount Cantidad a restar.
     */
    public void purchase(int amount){
        this.coins -= amount;
    }

    @Override
    public String toString() {
        return "User ID: "+ id + "\nusername: " + username + "\npasswordHash: " + passwordHash + "\ncreatedAt: "
                + createdAt + "\ncoins: " + coins + "\nstats:" + stats;
    }

}
