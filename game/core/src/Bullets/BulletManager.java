package Bullets;

import com.badlogic.gdx.physics.box2d.World;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BulletManager {
    public static ArrayList<Bullet> bulletsToRemove;
    private ArrayList<Bullet> bullets;
    private World world;
    private int nextBulletId;
    private static Map<Integer, Bullet> bulletsById;

    public BulletManager(World world) {
        this.world = world;
        bullets = new ArrayList<>();
        bulletsToRemove = new ArrayList<>();
        bulletsById = new HashMap<>();
        nextBulletId = 1; // Starting ID for bullets
    }

    public Bullet obtainBullet(float x, float y) {
        Bullet bullet = new Bullet(world, x, y);
        int bulletId = nextBulletId++;
        bullet.setId(bulletId); // Set the ID for the bullet
        bulletsById.put(bulletId, bullet); // Store the bullet in the HashMap
        bullets.add(bullet);
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
        }
        bulletsToRemove.clear();
    }

    public ArrayList<Bullet> getBullets() {
        return bullets;
    }
}
