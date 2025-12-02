package database.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.jarethjaziel.abyssbattle.database.entities.Skin;
import io.github.jarethjaziel.abyssbattle.database.entities.User;
import io.github.jarethjaziel.abyssbattle.database.entities.UserSkin;
import io.github.jarethjaziel.abyssbattle.util.SkinType;

class UserSkinTest {

    @Test
    void testConstructorAssignsUserAndSkin() {
        User user = new User("player1", "hash123");
        Skin skin = new Skin("DefaultTroop", 100, SkinType.TROOP);

        UserSkin userSkin = new UserSkin(user, skin);

        assertEquals(user, userSkin.getUser(), 
                "El usuario debe asignarse correctamente desde el constructor");
        assertEquals(skin, userSkin.getSkin(),
                "La skin debe asignarse correctamente desde el constructor");
    }

    @Test
    void testSetUser() {
        UserSkin userSkin = new UserSkin();
        User user = new User("player2", "pass456");

        userSkin.setUser(user);

        assertEquals(user, userSkin.getUser(),
                "El método setUser debe actualizar el usuario del UserSkin");
    }

    @Test
    void testSetSkin() {
        UserSkin userSkin = new UserSkin();
        Skin skin = new Skin("CannonFire", 300, SkinType.CANNON);

        userSkin.setSkin(skin);

        assertEquals(skin, userSkin.getSkin(),
                "El método setSkin debe actualizar la skin del UserSkin");
    }
}
