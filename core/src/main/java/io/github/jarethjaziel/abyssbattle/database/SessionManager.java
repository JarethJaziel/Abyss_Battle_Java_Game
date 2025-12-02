package io.github.jarethjaziel.abyssbattle.database;

import io.github.jarethjaziel.abyssbattle.database.entities.User;

/**
 * Maneja la sesión del usuario actual durante la ejecución del juego.
 * Singleton para acceso global.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {
        // Constructor privado para Singleton
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Inicia sesión con un usuario
     */
    public void login(User user) {
        this.currentUser = user;
        System.out.println("Sesión iniciada: " + user.getUsername());
    }

    /**
     * Cierra la sesión actual
     */
    public void logout() {
        if (currentUser != null) {
            System.out.println("Sesión cerrada: " + currentUser.getUsername());
        }
        this.currentUser = null;
    }

    /**
     * Obtiene el usuario actual
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Verifica si hay una sesión activa
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }

    /**
     * Obtiene el nombre del usuario actual
     */
    public String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "Invitado";
    }
}
