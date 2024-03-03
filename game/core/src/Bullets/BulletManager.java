package Bullets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static Screens.SettingsScreen.soundValue;

public class BulletManager {
    public static ArrayList<Bullet> bulletsToRemove;
    private ArrayList<Bullet> bullets;
    private World world;
    private int nextBulletId;
    private static Map<Integer, Bullet> bulletsById;

    private final Sound bulletSound;
    private long soundID;

    /**
     * Constructor
     *
     * @param world
     */
    public BulletManager(World world) {
        this.world = world;
        bullets = new ArrayList<>();
        bulletsToRemove = new ArrayList<>();
        bulletsById = new HashMap<>();
        nextBulletId = 1; // Starting ID for bullets

        // soundEffects
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("WeaponSounds/blaster.mp3"));
    }

    /**
     * Obtains a bullet and adds it to the appropriate data structures
     *
     * @param x
     * @param y
     * @return
     */
    public Bullet obtainBullet(float x, float y) {
        int bulletId = nextBulletId++;
        Bullet bullet = new Bullet(world, x, y, bulletId);
        bulletsById.put(bulletId, bullet); // Store the bullet in the HashMap
        bullets.add(bullet);

        // plays the sound and returns a soundID that is used to make the volume lower
        soundID = bulletSound.play();
        bulletSound.setVolume(soundID, soundValue);
        return bullet;
    }

    /**
     * Adds the bullet to the list of bullets that will be removed
     *
     * @param bullet
     */
    public static void freeBullet(Bullet bullet) {
        bulletsToRemove.add(bullet);
    }

    /**
     * Gets a bullet by id
     *
     * @param id
     * @return
     */
    public static Bullet getBulletById(int id) {
        return bulletsById.get(id);
    }

    /**
     * Updates the info for the bullets
     *
     * @param deltaTime
     */
    public void update(float deltaTime) {
        // Update each bullet
        for (Bullet bullet : bullets) {
            bullet.update(deltaTime);
        }

        // Remove bullets that are marked for removal
        for (Bullet bullet : bulletsToRemove) {
            bullets.remove(bullet);
            bullet.destroy();
            bullet.dispose();
        }
        bulletsToRemove.clear();
    }

    /**
     * Returns all the bullets
     *
     * @return
     */
    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
}
