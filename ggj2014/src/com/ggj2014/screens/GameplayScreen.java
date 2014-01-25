package com.ggj2014.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.ggj2014.Screen;
import com.ggj2014.ScreenManager;
import com.ggj2014.mechanic.World;
import com.ggj2014.mechanic.WorldRenderer;

public class GameplayScreen extends Screen {
	World world;
	WorldRenderer renderer;
	
	public GameplayScreen (ScreenManager manager) {
		super(manager);
		
		world = new World("levels/map1.tmx");
		renderer = new WorldRenderer(world);
		world.setRenderer(renderer);
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		renderer.render(delta);
		world.update(delta);
		
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			Gdx.app.exit();
		}
	}

	@Override
	public void dispose () {
	}
}
