package com.ggj2014.mechanic;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class WorldRenderer {	
	private static final float CAM_DAMP = 4;
	World world;
	SpriteBatch batch;
	public OrthographicCamera camera;
	OrthogonalTiledMapRenderer tileMapRenderer;
	Array<Entity> sortedEntities = new Array<Entity>();
	ShaderProgram vignetteShader;
	
	ShapeRenderer sr = new ShapeRenderer();
	public Texture[] patient1 = new Texture[2];
	public Texture[] patient2 = new Texture[2];
	
	public WorldRenderer(World world) {
		this.world = world;
		loadAssets();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth() / World.TILE_SIZE, Gdx.graphics.getHeight() / World.TILE_SIZE);		
		ShaderProgram.pedantic = false;
		vignetteShader = new ShaderProgram(Gdx.files.internal("graphics/vignette.vsh"), Gdx.files.internal("graphics/vignette.fsh"));
		if(!vignetteShader.isCompiled())
			System.out.println(vignetteShader.getLog());
		batch.setShader(vignetteShader);
		
		tileMapRenderer = new OrthogonalTiledMapRenderer(world.map, 1f / World.TILE_SIZE, batch);		
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
		// set vignette based on
		vignetteShader.begin();
		vignetteShader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		if(world.getMode() == World.REAL) {
			vignetteShader.setUniformf("tint", 1, 1, 1, 1);
			vignetteShader.setUniformf("innerRadius", 0.02f);
			vignetteShader.setUniformf("outerRadius", 0.9f);
			vignetteShader.setUniformf("intensity", 0.99f);
		} else {
			vignetteShader.setUniformf("tint", 1, 0.7f, 0.7f, 1);
			vignetteShader.setUniformf("innerRadius", 0.02f);
			vignetteShader.setUniformf("outerRadius", 0.3f);
			vignetteShader.setUniformf("intensity", 0.99f);		
		}
		vignetteShader.end();
		
		cameraFollow(deltaTime);
		camera.update();
		
		// render tiles
		tileMapRenderer.setView(camera);
		tileMapRenderer.render();

		// render collision layer
		sr.setProjectionMatrix(camera.combined);
		if(false) {
			Color c = new Color(1, 0, 0, 1);
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
		sortedEntities.clear();
		sortedEntities.addAll(world.entities);
		sortedEntities.sort(new Comparator<Entity>() {
			@Override
			public int compare (Entity o1, Entity o2) {
				return (int)Math.signum(o2.position.y - o1.position.y);
			}
		});
		
		for(Entity entity: sortedEntities) {
			if(entity instanceof Player) {
				batch.draw(patient1[World.REAL], entity.position.x, entity.position.y, 1, 2);
			}
			else if(entity instanceof Enemy) {
				batch.draw(patient1[world.getMode()], entity.position.x, entity.position.y, 1, 2);
			}
			else if(entity instanceof Enemy2) {
				batch.draw(patient2[world.getMode()], entity.position.x, entity.position.y, 1, 1);
			}
			else if(entity instanceof Pill) {
				
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
