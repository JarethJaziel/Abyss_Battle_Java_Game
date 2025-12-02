package model;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

import io.github.jarethjaziel.abyssbattle.model.Entity;

class EntityTest {

    private Body mockBody;
    private Entity entity;

    // Clase concreta mínima para poder testear Entity
    private static class TestEntity extends Entity {
        public TestEntity(Body body) {
            super(body);
        }

        @Override
        public boolean isActive() {
            return true;
        }
    }

    @BeforeEach
    void setup() {
        mockBody = mock(Body.class);
        entity = new TestEntity(mockBody);
    }

    @Test
    void constructor_setsBodyCorrectly() {
        assertEquals(mockBody, entity.getBody());
    }

    @Test
    void setBody_changesTheBodyCorrectly() {
        Body newBody = mock(Body.class);
        entity.setBody(newBody);

        assertEquals(newBody, entity.getBody());
    }

    @Test
    void getPosX_returnsBodyPositionX() {
        when(mockBody.getPosition()).thenReturn(new Vector2(5f, 10f));
        assertEquals(5f, entity.getPosX());
    }

    @Test
    void getPosY_returnsBodyPositionY() {
        when(mockBody.getPosition()).thenReturn(new Vector2(5f, 10f));
        assertEquals(10f, entity.getPosY());
    }

    @Test
    void isActive_returnsTrueInTestEntity() {
        assertTrue(entity.isActive());
    }
}
