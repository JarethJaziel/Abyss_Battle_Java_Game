package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.badlogic.gdx.physics.box2d.Body;

import io.github.jarethjaziel.abyssbattle.model.Troop;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Pruebas Unitarias para la Clase Troop")
class TroopTest {

    // Necesitamos simular el objeto Body, ya que Box2D no se inicializa en una prueba unitaria normal.
    // Usaremos Mockito para crear un objeto simulado (Mock).
    @Mock
    private Body mockBody;

    // Objeto bajo prueba
    private Troop troop;
    private final int INITIAL_HEALTH = 100;

    // Inicializa el Mock y la tropa antes de cada prueba
    @BeforeEach
    void setUp() {
        // Inicializa los mocks anotados (como mockBody)
        MockitoAnnotations.openMocks(this);
        // Creamos una nueva instancia de Troop con el Body simulado
        troop = new Troop(mockBody, INITIAL_HEALTH);
    }

    // --- 1. Pruebas del Constructor y Estado Inicial ---

    @Test
    @DisplayName("Debe inicializarse con la salud correcta")
    void shouldInitializeWithCorrectHealth() {
        assertEquals(INITIAL_HEALTH, troop.getHealth(), "La salud inicial debe ser 100.");
    }

    @Test
    @DisplayName("Debe estar activo al inicio")
    void shouldBeActiveInitially() {
        assertTrue(troop.isActive(), "La tropa debe estar activa al inicio.");
    }

    // --- 2. Pruebas del Método receiveDamage ---

    @Test
    @DisplayName("Debe reducir la salud correctamente")
    void shouldReduceHealthCorrectly() {
        int damage = 30;
        int expectedHealth = 70;
        
        troop.receiveDamage(damage);
        
        assertEquals(expectedHealth, troop.getHealth(), "La salud debe ser 70 después de 30 de daño.");
        assertTrue(troop.isActive(), "La tropa debe seguir activa.");
    }

    @Test
    @DisplayName("La salud no debe ser menor que cero (Muerte simple)")
    void healthShouldNotGoBelowZeroSimpleKill() {
        int damage = 100;
        
        troop.receiveDamage(damage);
        
        assertEquals(0, troop.getHealth(), "La salud debe ser 0 al recibir daño igual a la salud total.");
        assertFalse(troop.isActive(), "La tropa debe estar inactiva (muerta).");
    }

    @Test
    @DisplayName("La salud no debe ser menor que cero (Overkill)")
    void healthShouldNotGoBelowZeroOverkill() {
        int overkillDamage = 150;
        
        troop.receiveDamage(overkillDamage);
        
        assertEquals(0, troop.getHealth(), "La salud debe ser 0 al recibir más daño que la salud total (Overkill).");
        assertFalse(troop.isActive(), "La tropa debe estar inactiva (muerta).");
    }

    // --- 3. Pruebas del Estado isActive ---

    @Test
    @DisplayName("Debe cambiar a inactivo después de la muerte")
    void shouldBecomeInactiveAfterDeath() {
        // 1. Reducir la salud sin matar
        troop.receiveDamage(50);
        assertTrue(troop.isActive(), "Debe seguir activa (50 HP).");

        // 2. Matar
        troop.receiveDamage(50); 
        assertFalse(troop.isActive(), "Debe volverse inactiva (0 HP).");
    }
}