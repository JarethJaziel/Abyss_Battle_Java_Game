package database.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

class SkinTest {

    @Test
    void testConstructorAndGetters() {
        Skin skin = new Skin("Golden Armor", 500, SkinType.CANNON);

        assertEquals("Golden Armor", skin.getName(), "El nombre debe coincidir");
        assertEquals(SkinType.CANNON, skin.getType(), "El tipo debe coincidir");
        
        // id no se asigna hasta que ORMlite lo persista
        assertEquals(0, skin.getId(), "El id debe ser 0 antes de persistir en BD");
    }

    @Test
    void testSetType() {
        Skin skin = new Skin("Warrior", 300, SkinType.TROOP);

        skin.setType(SkinType.CANNON);

        assertEquals(SkinType.CANNON, skin.getType(), 
            "El tipo debe actualizarse correctamente");
    }

    @Test
    void testEmptyConstructorExists() {
        Skin skin = new Skin();

        assertNotNull(skin, "El constructor vacío debe existir para ORMLite");
    }
}
