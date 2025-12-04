package io.github.jarethjaziel.abyssbattle.gameutil.manager;

import com.badlogic.gdx.Gdx;

import io.github.jarethjaziel.abyssbattle.database.entities.User;

/**
 * Gestor global de la sesión del usuario actual (Singleton).
 * <p>
 * Mantiene la referencia en memoria del usuario que ha iniciado sesión en el juego.
 * Actúa como punto de acceso global para obtener información del perfil del jugador
 * (monedas, nombre, stats) desde cualquier pantalla.
 */
public class SessionManager {

    private static final String TAG = SessionManager.class.getSimpleName();

    private static SessionManager instance;
    private User currentUser;

    /**
     * Constructor privado para implementar el patrón Singleton.
     */
    private SessionManager() {
    }

    /**
     * Obtiene la instancia única del gestor de sesión.
     * @return La instancia activa de {@link SessionManager}.
     */
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Establece el usuario activo en la sesión (Login).
     *
     * @param user El objeto usuario recuperado de la base de datos.
     */
    public void login(User user) {
        this.currentUser = user;
        if (user != null) {
            Gdx.app.log(TAG, "Sesión iniciada: " + user.getUsername());
        }
    }

    /**
     * Cierra la sesión actual, eliminando la referencia al usuario.
     */
    public void logout() {
        if (currentUser != null) {
            Gdx.app.log(TAG, "Sesión cerrada: " + currentUser.getUsername());
        }
        this.currentUser = null;
    }

    /**
     * Obtiene el objeto del usuario actual.
     *
     * @return El {@link User} logueado, o {@code null} si no hay sesión activa.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Verifica si existe una sesión activa válida.
     *
     * @return {@code true} si hay un usuario logueado.
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Obtiene el nombre de usuario de forma segura (null-safe).
     *
     * @return El nombre del usuario o "Invitado" si no hay sesión.
     */
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "Invitado";
    }
}
