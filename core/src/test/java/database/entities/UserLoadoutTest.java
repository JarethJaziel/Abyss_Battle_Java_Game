package database.entities;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.jarethjaziel.abyssbattle.database.entities.*;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

class UserLoadoutTest {

    private User user;
    private Skin troopSkin;
    private Skin cannonSkin;

    @BeforeEach
    void setUp() {
        user = new User("playerTest", "passwordHash");
        troopSkin = new Skin("SoldierSkin", 200, SkinType.TROOP);
        cannonSkin = new Skin("IronCannonSkin", 350, SkinType.CANNON);
    }

    // --- Utilidad para leer atributos privados ---
    private <T> T getPrivate(Object obj, String field, Class<T> type) {
        try {
            Field f = obj.getClass().getDeclaredField(field);

            f.setAccessible(true);
            return type.cast(f.get(obj));
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    void testConstructorSetsFieldsCorrectly() {
        UserLoadout loadout = new UserLoadout(user, troopSkin);

        assertEquals(troopSkin, getPrivate(loadout, "activeSkin", Skin.class));
        assertEquals(SkinType.TROOP, getPrivate(loadout, "skinType", SkinType.class));
    }

    @Test
    void testSetActiveSkinUpdatesSkinAndType() {
        UserLoadout loadout = new UserLoadout(user, troopSkin);
        loadout.setActiveSkin(cannonSkin);

        assertEquals(cannonSkin, getPrivate(loadout, "activeSkin", Skin.class));
        assertEquals(SkinType.CANNON, getPrivate(loadout, "skinType", SkinType.class));
    }

    @Test
    void testSkinTypeMatchesNewSkin() {
        UserLoadout loadout = new UserLoadout(user, cannonSkin);

        assertEquals(SkinType.CANNON, getPrivate(loadout, "skinType", SkinType.class));
    }
}
