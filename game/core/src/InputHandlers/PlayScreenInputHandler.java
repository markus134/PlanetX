package InputHandlers;

import Bullets.Bullet;
import Items.Items;
import Opponents.Boss;
import Opponents.Monster;
import Opponents.Robot;
import Screens.PlayScreen;
import Sprites.OtherPlayer;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.mygdx.game.MyGDXGame;
import crystals.Crystal;
import serializableObjects.BulletData;
import serializableObjects.CrystalToRemove;
import serializableObjects.OpponentData;
import serializableObjects.RevivePlayer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static Screens.PlayScreen.opponentDataMap;
import static Screens.PlayScreen.opponentIds;
import static Screens.PlayScreen.opponents;

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
    private long revivingStartTime = 0;
    private Crystal closeCrystal;
    private OtherPlayer closeDeadPlayer;


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
        if (keyPresses > 0 && !playScreen.player.getIsMining() && !playScreen.player.isInShell() && !playScreen.player.getIsReviving()) {
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
                    case Input.Keys.N:
                        if (isFirstClick) {
                            generateBoss();
                            isFirstClick = false;
                        }
                        break;
                    case Input.Keys.M:
                        if (isFirstClick) {
                            generateMonster();
                            isFirstClick = false;
                        }
                        break;
                    case Input.Keys.R:
                        System.out.println("Clicked r, reviving: " + playScreen.player.getIsReviving());
                        if (!playScreen.player.getIsReviving() && isCloseToDeadPlayer(playScreen.player.getX(), playScreen.player.getY())) {
                            revivingStartTime = TimeUtils.nanoTime();
                            playScreen.player.setIsReviving(true);
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

        opponentIds.add(uniqueID);
        opponents.put(uniqueID, robot);
        opponentDataMap.put(uniqueID, new OpponentData(robot.getX(), robot.getY(), robot.getHealth(), robot.getUuid(), robot.getMobId()));

        MyGDXGame.client.sendTCP(opponentDataMap);
    }

    /**
     * Generates robots when the button is clicked.
     */
    private void generateBoss() {
        Boss boss = new Boss(playScreen.world, playScreen);
        String uniqueID = UUID.randomUUID().toString();
        boss.setUuid(uniqueID);

        opponentIds.add(uniqueID);
        opponents.put(uniqueID, boss);
        opponentDataMap.put(uniqueID, new OpponentData(boss.getX(), boss.getY(), boss.getHealth(), boss.getUuid(), boss.getMobId()));

        MyGDXGame.client.sendTCP(opponentDataMap);
    }

    /**
     * Generates robots when the button is clicked.
     */
    private void generateMonster() {
        Monster monster = new Monster(playScreen.world, playScreen);
        String uniqueID = UUID.randomUUID().toString();
        monster.setUuid(uniqueID);

        opponentIds.add(uniqueID);
        opponents.put(uniqueID, monster);
        opponentDataMap.put(uniqueID, new OpponentData(monster.getX(), monster.getY(), monster.getHealth(), monster.getUuid(), monster.getMobId()));

        MyGDXGame.client.sendTCP(opponentDataMap);
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

        if (keycode == Input.Keys.ESCAPE) {
            playScreen.pauseDialog.showStage();
            keysPressed.remove(Input.Keys.ESCAPE);

        }

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

        if (keycode == Input.Keys.B || keycode == Input.Keys.N || keycode == Input.Keys.M) {
            isFirstClick = true;
        }

        // Stop reviving animation when R key is released
        if (keycode == Input.Keys.R && isCloseToDeadPlayer(playScreen.player.getX(), playScreen.player.getY())) {
            playScreen.player.setIsReviving(false);

            long revivingDuration = TimeUtils.timeSinceNanos(revivingStartTime);
            if (revivingDuration >= 1_000_000_000L) {
                MyGDXGame.client.sendTCP(new RevivePlayer(closeDeadPlayer.getUuid()));
                closeDeadPlayer.setIsDead(false);
                closeDeadPlayer.setIsFirstDeath(false);
            }

            revivingStartTime = 0;
        } else if (keycode == Input.Keys.R) {
            playScreen.player.setIsReviving(false);
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
        if (button == Input.Buttons.LEFT && !playScreen.player.isInShell() && !playScreen.player.getIsReviving()) {
            Items selectedItem = playScreen.hud.getHighlightedItem();

            if (TimeUtils.timeSinceNanos(lastShotTime) >= SHOOT_COOLDOWN_NANOS && selectedItem.equals(Items.BLASTER)) {
                shootBullet(touchPoint, screenX, screenY);
            } else if (selectedItem.equals(Items.DRILL)) {
                if (isCloseToCrystal(playScreen.player.getX(), playScreen.player.getY())) {
                    miningStartTime = TimeUtils.nanoTime();
//                    System.out.println("close to crystal");
                }
                playScreen.player.setIsMining(true);
            } else if (selectedItem.equals(Items.CRYSTAL)) {
                playScreen.player.recoverHealth(20);
                playScreen.hud.removeHighlightedItem();
            }
        } else if (button == Input.Buttons.RIGHT) {
//            System.out.println("right click");
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
                opponentDataMap.getWorldUUID()
        ));
    }

    /**
     * Check if player is close to any crystals.
     * @param playerX
     * @param playerY
     * @return true if player is close, otherwise false
     */
    private boolean isCloseToCrystal(float playerX, float playerY) {
        float miningRange = 0.7f;

        // Convert player's position to Box2D coordinates
        Vector2 playerPos = new Vector2(playerX, playerY);

        for (Crystal crystal : PlayScreen.crystals) {
            // Get crystal position directly in Box2D coordinates
            Vector2 crystalPos = new Vector2(crystal.getX() / MyGDXGame.PPM, crystal.getY() / MyGDXGame.PPM);

            // Calculate the distance between the player and the crystal
            float distance = playerPos.dst(crystalPos);
            if (distance <= miningRange) {
                closeCrystal = crystal;
                return true;
            }
        }
        return false;
    }

    /**
     * Check if player is close to any dead player. Dead in this case means that they are in a shell.
     * @param playerX
     * @param playerY
     * @return true if player is close, otherwise false
     */
    private boolean isCloseToDeadPlayer(float playerX, float playerY) {
        float deadPlayerRange = 0.7f;

        // Convert player's position to Box2D coordinates
        Vector2 playerPos = new Vector2(playerX, playerY);

        for (Map.Entry<Integer, Set<OtherPlayer>> entry : MyGDXGame.playerDict.entrySet()) {
            for (OtherPlayer otherPlayer : entry.getValue()) {
                if (!otherPlayer.isInShell()) continue;

                // Get other player position directly in Box2D coordinates
                Vector2 otherPlayerPos = new Vector2(otherPlayer.b2body.getPosition().x - otherPlayer.getWidth() / 2,
                        otherPlayer.b2body.getPosition().y - otherPlayer.getHeight() / 2);

                // Calculate the distance between the player and the other player
                float distance = playerPos.dst(otherPlayerPos);

                if (distance <= deadPlayerRange) {
                    closeDeadPlayer = otherPlayer;
                    playScreen.player.setIsReviving(true);
                    return true;
                }
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

                        MyGDXGame.client.sendTCP(new CrystalToRemove(closeCrystal.getId()));
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
