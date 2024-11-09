## Game Description

A 2D Multiplayer RPG Roguelike game where you have to explore an unknown world, fight robots, aliens and monster and find your way home.

## Current state of the game

When started a menu appears. There you have multiple options:

1. Singleplayer
2. Multiplayer
3. Settings
4. Exit

SinglePlayer brings you to a new screen. There you can create and remove worlds. To create a new one, press the "Create New" button at top right of the screen. Then enter the world name and press "Save". In case you want to remove the world, you can press the "Remove" button. To enter a world, select it in the menu - it will be highlighted with purple - and press "Connect".

Multiplayer is very similar to the SinglePlayer screen. There you follow the same steps. At one point you are prompted and need to choose what to do next. You can either join an existing world or create a new one. Let's create a new one! There you enter the name of the world and the amount of players. After pressing "Save" a screen with a ton of text appears. Read it. Now you have the code of the world in your ClipBoard. Send it to your friends to play together. They will be able to join you using that code. In case you created the world long ago and do not have the code in your clipboard anymore, in the menu, where your multiPlayer worlds are listed, you can select the world and press the "C" button which stands for copy. It will again copy the code of the world to your ClipBoard. Pressing "R" removes the world from the list. Keep in mind that when creating a world, there is a fixed amount of players that can join the world simultaneously. That means that if the world is made for 2 players and there are 2 players currently playing and some third player tries to join, they will be prompted with an error screen saying that they cannot join at the moment.

Pressing Settings navigates you to the settings screen where you can change the volume of the sound effects and music.

Exit - exits the game.

When you enter a world, you can see your character in the middle of the map. Use A, S, W, D to move. You can also see the inventory bar at the bottom of the screen. There you have 2 items right from the start. The first one, which is the one automatically selected at the beginning, is a blaster. You can shoot using mouse. Also if you use mouse wheel or key from 1 to 8, you can change the selected inventory slot. The second item is a drill, it is used to mine crystals. You can see them on the map - blue ones. To mine one, select the drill, and hold mouse right click for ± 1 second. The crystal should be removed from the map and occur in you inventory. Crystals are used to restore hp. If your hp is not full, select a crystal and press mouse button to consume one. Now you should have +1 heart.

In the game there are mobs. They start spawning as soon as you enter the world. There are 3 types of mobs:

1. Robots - small and sneaky, they drive towards you. They do not deal damage on contact, but do, in fact, make it harder for you to move if you allow them to swarm you. When destroyed they blow up. The explosion deals damage to all nearby players.
2. Monsters - they fight in melee. They deal damage when they jump on you. When killed a death animation is played.
3. Boss - not very mobile, but deals a ton of damage. If they get close to you, you are in trouble. They are big, that's why walls make it hard for them to move freely, but do not worry, we gave them a special ability - they can teleport to the closest player once in a while.

If you happen to lose all your hp, it is not over yet. Your spacesuit will protect you! You will stay in a capsule created by your spacesuit until a friend you are playing with revives you. To revive a player in a capsule, come close to them and hold down the "R" key. After a second, they will be back and ready to fight the mobs! But be careful, after someone gets revived their hp is not max. This means they might need to mine and consume some crystals to be out of danger. If a player happens to lose all hp for the second time, it is officially over for them. A scene will appear saying that the game is over. Pressing the button on the screen will return you to the main menu.

The idea of the game is to survive 5 waves of mobs. Each new wave is harder than the previous one. During the latter waves the probability of boss spawning is higher, so good luck trying to win.

In case you want to leave the game before you lose all your hp, press "Escape"/"ESC" and then choose "Yes". If you leave the game before comple it and then rejoin, the game will continue from the place where you ended.

NB! Players can deal damage to each other with their blasters. Be careful.

NB! If you do not want to wait for the mobs to spawn, you can press "B", "N", "M" buttons to spawn some near your spawning point.

## Getting started

To play the game on the university server:

### 1. Make sure that the game client connects to the server:

-   Navigate to the game/core/src/com/mygdx/game/MyGDXGame.java file.
-   Make sure that at the top of the file private static final String SERVER_ADDRESS = "193.40.255.19"; is uncommented and the line with "localhost" is commented.

### 2. Run Game Client:

-   Open gradle menu.
-   Open game/Tasks/build/build and double-click build.
-   Open game/Tasks/other/run and double-click run.
-   Feel free to run it multiple times to experience the game with multiple players.

To play the game on the localhost server:

### 1. Make sure that the game client connects to the server:

-   Navigate to the game/core/src/com/mygdx/game/MyGDXGame.java file.
-   Make sure that at the top of the file private static final String SERVER_ADDRESS = "193.40.255.19"; is commented and the line with "localhost" is uncommented.

### 2. Start Kryonet Server:

-   Navigate to the project directory.
-   Open the Kryonet server located here :`server/src/main/java/ee/taltech/game/server/GameServer.java`.
-   Start the server by running the file.

### 3. Run Game Client:

-   Open gradle menu.
-   Open game/Tasks/build/build and double-click build.
-   Open game/Tasks/other/run and double-click run.
-   Feel free to run it multiple times to experience the game with multiple players.
