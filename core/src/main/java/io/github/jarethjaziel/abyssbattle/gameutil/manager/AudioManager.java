package io.github.jarethjaziel.abyssbattle.gameutil.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Gestor centralizado de audio (Música y Efectos de Sonido).
 * <p>
 * Implementa el patrón Singleton para ser accesible desde cualquier parte del
 * juego.
 * Se encarga de reproducir recursos cargados en el {@link AssetManager},
 * gestionar
 * el volumen global y controlar el estado (pausa/reproducción) de la música de
 * fondo.
 */
public class AudioManager {

    private static final String TAG = AudioManager.class.getSimpleName();

    private static AudioManager instance;
    private AssetManager assetManager;

    private Music currentMusic;
    private String currentMusicPath;

    // Configuración (Podrías guardarla en Preferences luego)
    private float musicVolume = 0.5f;
    private float soundVolume = 1.0f;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;

    /**
     * Constructor privado para implementar Singleton.
     */
    private AudioManager() {
    }

    /**
     * Obtiene la instancia única del gestor de audio.
     * 
     * @return La instancia de AudioManager.
     */
    public static AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Inicializa el gestor con el AssetManager del juego.
     * <p>
     * <b>IMPORTANTE:</b> Debe llamarse en {@code AbyssBattle.create()} después de
     * crear el AssetManager
     * y antes de intentar reproducir cualquier sonido.
     *
     * @param assetManager El gestor de assets donde se cargan los sonidos.
     */
    public void initialize(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * Reproduce una pista de música de fondo en bucle.
     * <p>
     * Si la canción solicitada ya está sonando, no la reinicia.
     * Si hay otra canción sonando, la detiene antes de iniciar la nueva.
     *
     * @param filePath Ruta del archivo de música dentro del AssetManager.
     */
    public void playMusic(String filePath) {
        if (assetManager == null) {
            Gdx.app.error(TAG, "ERROR: AudioManager no inicializado. Llama a initialize() primero.");
            return;
        }

        if (currentMusicPath != null && currentMusicPath.equals(filePath)) {
            if (!currentMusic.isPlaying() && musicEnabled) {
                currentMusic.play();
            }
            return;
        }

        if (currentMusic != null) {
            currentMusic.stop();
        }

        if (assetManager.isLoaded(filePath)) {
            currentMusic = assetManager.get(filePath, Music.class);
            currentMusicPath = filePath;
            currentMusic.setLooping(true);
            currentMusic.setVolume(musicVolume);

            if (musicEnabled) {
                currentMusic.play();
                Gdx.app.log(TAG, "Reproduciendo música: " + filePath);
            }
        } else {
            Gdx.app.error(TAG, "ERROR: Música no cargada en AssetManager: " + filePath);
        }
    }

    /**
     * Reproduce un efecto de sonido una vez.
     *
     * @param filePath Ruta del archivo de sonido dentro del AssetManager.
     */
    public void playSound(String filePath) {
        if (!soundEnabled || assetManager == null)
            return;

        if (assetManager.isLoaded(filePath)) {
            Sound sound = assetManager.get(filePath, Sound.class);
            sound.play(soundVolume);
        }
    }

    /**
     * Detiene completamente la música actual.
     */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }
    }

    /**
     * Pausa la música actual sin perder la posición.
     */
    public void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

    /**
     * Reanuda la música si estaba pausada y la música está habilitada.
     */
    public void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying() && musicEnabled) {
            currentMusic.play();
        }
    }

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

    public float getVolume() {
        return currentMusic != null ? currentMusic.getVolume() : 0.5f;
    }
}