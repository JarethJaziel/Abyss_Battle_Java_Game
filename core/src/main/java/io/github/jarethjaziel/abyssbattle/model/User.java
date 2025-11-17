package io.github.jarethjaziel.abyssbattle.model;

public class User {

    private String username;
    private String passwordHash;
    private int failedAttempts;
    private long blockUntilTimestamp;

    // Estadísticas
    private int matchesPlayed;
    private int wins;
    private int losses;
    private int totalTroopsEliminated;
    private float averageAccuracy;

    public User(String username, String passwordHash) {}

    public boolean validatePassword(String password) {
        return true;
    }
    public void increaseFailedAttempts() {}
    public void resetFailedAttempts() {}
    public boolean isBlocked() {
        return true;
    }
    public void blockTemporarily() {}

    public void updateStats(int hits, int launches, int troopsEliminated, boolean victory) {}
}

