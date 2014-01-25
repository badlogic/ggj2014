package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class WorldRenderer {	
	private static final float CAM_DAMP = 4;
	World world;
	SpriteBatch batch;
	public OrthographicCamera camera;
	OrthogonalTiledMapRenderer tileMapRenderer;
	
	ShapeRenderer sr = new ShapeRenderer();
	public Texture[] patient1 = new Texture[2];
	public Texture[] patient2 = new Texture[2];
	
	public WorldRenderer(World world) {
		this.world = world;
		loadAssets();
		tileMapRenderer = new OrthogonalTiledMapRenderer(world.map, 1f / World.TILE_SIZE);		
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth() / World.TILE_SIZE, Gdx.graphics.getHeight() / World.TILE_SIZE);
	}
	
	private void loadAssets () {
		loadImage(patient1, "patient1");
		loadImage(patient2, "patient2");
	}
	
	private void loadImage(Texture[] images, String path) {
		images[World.REAL] = new Texture(Gdx.files.internal("graphics/" + path + "-real.png"));
		images[World.GHOST] = new Texture(Gdx.files.internal("graphics/" + path + "-geist.png"));
		for(int i = 0; i < images.length; i++) {
			images[i].setFilter(TextureFilter.Linear, TextureFilter.Linear);
		}
	}

	public void render(float deltaTime) {
		cameraFollow(deltaTime);
		camera.update();
		
		// render tiles
		tileMapRenderer.setView(camera);
		tileMapRenderer.render();

		// render collision layer
		if(true) {
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
		
		// render objects
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		for(Entity entity: world.entities) {
			if(entity instanceof Player) {
				batch.draw(patient1[world.mode], entity.position.x, entity.position.y, 1, 2);
			}
		}
		batch.end();
		
		// draw entity bounds
		sr.begin(ShapeType.Line);
		sr.setColor(0, 1, 0, 1);
		for(Entity entity: world.entities) {
			if(entity instanceof Player) {
				sr.rect(entity.bounds.x, entity.bounds.y, entity.bounds.width, entity.bounds.height);
			}
		}
		sr.end();
	}
	
	private void cameraFollow (float deltaTime) {
		Vector2 dist = new Vector2(world.player.position).sub(camera.position.x, camera.position.y);
		camera.position.add(dist.x * deltaTime * CAM_DAMP, dist.y * deltaTime * CAM_DAMP, 0);
	}
}
