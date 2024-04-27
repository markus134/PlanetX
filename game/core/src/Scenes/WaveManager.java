package Scenes;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;

/**
 * Represents a wave manager that controls the progression of waves.
 * Each wave lasts for 60 seconds, and there are five waves in total.
 * Displays the current wave number.
 */
public class WaveManager extends Actor {
    public static final int TOTAL_WAVES = 5;  // Total number of waves
    public static final float WAVE_DURATION = 60f;  // Duration of each wave in seconds

    private int currentWave;  // The current wave number (1-5)
    private float timeInWave;  // The elapsed time in the current wave
    private final Timer.Task waveTimerTask;  // Timer task to track the wave time

    /**
     * Constructor for the WaveManager class.
     * Initializes the wave manager with the first wave.
     */
    public WaveManager() {
        currentWave = 1;  // Start with the first wave
        timeInWave = 0;  // Reset the time

        // Initialize the timer task
        waveTimerTask = new Timer.Task() {
            @Override
            public void run() {
                System.out.println(timeInWave);
                timeInWave++;  // Increment the timer every second

                // If the wave duration is reached, advance to the next wave
                if (timeInWave >= WAVE_DURATION && currentWave < TOTAL_WAVES) {
                    goToNextWave();  // Move to the next wave
                }
            }
        };

        // Start the timer to run every second
        Timer.schedule(waveTimerTask, 0, 1);  // Start immediately, repeat every second
    }

    /**
     * Moves to the next wave and resets the timer.
     */
    public void goToNextWave() {
        if (currentWave < TOTAL_WAVES) {
            currentWave++;  // Advance to the next wave
            timeInWave = 0;  // Reset the timer
        }
    }

    /**
     * Gets the current wave number.
     *
     * @return The current wave number.
     */
    public int getCurrentWave() {
        return currentWave;
    }

    /**
     * Determines if the final wave has been reached.
     *
     * @return True if the final wave has been reached, false otherwise.
     */
    public boolean isFinalWave() {
        return currentWave == TOTAL_WAVES;
    }

    /**
     * Gets the elapsed time in the current wave.
     *
     * @return The elapsed time in the current wave.
     */
    public float getTimeInWave() {
        return timeInWave;
    }

    /**
     * Disposes of the timer task and other resources.
     */
    public void dispose() {
        if (waveTimerTask != null) {
            waveTimerTask.cancel();  // Stop the timer when disposing
        }
    }
}
