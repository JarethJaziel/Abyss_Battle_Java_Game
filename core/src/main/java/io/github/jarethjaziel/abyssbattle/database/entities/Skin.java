package io.github.jarethjaziel.abyssbattle.database.entities;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import io.github.jarethjaziel.abyssbattle.util.SkinType;

/**
 * Entidad que representa un artículo cosmético o unidad disponible en el juego.
 * <p>
 * Esta clase mapea la tabla "skins" en la base de datos y define las propiedades
 * inmutables del catálogo, como el nombre, precio y tipo de unidad (Tropa o Cañón).
 */
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

    /**
     * Constructor vacío requerido por ORMLite para la instanciación por reflexión.
     */
    public Skin() {
    }

    /**
     * Constructor para crear una nueva definición de Skin.
     *
     * @param name  Nombre único de la skin (debe coincidir con el nombre del asset gráfico).
     * @param price Costo en monedas para adquirirla.
     * @param type  Categoría de la skin ({@link SkinType#TROOP} o {@link SkinType#CANNON}).
     */
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

    public int getPrice() {
        return price;
    }

    public SkinType getType() {
        return type;
    }

    // setters

    public void setType(SkinType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Skin{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", price=" + price +
            ", type=" + type +
            '}';
    }
}
