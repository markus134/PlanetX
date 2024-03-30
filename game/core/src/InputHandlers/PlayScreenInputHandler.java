package InputHandlers;

import Bullets.Bullet;
import Opponents.Robot;
import Screens.PlayScreen;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.MyGDXGame;
import crystals.Crystal;
import serializableObjects.BulletData;
import serializableObjects.CrystalToRemove;
import serializableObjects.RobotData;
import Items.Items;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static Screens.PlayScreen.robotDataMap;
import static Screens.PlayScreen.robotIds;
import static Screens.PlayScreen.robots;

public class PlayScreenInputHandler implements InputProcessor {
    public Set<Integer> keysPressed = new HashSet<>();
    public int keyPresses = 0;
    private PlayScreen playScreen;
    private Vector3 touchPoint; // Added to store the touch point in world coordinates
    private float bulletSpeed = 5.0f; // Adjust the bullet speed as needed
    private static final float PLAYER_RADIUS = 16 / MyGDXGame.PPM;
    private static final float BULLET_OFFSET = 8 / MyGDXGame.PPM;
    private boolean isFirstClick = true;
    private static final long SHOOT_COOLDOWN_NANOS = 250_000_000L; // 0.25 seconds in nanoseconds
    private long lastShotTime = 0;
    private long miningStartTime = 0;
    private Crystal closeCrystal;


    /**
     * Constructor
     *
     * @param playScreen
     */
    public PlayScreenInputHandler(PlayScreen playScreen) {
        this.playScreen = playScreen;
        touchPoint = new Vector3();
    }

    /**
     * Handles player input.
     */
    public void handleInput() {
        if (keyPresses > 0 && !playScreen.player.getIsMining()) {
            for (Integer keypress : keysPressed) {
                switch (keypress) {
                    case Input.Keys.W:
                        playScreen.player.move(0, 0.1f);
                        break;
                    case Input.Keys.S:
                        playScreen.player.move(0, -0.1f);
                        break;
                    case Input.Keys.D:
                        playScreen.player.move(0.1f, 0);
                        break;
                    case Input.Keys.A:
                        playScreen.player.move(-0.1f, 0);
                        break;
                    case Input.Keys.NUM_1:
                        playScreen.hud.switchHighlightedSlot(0);
                        break;
                    case Input.Keys.NUM_2:
                        playScreen.hud.switchHighlightedSlot(1);
                        break;
                    case Input.Keys.NUM_3:
                        playScreen.hud.switchHighlightedSlot(2);
                        break;
                    case Input.Keys.NUM_4:
                        playScreen.hud.switchHighlightedSlot(3);
                        break;
                    case Input.Keys.NUM_5:
                        playScreen.hud.switchHighlightedSlot(4);
                        break;
                    case Input.Keys.NUM_6:
                        playScreen.hud.switchHighlightedSlot(5);
                        break;
                    case Input.Keys.NUM_7:
                        playScreen.hud.switchHighlightedSlot(6);
                        break;
                    case Input.Keys.NUM_8:
                        playScreen.hud.switchHighlightedSlot(7);
                        break;
                    case Input.Keys.B:
                        if (isFirstClick) {
                            generateRobot();
                            isFirstClick = false;
                        }
                        break;
                }

            }
        }

    }

    /**
     * Generates robots when the button is clicked.
     */
    private void generateRobot() {
        Robot robot = new Robot(playScreen.world, playScreen);
        String uniqueID = UUID.randomUUID().toString();
        robot.setUuid(uniqueID);

        robotIds.add(uniqueID);
        robots.put(uniqueID, robot);
        robotDataMap.put(uniqueID, new RobotData(robot.getX(), robot.getY(), robot.getHealth(), robot.getUuid()));

        MyGDXGame.client.sendTCP(robotDataMap);
    }

    /**
     * Handles the logic for button clicks.
     */
    private void buttonClick() {
        System.out.println("Button Clicked!");
        // Add your button click logic here
        // This will later be used to regenerate the map without closing and reopening the program
    }

    /**
     * Handles the key down event.
     *
     * @param keycode The keycode of the pressed key.
     * @return True to indicate that the input event was handled.
     */
    @Override
    public boolean keyDown(int keycode) {
        keysPressed.add(keycode);
        keyPresses++;

        return true; // Return true to indicate that the input event was handled
    }

    /**
     * Handles the key up event.
     *
     * @param keycode The keycode of the released key.
     * @return True to indicate that the input event was handled.
     */
    @Override
    public boolean keyUp(int keycode) {
        keysPressed.remove(keycode);

        if (keycode == Input.Keys.B) {
            isFirstClick = true;
        }
        // Reset horizontal velocity when D or A key is released
        if (keycode == Input.Keys.D || keycode == Input.Keys.A) {
            playScreen.player.b2body.setLinearVelocity(0, playScreen.player.b2body.getLinearVelocity().y);
            keyPresses--;
        }

        // Reset vertical velocity when W or S key is released
        if (keycode == Input.Keys.W || keycode == Input.Keys.S) {
            playScreen.player.b2body.setLinearVelocity(playScreen.player.b2body.getLinearVelocity().x, 0);
            keyPresses--;
        }

        // When resizing and clearing the keyPresses and keyPressed hashset, it for some reason sometimes bugs out and decrements from 0 to get -1
        // This is to make sure it doesn't get lower than 0
        if (keyPresses < 0) keyPresses = 0;

        return true;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    /**
     * Handles touch down event.
     *
     * @param screenX The x-coordinate of the touch position.
     * @param screenY The y-coordinate of the touch position.
     * @param pointer The pointer for the event.
     * @param button  The button pressed during the event.
     * @return True if the input event was handled.
     */
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Items selectedItem = playScreen.hud.getHighlightedItem();

            if (TimeUtils.timeSinceNanos(lastShotTime) >= SHOOT_COOLDOWN_NANOS && selectedItem.equals(Items.BLASTER)) {
                shootBullet(touchPoint, screenX, screenY);
            } else if (selectedItem.equals(Items.DRILL)) {
                if (isCloseToCrystal(playScreen.player.getX(), playScreen.player.getY())) {
                    miningStartTime = TimeUtils.nanoTime();
                    System.out.println("close to crystal");
                }
                playScreen.player.setIsMining(true);
            } else if (selectedItem.equals(Items.CRYSTAL)) {
                playScreen.player.recoverHealth(20);
                playScreen.hud.removeHighlightedItem();
            }
        } else if (button == Input.Buttons.RIGHT) {
            System.out.println("right click");
        }

        return true;

    }

    private void shootBullet(Vector3 touchPoint, int screenX, int screenY) {
        // Check if enough time has passed since the last shot
        long currentTime = TimeUtils.nanoTime();

        // Convert screen coordinates to world coordinates
        touchPoint.set(screenX, screenY, 0);
        PlayScreen.gameCam.unproject(touchPoint);

        float x = playScreen.player.getX() + playScreen.player.getWidth() / 2;
        float y = playScreen.player.getY() + playScreen.player.getHeight() / 2;

        Vector2 direction = new Vector2(touchPoint.x - x, touchPoint.y - y);
        direction.nor(); // Normalize the direction vector

        x += direction.x * (PLAYER_RADIUS + BULLET_OFFSET);
        y += direction.y * (PLAYER_RADIUS + BULLET_OFFSET);

        float velocityX = direction.x * bulletSpeed;
        float velocityY = direction.y * bulletSpeed;

        // Shoot a bullet towards the touched position
        Bullet newBullet = playScreen.bulletManager.obtainBullet(x, y);
        newBullet.body.setLinearVelocity(velocityX, velocityY);

        // Update the time of the last shot
        lastShotTime = currentTime;

        MyGDXGame.client.sendTCP(new BulletData(
                velocityX,
                velocityY,
                x,
                y,
                robotDataMap.getWorldUUID()
        ));
    }

    private boolean isCloseToCrystal(float playerX, float playerY) {
        float miningRange = 30f;

        // Convert player's position to screen coordinates
        Vector3 playerPos = new Vector3(playerX, playerY, 0);
        PlayScreen.gameCam.project(playerPos);

        for (Crystal crystal : PlayScreen.crystals) {
            // Convert crystal position to screen coordinates
            Vector3 crystalPos = new Vector3(crystal.getX() / MyGDXGame.PPM, crystal.getY() / MyGDXGame.PPM, 0);
            PlayScreen.gameCam.project(crystalPos);

            System.out.println("player x: " + playerPos.x + " player y: " + playerPos.y);
            System.out.println("crystal x: " + crystalPos.x + " crystal y: " + crystalPos.y);
            // Calculate the distance between the player and the crystal
            float distance = Vector2.dst(playerPos.x, playerPos.y, crystalPos.x, crystalPos.y);
            if (distance <= miningRange) {
                closeCrystal = crystal;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Items selectedItem = playScreen.hud.getHighlightedItem();

            if (selectedItem.equals(Items.DRILL)) {
                if (isCloseToCrystal(playScreen.player.getX(), playScreen.player.getY())) {
                    long miningDuration = TimeUtils.timeSinceNanos(miningStartTime); // Calculate the duration of mining
                    if (miningDuration >= 1_000_000_000L) { // If mining duration is >= 1 second
                        PlayScreen.crystals.remove(closeCrystal);
                        miningStartTime = 0;
                        playScreen.hud.addItemToNextFreeSlot(Items.CRYSTAL);
                    }
                }
                playScreen.player.setIsMining(false); // Stop mining when touch is released
            }
        }

        return true;
    }

    @Override
    public boolean touchCancelled(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }


    @Override
    public boolean scrolled(float amountX, float amountY) {
        if (!playScreen.player.getIsMining()) {
            int scrollDirection = amountY > 0 ? -1 : 1; // Determine scroll direction

            int currentIndex = playScreen.hud.getHighlightedSlotIndex();
            int totalSlots = 8; // Total number of slots in the inventory bar
            int newIndex = (currentIndex + scrollDirection + totalSlots) % totalSlots;

            // Switch to the new index
            playScreen.hud.switchHighlightedSlot(newIndex);
        }

        return true;
    }
}
