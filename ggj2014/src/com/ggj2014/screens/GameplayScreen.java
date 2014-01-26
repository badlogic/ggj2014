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
	int lvl;
	
	public GameplayScreen (ScreenManager manager, int lvl) {
		super(manager);
		this.lvl = lvl;
		switch(lvl){
		case 1: world = new World("levels/map1_v2.tmx");
				break;
		case 2: world = new World("levels/map2_v1.tmx");
				break;
		}
		renderer = new WorldRenderer(world);
		audio = new AudioManager();
		world.setRenderer(renderer);
		world.setAudio(audio);
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		renderer.render(delta);
		audio.update(delta);
		world.update(delta);
		
		if(world.player.isDead()) {
			manager.setScreen(new GameOverScreen(manager));
		} else if(world.player.isWin()) {
			if(lvl==1)
			{
				manager.setScreen(new GameplayScreen(manager,2));
			}
			else{
				manager.setScreen(new WinScreen(manager));
			}
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
