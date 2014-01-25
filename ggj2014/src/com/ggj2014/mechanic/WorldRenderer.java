package com.ggj2014.mechanic;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ggj2014.mechanic.Player.Heading;

public class WorldRenderer {	
	private static final float CAM_DAMP = 4;
	private int LAYER_FLOOR = 0;
	private int LAYER_FLOOR_UPPER = 1;
	private int LAYER_INTERIEUR = 3;
	
	World world;
	SpriteBatch batch;
	public OrthographicCamera camera;
	OrthogonalTiledMapRenderer tileMapRenderer;
	Array<Entity> sortedEntities = new Array<Entity>();
	ShaderProgram vignetteShader;
	
	ShapeRenderer sr = new ShapeRenderer();
	
	public Animation mainIdle;
	public Animation mainAxeIdle;
	public Animation mainAttack;
	
	public Texture[] patient1 = new Texture[2];
	public Texture[] patient2 = new Texture[2];
	
	public Texture doorClosed;
	public Texture doorOpen;
	public Texture doorVertical;
	public Texture pill;
	
	public WorldRenderer(World world) {
		this.world = world;
		loadAssets();
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, Gdx.graphics.getWidth() / (float)World.TILE_SIZE, Gdx.graphics.getHeight() / (float)World.TILE_SIZE);				
		tileMapRenderer = new OrthogonalTiledMapRenderer(world.map, 1f / World.TILE_SIZE, batch);
		
		// vignette shader
		ShaderProgram.pedantic = false;
		vignetteShader = new ShaderProgram(Gdx.files.internal("graphics/vignette.vsh"), Gdx.files.internal("graphics/vignette.fsh"));
		if(!vignetteShader.isCompiled())
			System.out.println(vignetteShader.getLog());
		batch.setShader(vignetteShader);
		
		// figure out which layer has which id, idiotic
		for(int i = 0; i < world.map.getLayers().getCount(); i++) {
			MapLayer layer = world.map.getLayers().get(i);
			if(layer.getName().equals("floor")) LAYER_FLOOR = i;
			if(layer.getName().equals("floor_upper")) LAYER_FLOOR_UPPER = i;
			if(layer.getName().equals("interieur")) LAYER_INTERIEUR = i;
		}
	}
	
	private void loadAssets () {
		// main
		mainIdle = loadAnimation("graphics/animations/main-normal-idle", 2, 0.5f);
		mainAxeIdle = loadAnimation("graphics/animations/main-axe-idle", 2, 0.5f);
		mainAttack = loadAnimation("graphics/animations/main-char-axe", 2, Player.ATTACK_TIME / 2);
		
		// images & animations
		loadImage(patient1, "patient1");
		loadImage(patient2, "patient2");
		
		doorOpen = new Texture(Gdx.files.internal("graphics/door-open.png"));
		doorClosed = new Texture(Gdx.files.internal("graphics/door-closed.png"));
		doorVertical = new Texture(Gdx.files.internal("graphics/door-vertical.png"));
		pill = new Texture(Gdx.files.internal("graphics/tablette.png"));
	}
	
	private Animation loadAnimation(String path, int frames, float frameDuration) {
		TextureRegion[] regions = new TextureRegion[frames];
		for(int i = 1; i <= frames; i++) {
			Texture tex = new Texture(Gdx.files.internal(path + i + ".png"));
			regions[i-1] = new TextureRegion(tex);
		}
		return new Animation(frameDuration, regions);
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
		tileMapRenderer.render(new int[] { LAYER_FLOOR });

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
		
		// render interieur
		tileMapRenderer.render(new int[] { LAYER_INTERIEUR });
		
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
				renderPlayer((Player)entity);
			}
			else if(entity instanceof Enemy && !(entity instanceof Enemy2)) {
				batch.draw(patient1[world.getMode()], entity.position.x, entity.position.y, 1, 2);
			}
			else if(entity instanceof Enemy2) {
				batch.draw(patient2[world.getMode()], entity.position.x, entity.position.y, 1, 1);
			}
			else if(entity instanceof Pill) {
				batch.draw(pill, entity.position.x, entity.position.y, 1, 1);
			}
			else if(entity instanceof DoorVertical) {
				Door door = (Door)entity;
				if(!door.isOpened) {
					batch.draw(doorVertical, entity.position.x, entity.position.y, 1, 1);
				}
			}
			else if(entity instanceof Door) {				
				Door door = (Door)entity;
				if(door.isOpened) {
					batch.draw(doorOpen, entity.position.x, entity.position.y, 1, 2);
				} else {
					batch.draw(doorClosed, entity.position.x, entity.position.y, 1, 2);
				}
			}
		}
		batch.end();
		
		tileMapRenderer.render(new int[] { LAYER_FLOOR_UPPER });
		
		// draw entity bounds
//		sr.begin(ShapeType.Line);
//		sr.setColor(0, 1, 0, 1);
//		for(Entity entity: world.entities) {			
//			sr.rect(entity.bounds.x, entity.bounds.y, entity.bounds.width, entity.bounds.height);	
//		}
//		sr.end();
	}
	
	private void renderPlayer (Player entity) {
//		System.out.println(entity.state + ", " + entity.stateTime);
		TextureRegion frame;
		switch(entity.state) {
			case IDLE:
				if(entity.heading == Heading.Left) {					
					frame = mainIdle.getKeyFrame(entity.stateTime, true);
					frame.flip(true, false);
					batch.draw(frame, entity.position.x, entity.position.y, 1, 2);
					frame.flip(true, false);
				} else {
					frame = mainIdle.getKeyFrame(entity.stateTime, true);
					batch.draw(frame, entity.position.x, entity.position.y, 1, 2);
				}
				break;
			case MOVING:
				if(entity.heading == Heading.Left) {					
					frame = mainIdle.getKeyFrame(entity.stateTime, true);
					frame.flip(true, false);
					batch.draw(frame, entity.position.x, entity.position.y, 1, 2);
					frame.flip(true, false);
				} else {
					frame = mainIdle.getKeyFrame(entity.stateTime, true);
					batch.draw(frame, entity.position.x, entity.position.y, 1, 2);
				}
				break;
			case ATTACK:
				if(entity.heading == Heading.Left) {					
					frame = mainAttack.getKeyFrame(entity.stateTime, false);
					frame.flip(true, false);
					batch.draw(frame, entity.position.x - 0.5f, entity.position.y, 2, 2);
					frame.flip(true, false);
				} else {
					frame = mainAttack.getKeyFrame(entity.stateTime, false);
					batch.draw(frame, entity.position.x - 0.5f, entity.position.y, 2, 2);
				}
				break;
			default:
		}
	}

	private void cameraFollow (float deltaTime) {
		Vector2 dist = new Vector2(world.player.position).sub(camera.position.x, camera.position.y);
		camera.position.add(dist.x * deltaTime * CAM_DAMP, dist.y * deltaTime * CAM_DAMP, 0);
	}
}
