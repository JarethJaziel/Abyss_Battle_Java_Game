package io.github.jarethjaziel.abyssbattle.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Entidad de relación que representa la propiedad de un ítem (Skin) por un usuario.
 * <p>
 * Mapea la tabla "user_skins". Cada fila en esta tabla significa que el usuario 'X'
 * ha comprado o desbloqueado la skin 'Y'. Es la base del inventario.
 */
@DatabaseTable(tableName = "user_skins")
public class UserSkin {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true)
    private User user;

    @DatabaseField(foreign = true)
    private Skin skin;

    /**
     * Constructor vacío requerido por ORMLite.
     */
    public UserSkin() {}

    /**
     * Crea un nuevo registro de propiedad.
     *
     * @param user El usuario que adquiere la skin.
     * @param skin La skin adquirida.
     */
    public UserSkin(User user, Skin skin) {
        this.user = user;
        this.skin = skin;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
    }

    
}
