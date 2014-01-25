package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldRenderer {
	SpriteBatch batch;
	public OrthographicCamera camera;
	
	public WorldRenderer() {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / 32, Gdx.graphics.getHeight() / 32);
	}
	
	public void render(World world, float deltaTime) {
		
	}
}
