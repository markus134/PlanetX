package com.mygdx.game;

import Screens.PlayScreen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
public class MyGDXGame extends Game {
	public SpriteBatch batch;
	public final static int V_WIDTH = 1024;
	public final static int V_HEIGHT = 1024;
	@Override
	public void create () {
		batch = new SpriteBatch();
		setScreen(new PlayScreen(this));
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {

	}
}
