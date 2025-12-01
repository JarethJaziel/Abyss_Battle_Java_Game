package io.github.jarethjaziel.abyssbattle.database.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import io.github.jarethjaziel.abyssbattle.util.SkinType;

@DatabaseTable(tableName = "skins")
public class Skin {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(unique = true)
    private String name;

    @DatabaseField
    private int price;

    @DatabaseField(dataType = DataType.ENUM_STRING)
    private SkinType type;

    public Skin() {
    }

    public Skin(String name, int price, SkinType type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public SkinType getType() {
        return type;
    }

    public void setType(SkinType type) {
        this.type = type;
    }

}