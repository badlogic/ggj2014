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
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		world.update(delta);
		renderer.render(world, delta);
	}

	@Override
	public void dispose () {
	}
}
