package com.ggj2014.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.ggj2014.AudioManager;
import com.ggj2014.Screen;
import com.ggj2014.ScreenManager;
import com.ggj2014.mechanic.World;
import com.ggj2014.mechanic.WorldRenderer;

public class GameplayScreen extends Screen {
	World world;
	WorldRenderer renderer;
	AudioManager audio;
	
	public GameplayScreen (ScreenManager manager) {
		super(manager);
		
		world = new World("levels/map1_v2.tmx");
		renderer = new WorldRenderer(world);
		audio = new AudioManager();
		world.setRenderer(renderer);
		world.setAudio(audio);
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		renderer.render(delta);
		world.update(delta);
		
		if(world.player.isDead()) {
			manager.setScreen(new GameOverScreen(manager));
		} else if(world.player.isWin()) {
			manager.setScreen(new WinScreen(manager));
		}
		
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}

	@Override
	public void dispose () {
		renderer.dispose();
		audio.dispose();
		ScreenManager.multiplexer.clear();
	}
}
