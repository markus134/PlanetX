package com.mygdx.game;

import Screens.PlayScreen;
import Sprites.Player;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyGDXGame extends Game {
	public SpriteBatch batch;
	public final static int V_WIDTH = 1920;
	public final static int V_HEIGHT = 1080;
	public final static float PPM = 100;

	public static Client client;

	private Object lastReceivedData;
	private Map<Integer, SpriteBatch> spriteDict = new HashMap<>();

	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));

		client = new Client();
		client.start();
		client.sendTCP("Start");
        try {
            client.connect(5000, "localHost", 8080, 8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
		client.addListener(new Listener.ThreadedListener(new Listener() {
			@Override
			public void received(Connection connection, Object object){
				if (!(object instanceof FrameworkMessage.KeepAlive)) {
					System.out.println("received: " + object);
					lastReceivedData = object;
				}
			}
		}));
    }

	@Override
	public void render () {
		super.render();
		if (lastReceivedData != null){
			String[] playersData = lastReceivedData.toString().split("/");
//			System.out.println(Arrays.toString(playersData));

			for (String playerData : playersData) {
				String[] parts = playerData.split(":");
//				System.out.println(Arrays.toString(parts));
				int id = Integer.parseInt(parts[0]);

				if (!spriteDict.containsKey(id)){
					spriteDict.put(id, new SpriteBatch());
				}
				// id:x,y|id:x,y
				if (id != client.getID()){
					String[] coordinates = parts[1].split(",");
					spriteDict.get(id).begin();
					spriteDict.get(id).draw(new Texture("testplayer.png"),
							17 * Float.parseFloat(coordinates[0]) + (float) V_WIDTH / 6,
							17 * Float.parseFloat(coordinates[1]) + (float) V_HEIGHT / 5);
					spriteDict.get(id).end();
				}
			}
		}
//		System.out.println(spriteDict);
		lastReceivedData = null;
	}

	@Override
	public void dispose () {
		client.close();

        try {
            client.dispose();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
