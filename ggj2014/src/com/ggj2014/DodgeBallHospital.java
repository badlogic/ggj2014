package com.ggj2014;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.ggj2014.screens.GameplayScreen;

public class DodgeBallHospital implements ApplicationListener {	
	ScreenManager manager;
	
	@Override
	public void create () {
		manager = new ScreenManager();
		// SET START SCREEN HERE!
		Screen screen = new GameplayScreen(manager);
		manager.setScreen(screen);		
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		manager.render();
	}

	@Override
	public void pause () {
		manager.pause();
	}

	@Override
	public void resume () {
		manager.resume();
	}

	@Override
	public void dispose () {
		manager.dispose();
	}
}
