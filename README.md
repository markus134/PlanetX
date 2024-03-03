# iti0301-2024-meeskond-suva

## Game Description

A 2D Multiplayer RPG Roguelike game where you have to explore an unknown world, fight robots and aliens and find your way home.

For a more in-depth description, check out our project wiki : https://gitlab.cs.taltech.ee/ppavli/iti0301-2024-meeskond-suva/-/wikis/Projektiplaan

## Current state of the game

At the moment, the project is not finished. When started a menu appears. There you have multiple options: 
1. Singleplayer
2. Multiplayer
3. Settings
4. Exit

The first 2 are identical at the moment.
Pressing Settings navigates you to the settings screen where you can change the volume of the sound effects and music.
Exit - exits the game.

When a game session is opened (after pressing either Singleplayer or Multiplayer) you see a map with your in-game character in the middle. If multiple instances of the game are opened, you should see all different players on the screen. If one is moving, this should be visible to the others. If you click on the screen using mouse or touchpad, a bullet is shot. Hitting another player 5 times kills that player. They are returned to the main menu. Besides players, robots are also added to the game. To spawn one, you need to press button "B". Robots can also be killed. The rules are the same as with the players.

## Getting started

To get started, follow these steps:

### 1. Start Kryonet Server:

   - Navigate to the project directory.
   - Open the Kryonet server located here :`server/src/main/java/ee/taltech/game/server/GameServer.java`.
   - Start the server by running the file. 

### 2. Run Game Client:

   - Open gradle menu.
   - Open game/Tasks/build/build and double-click build.
   - Open game/Tasks/other/run and double-click run. 
   - Feel free to run it multiple times to experience the game with multiple players.

**Note:** Currently it doesn't matter which mode you pick at the menu, they will both be multiplayer.

You can also refer to the video here if you have any troubles starting server or client: https://gitlab.cs.taltech.ee/ppavli/iti0301-2024-meeskond-suva/-/wikis/1.-sprindi-video

