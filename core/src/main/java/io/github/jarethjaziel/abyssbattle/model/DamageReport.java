package io.github.jarethjaziel.abyssbattle.model;

public class DamageReport {
    private boolean killOccurred;
    private int totalDamageDealt;

    public DamageReport(boolean killOccurred, int totalDamageDealt) {
        this.killOccurred = killOccurred;
        this.totalDamageDealt = totalDamageDealt;
    }

    public boolean killOccurred() {
        return killOccurred;
    }

    public int getTotalDamageDealt() {
        return totalDamageDealt;
    }
}