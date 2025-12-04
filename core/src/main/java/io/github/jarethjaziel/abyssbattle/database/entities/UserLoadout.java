package io.github.jarethjaziel.abyssbattle.database.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import io.github.jarethjaziel.abyssbattle.util.SkinType;


/**
 * Entidad que representa la configuración de equipamiento actual de un usuario.
 * <p>
 * Funciona como un registro de "Slots": Para un Usuario dado y un Tipo de Skin específico,
 * guarda cuál es la Skin activa. Esto permite que el usuario tenga equipada una skin de Tropa
 * y una de Cañón simultáneamente sin conflictos.
 */
@DatabaseTable(tableName = "user_loadouts")
public class UserLoadout {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "user_id")
    private User user;

    /**
     * Tipo de skin (Categoría del slot).
     * Se guarda explícitamente para facilitar consultas rápidas como:
     * "Dame la skin de TIPO CANNON que tiene equipada el usuario X".
     */
    @DatabaseField(dataType = DataType.ENUM_STRING)
    private SkinType skinType;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "skin_id")
    private Skin activeSkin;
    
    /**
     * Constructor vacío requerido por el ORM.
     */
    public UserLoadout() {}

    /**
     * Crea un nuevo registro de equipamiento.
     *
     * @param user El usuario que equipa el objeto.
     * @param skin La skin que se va a equipar.
     */
    public UserLoadout(User user, Skin skin) {
        this.user = user;
        this.activeSkin = skin;
        this.skinType = skin.getType(); 
    }
    
    /**
     * Cambia la skin activa en este slot.
     * Actualiza también el tipo para mantener la coherencia de datos.
     *
     * @param skin La nueva skin a equipar.
     */
    public void setActiveSkin(Skin skin) {
        this.activeSkin = skin;
        this.skinType = skin.getType();
    }

    public User getUser() {
        return user;
    }

    public SkinType getSkinType() {
        return skinType;
    }

    public Skin getActiveSkin() {
        return activeSkin;
    }
}