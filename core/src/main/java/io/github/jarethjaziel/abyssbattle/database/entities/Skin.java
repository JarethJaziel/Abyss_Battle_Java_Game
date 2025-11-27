package io.github.jarethjaziel.abyssbattle.database.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "skins")
public class Skin {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(unique = true)
    private String nombre;

    public Skin() {}

    public Skin(String nombre) {
        this.nombre = nombre;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }
    
}