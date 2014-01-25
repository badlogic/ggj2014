package com.ggj2014.screens;

import com.badlogic.gdx.Gdx;
import com.ggj2014.Screen;
import com.ggj2014.ScreenManager;
import com.ggj2014.mechanic.World;
import com.ggj2014.mechanic.WorldRenderer;

public class GameplayScreen extends Screen {
	World world;
	WorldRenderer renderer;
	
	public GameplayScreen (ScreenManager manager) {
		super(manager);
		
		world = new World("levels/testmap.tmx");
		renderer = new WorldRenderer(world);
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		world.update(delta);
		renderer.render(delta);
	}

	@Override
	public void dispose () {
	}
}
