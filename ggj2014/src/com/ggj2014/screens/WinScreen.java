package com.ggj2014.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.ggj2014.Screen;
import com.ggj2014.ScreenManager;

public class WinScreen extends Screen {

	public WinScreen(ScreenManager manager) {
		super(manager);
	}

	@Override
	public void render() {
		// TODO: render beautiful game over graphics :D

		Gdx.gl20.glClearColor(0.4f, 0.4f, 1, 1);
		Gdx.gl20.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);
		
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}

	@Override
	public void dispose() {
	}

}
