package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;

public class WorldRenderer {
	SpriteBatch batch;
	public OrthographicCamera camera;
	
	ShapeRenderer sr = new ShapeRenderer();
	
	public WorldRenderer(World world) {
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth() / world.level.getTileSize(), Gdx.graphics.getHeight() / world.level.getTileSize());
		camera.position.x = 8;
		camera.position.y = 8;
	}
	
	public void render(World world, float deltaTime) {
		world.level.render(camera);
		
		if(false) {
			Color c = new Color(1, 0, 0, 1);
			
			sr.setProjectionMatrix(camera.combined);
			sr.begin(ShapeType.Filled);
			for(int x = 0; x < world.walls.length; x++) {
				for(int y = 0; y < world.walls[x].length; y++) {
					Rectangle r = world.walls[x][y];
					if(r != null) {
						sr.rect(r.x, r.y, r.width, r.height, c, c, c, c);
					}
				}
			}
			sr.end();
		}
	}
}
