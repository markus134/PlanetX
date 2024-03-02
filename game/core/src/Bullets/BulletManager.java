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

    public BulletManager(World world) {
        this.world = world;
        bullets = new ArrayList<>();
        bulletsToRemove = new ArrayList<>();
        bulletsById = new HashMap<>();
        nextBulletId = 1; // Starting ID for bullets

        // soundEffects
        bulletSound = Gdx.audio.newSound(Gdx.files.internal("WeaponSounds/blaster.mp3"));
    }

    public Bullet obtainBullet(float x, float y) {
        Bullet bullet = new Bullet(world, x, y);
        int bulletId = nextBulletId++;
        bullet.setId(bulletId); // Set the ID for the bullet
        bulletsById.put(bulletId, bullet); // Store the bullet in the HashMap
        bullets.add(bullet);

        // plays the sound and returns a soundID that is used to make the volume lower
        soundID = bulletSound.play();
        bulletSound.setVolume(soundID, soundValue);
        return bullet;
    }

    public static void freeBullet(Bullet bullet) {
        bulletsToRemove.add(bullet);
    }

    public static Bullet getBulletById(int id) {
        return bulletsById.get(id);
    }

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

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
}
