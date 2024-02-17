package jtetris.swing;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;

/**
 * Audio helper plays sounds.
 */
final class AudioHelper {
    private static final String CANNOT_PLAY_SOUND = "Cannot play sound";
    private static final Logger LOGGER = Logger.getLogger("AudioHelper");
    private static boolean muted;

    private AudioHelper() {}

    /**
     * Plays an audio file.
     */
    static void play(final String file) {
        if (muted) {
            return;
        }

        new Thread(() -> playSync(file)).start();
    }

    static void toggleMute() {
        muted = !muted;
    }

    private static void playSync(AudioInputStream audioInputStream) {
        AudioFormat format = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, format);
        Clip clip = null;
        try {
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(audioInputStream);
            clip.start();
        } catch (IOException | LineUnavailableException ex) {
            LOGGER.log(Level.WARNING, CANNOT_PLAY_SOUND, ex);
        } finally {
            if (clip != null) {
                clip.close();
            }
        }
    }

    private static void playSync(String file) {
        URL url = AudioHelper.class.getResource(file);
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(url);
            playSync(audioInputStream);
        } catch (IOException | UnsupportedAudioFileException ex) {
            LOGGER.log(Level.WARNING, CANNOT_PLAY_SOUND, ex);
        } finally {
            if (audioInputStream != null) {
                try {
                    audioInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
