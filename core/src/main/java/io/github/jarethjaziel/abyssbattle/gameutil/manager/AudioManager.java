package io.github.jarethjaziel.abyssbattle.gameutil.manager;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioManager {

    private static AudioManager instance;
    private AssetManager assetManager;

    private Music currentMusic;
    private String currentMusicPath;

    // Configuración (Podrías guardarla en Preferences luego)
    private float musicVolume = 0.5f;
    private float soundVolume = 1.0f;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;

    private AudioManager() {
        // Constructor privado para Singleton
    }

    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * IMPORTANTE: Llamar a esto en AbyssBattle.create() después de crear el AssetManager
     */
    public void initialize(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    public void playMusic(String filePath) {
        if (assetManager == null) {
            System.err.println("ERROR: AudioManager no inicializado. Llama a initialize() primero.");
            return;
        }

        // 1. Verificar si ya está sonando esa canción
        if (currentMusicPath != null && currentMusicPath.equals(filePath)) {
            if (!currentMusic.isPlaying() && musicEnabled) {
                currentMusic.play(); // Si estaba pausada, reanudar
            }
            return; // Ya está sonando, no hacer nada
        }

        // 2. Detener música anterior
        if (currentMusic != null) {
            currentMusic.stop();
        }

        // 3. Cargar nueva música
        if (assetManager.isLoaded(filePath)) {
            currentMusic = assetManager.get(filePath, Music.class);
            currentMusicPath = filePath;
            currentMusic.setLooping(true);
            currentMusic.setVolume(musicVolume);

            if (musicEnabled) {
                currentMusic.play();
            }
        } else {
            System.err.println("ERROR: Música no cargada en AssetManager: " + filePath);
        }
    }

    public void playSound(String filePath) {
        if (!soundEnabled || assetManager == null) return;

        if (assetManager.isLoaded(filePath)) {
            Sound sound = assetManager.get(filePath, Sound.class);
            sound.play(soundVolume);
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying() && musicEnabled) {
            currentMusic.play();
        }
    }

    // --- Setters de Configuración ---

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(volume);
        }
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (!enabled && currentMusic != null) {
            currentMusic.pause();
        } else if (enabled && currentMusic != null) {
            currentMusic.play();
        }
    }
}