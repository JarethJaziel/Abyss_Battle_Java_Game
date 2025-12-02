package io.github.jarethjaziel.abyssbattle.database.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import io.github.jarethjaziel.abyssbattle.util.SkinType;

@DatabaseTable(tableName = "user_loadouts")
public class UserLoadout {

    @DatabaseField(generatedId = true)
    private int id;

    // Relación con el Usuario
    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "user_id")
    private User user;

    @DatabaseField(dataType = DataType.ENUM_STRING)
    private SkinType skinType;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = "skin_id")
    private Skin activeSkin;
    

    public UserLoadout() {}

    public UserLoadout(User user, Skin skin) {
        this.user = user;
        this.activeSkin = skin;
        this.skinType = skin.getType(); 
    }
    
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