package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class WorldRenderer {
	SpriteBatch batch;
	public OrthographicCamera camera;
	
	public WorldRenderer(World world) {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / world.level.getTileSize(), Gdx.graphics.getHeight() / world.level.getTileSize());
		camera.position.x = 8;
		camera.position.y = -8;
	}
	
	public void render(World world, float deltaTime) {
		world.level.render(camera);
	}
}
