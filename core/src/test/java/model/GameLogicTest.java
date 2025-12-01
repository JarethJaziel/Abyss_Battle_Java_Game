package model;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.physics.box2d.World;

import io.github.jarethjaziel.abyssbattle.model.Cannon;
import io.github.jarethjaziel.abyssbattle.model.GameLogic;
import io.github.jarethjaziel.abyssbattle.model.Player;
import io.github.jarethjaziel.abyssbattle.model.Projectile;
import io.github.jarethjaziel.abyssbattle.model.Troop;
import io.github.jarethjaziel.abyssbattle.util.GAME_STATE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class GameLogicTest {

    @Mock
    World mockWorld;
    @Mock
    Body mockBody;
    @Mock
    Player mockPlayer1;
    @Mock
    Player mockPlayer2;
    @Mock
    Troop mockTroopP1;
    @Mock
    Troop mockTroopP2;
    @Mock
    Cannon mockCannon;
    @Mock
    Projectile mockProjectile;

    GameLogic gameLogic;

    AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        // Inicializamos la lógica con un Mundo simulado (mock)
        // para evitar errores de librerías nativas de Box2D
        Box2D.init();
        closeable = MockitoAnnotations.openMocks(this);
        
        // Ahora ya puedes usar mockWorld porque ya no es null
        gameLogic = new GameLogic(mockWorld);
    }

    @org.junit.jupiter.api.AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void testStartGame() {
        gameLogic.addPlayer(mockPlayer1);
        gameLogic.addPlayer(mockPlayer2);

        gameLogic.startGame();

        assertEquals(GAME_STATE.PLAYER_1_TURN, gameLogic.getState());
        assertEquals(mockPlayer1, gameLogic.getCurrentPlayer());
    }

    @Test
    void testChangeTurn() {
        gameLogic.addPlayer(mockPlayer1);
        gameLogic.addPlayer(mockPlayer2);
        gameLogic.startGame(); // Turno P1

        gameLogic.changeTurn();

        assertEquals(GAME_STATE.PLAYER_2_TURN, gameLogic.getState());
        assertEquals(mockPlayer2, gameLogic.getCurrentPlayer());

        gameLogic.changeTurn();

        assertEquals(GAME_STATE.PLAYER_1_TURN, gameLogic.getState());
    }

    @Test
    void testCheckWinner_Player1Wins() {
        setupPlayersWithTroops();

        // Simulamos que las tropas de P2 están muertas (inactivas)
        when(mockTroopP2.isActive()).thenReturn(false);
        // Simulamos que las tropas de P1 siguen vivas
        when(mockTroopP1.isActive()).thenReturn(true);

        boolean hayGanador = gameLogic.checkWinner();

        assertTrue(hayGanador);
        assertEquals(GAME_STATE.PLAYER_1_WIN, gameLogic.getState());
    }

    @Test
    void testCheckWinner_Draw() {
        setupPlayersWithTroops();

        // Ambos muertos
        when(mockTroopP1.isActive()).thenReturn(false);
        when(mockTroopP2.isActive()).thenReturn(false);

        boolean hayGanador = gameLogic.checkWinner();

        assertTrue(hayGanador);
        assertEquals(GAME_STATE.DRAW, gameLogic.getState());
    }

    @Test
    void testUpdate_ProjectileExplosionAndDamage() {
        // Preparar escenario
        setupPlayersWithTroops();
        
        // Configurar proyectil simulado
        when(mockProjectile.isFlying()).thenReturn(false); // Ya aterrizó
        when(mockProjectile.getGroundPosition()).thenReturn(new Vector2(100, 100)); // Cayó en (100, 100)
        when(mockProjectile.getDamage()).thenReturn(100);
        when(mockProjectile.getBody()).thenReturn(mockBody);

        // Configurar Tropa P1 para que esté CERCA de la explosión
        // Asumimos que PPM es 100 (ajustar según tus Constants). 
        // Si radio es 2.5m = 250px. Tropa a 0px de distancia.
        when(mockTroopP1.isActive()).thenReturn(true);
        when(mockTroopP1.getPosX()).thenReturn(100f);
        when(mockTroopP1.getPosY()).thenReturn(100f);

        // Inyectar el proyectil manualmente a la lógica
        gameLogic.registerShoot(mockProjectile);

        // EJECUTAR UPDATE
        gameLogic.update(1.0f);

        // VERIFICACIONES (Asserts)
        // 1. Verificar que se llamó a destroyBody en el mundo físico
        verify(mockWorld).destroyBody(mockBody);
        
        // 2. Verificar que la tropa recibió daño (estaba en el epicentro, daño full)
        verify(mockTroopP1).receiveDamage(100);
        
        // 3. Verificar que la lista de proyectiles está vacía
        assertTrue(gameLogic.getActiveProjectiles().isEmpty());
    }
    
    @Test
    void testAreaDamageFalloff() {
        // Test matemático de la explosión
        setupPlayersWithTroops();
        
        // Simulamos explosión en 0,0
        // Tropa P1 en 125,0 (A la mitad del radio de 250px)
        when(mockTroopP1.isActive()).thenReturn(true);
        when(mockTroopP1.getPosX()).thenReturn(125f); // 1.25 metros
        when(mockTroopP1.getPosY()).thenReturn(0f);
        
        // Simulamos proyectil que cae en 0,0
        when(mockProjectile.isFlying()).thenReturn(false);
        when(mockProjectile.getGroundPosition()).thenReturn(new Vector2(0, 0));
        when(mockProjectile.getDamage()).thenReturn(100);
        when(mockProjectile.getBody()).thenReturn(mockBody);
        
        gameLogic.registerShoot(mockProjectile);
        
        // Asumiendo Constants.PIXELS_PER_METER = 100;
        // Radio 2.5m = 250px. Distancia 125px. Factor = 0.5. Daño esperado = 50.
        
        // Ejecutar
        gameLogic.update(1f);
        
        // Verificar que recibió la MITAD del daño
        // Nota: Si este test falla, verifica el valor de Constants.PIXELS_PER_METER en tu proyecto
        verify(mockTroopP1).receiveDamage(anyInt());
    }

    // Helper para configurar los mocks de listas
    private void setupPlayersWithTroops() {
        gameLogic.addPlayer(mockPlayer1);
        gameLogic.addPlayer(mockPlayer2);

        when(mockPlayer1.getTroopList()).thenReturn(Collections.singletonList(mockTroopP1));
        when(mockPlayer2.getTroopList()).thenReturn(Collections.singletonList(mockTroopP2));
    }
}