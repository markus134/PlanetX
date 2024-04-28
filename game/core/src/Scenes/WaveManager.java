package Scenes;

import Screens.PlayScreen;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Timer;

/**
 * Represents a wave manager that controls the progression of waves.
 * Each wave lasts for 60 seconds, and there are five waves in total.
 * Displays the current wave number.
 */
public class WaveManager extends Actor {
    public static final int TOTAL_WAVES = 5;  // Total number of waves, last one isn't really a wave and is meant to end the game
    public static final float WAVE_DURATION = 60f;  // Duration of each wave in seconds
    private boolean finishedAllWaves = false;
    private int currentWave;  // The current wave number (1-5)
    private int timeInWave;  // The elapsed time in the current wave
    private final Timer.Task waveTimerTask;  // Timer task to track the wave time
    /**
     * Constructor for the WaveManager class.
     * Initializes the wave manager with the first wave.
     */
    public WaveManager(PlayScreen playScreen, int currentWave1, int timeInWave1) {
        this.currentWave = currentWave1;
        this.timeInWave = timeInWave1;

        // Initialize the timer task
        waveTimerTask = new Timer.Task() {
            @Override
            public void run() {
                System.out.println(timeInWave);

                // Start counting when the grace period is over and the player is viewing playScreen
                if (!playScreen.opponents.isEmpty() && playScreen.game.getScreen().equals(playScreen)) {
                    timeInWave++;  // Increment the timer every second

                    // If the wave duration is reached, advance to the next wave
                    if (timeInWave >= WAVE_DURATION && currentWave < TOTAL_WAVES) {
                        goToNextWave();  // Move to the next wave
                    } else if (timeInWave >= WAVE_DURATION){
                        finishedAllWaves = true;
                    }
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
        return finishedAllWaves;
    }

    /**
     * Gets the elapsed time in the current wave.
     * @return The elapsed time in the current wave.
     */
    public int getTimeInWave() {
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
