package io.github.jarethjaziel.abyssbattle.model;

import java.util.List;

public class AspectGallery {

    private List<String> aspects;
    private List<String> usedAspects;

    public AspectGallery(List<String> aspects) {}

    public List<String> getAvailableAspects() {
        return aspects;
    }
    public boolean selectAspect(Player player, String aspect) {
        return true;
    }
    public void markAsUsed(String aspect) {}
}
