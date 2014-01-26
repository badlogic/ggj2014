package com.ggj2014.mechanic;

import java.util.Comparator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.ggj2014.mechanic.Enemy.State;
import com.ggj2014.mechanic.Player.Heading;

public class WorldRenderer {	
	private static final float MODE_TRANSITION_TIME = 1.0f;
	
	private static final float CAM_DAMP = 4;
	private static final int CULL_RADIUS = 10;
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
	public Texture mainDead;
	
	public Animation[] patient1Idle = new Animation[2];
	public Animation[] patient2Idle = new Animation[2];
	public Animation poof;
	
	public Texture doorClosed;
	public Texture doorOpen;
	public Texture doorVertical;
	public Texture pill;
	public Texture axe;
	public Texture blood;
	public Texture switchOn;
	public Texture switchOff;
	
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
	
	public void dispose() {
		batch.dispose();
		tileMapRenderer.dispose();
		vignetteShader.dispose();
		world.map.dispose();
		disposeAnim(mainIdle);
		disposeAnim(mainAxeIdle);
		disposeAnim(mainAttack);
		mainDead.dispose();
		disposeAnim(patient1Idle[0]);
		disposeAnim(patient1Idle[1]);
		disposeAnim(patient2Idle[0]);
		disposeAnim(patient2Idle[1]);
		doorClosed.dispose();
		doorOpen.dispose();
		doorVertical.dispose();
		pill.dispose();
		axe.dispose();
		blood.dispose();
		switchOn.dispose();
		switchOff.dispose();
		sr.dispose();
	}
	
	private void disposeAnim (Animation mainIdle2) {
		for(TextureRegion region: mainIdle.getKeyFrames()) {
			region.getTexture().dispose();
		}
	}

	private void loadAssets () {
		// main
		mainIdle = loadAnimation("graphics/animations/main-normal-idle", 2, 0.5f);
		mainAxeIdle = loadAnimation("graphics/animations/main-axe-idle", 2, 0.5f);
		mainAttack = loadAnimation("graphics/animations/main-char-axe", 2, Player.ATTACK_TIME / 2);
		mainDead = new Texture("graphics/animations/main-dead.png");
		
		// patient1
		patient1Idle[World.modeToInt(World.Mode.GHOST)] = loadAnimation("graphics/animations/patient1-ghost-idle", 2, 0.5f);
		patient1Idle[World.modeToInt(World.Mode.REAL)] = loadAnimation("graphics/animations/patient1-real-idle", 2, 0.5f);
		
		// patient2
		patient2Idle[World.modeToInt(World.Mode.GHOST)] = loadAnimation("graphics/animations/patient2-ghost-idle", 2, 0.5f);
		patient2Idle[World.modeToInt(World.Mode.REAL)] = loadAnimation("graphics/animations/patient2-real-idle", 2, 0.5f);
		
		// poof
		poof = loadAnimation("graphics/animations/poof-", 2, 0.3f);		
		
		// statics
		doorOpen = new Texture(Gdx.files.internal("graphics/door-open.png"));
		doorClosed = new Texture(Gdx.files.internal("graphics/door-closed.png"));
		doorVertical = new Texture(Gdx.files.internal("graphics/door-vertical.png"));
		pill = new Texture(Gdx.files.internal("graphics/tablette.png"));
		axe = new Texture(Gdx.files.internal("graphics/axe.png"));
		blood = new Texture(Gdx.files.internal("graphics/blood.png"));
		switchOn = new Texture(Gdx.files.internal("graphics/switch-on.png"));
		switchOff = new Texture(Gdx.files.internal("graphics/switch-off.png"));
	}
	
	private Animation loadAnimation(String path, int frames, float frameDuration) {
		TextureRegion[] regions = new TextureRegion[frames];
		for(int i = 1; i <= frames; i++) {
			Texture tex = new Texture(Gdx.files.internal(path + i + ".png"));
			regions[i-1] = new TextureRegion(tex);
		}
		return new Animation(frameDuration, regions);
	}	

	public void render(float deltaTime) {
		// set vignette based on
		vignetteShader.begin();
		vignetteShader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		
		float transition = 0.0f;
		
		if(world.getMode() == World.Mode.REAL) {
			if(world.modeTime < MODE_TRANSITION_TIME)
				transition = world.modeTime / MODE_TRANSITION_TIME;
			else if(world.modeTime >= World.REAL_TIME - MODE_TRANSITION_TIME)
				transition = (World.REAL_TIME - world.modeTime) / MODE_TRANSITION_TIME;
			else
				transition = 1.0f;
		}
		
		vignetteShader.setUniformf("tint", 1, 0.7f + transition * 0.3f, 0.7f + transition * 0.3f, 1);
		vignetteShader.setUniformf("innerRadius", 0.02f);
		vignetteShader.setUniformf("outerRadius", 0.4f + transition * 0.5f);
		vignetteShader.setUniformf("intensity", 0.99f);
		vignetteShader.setUniformf("timer", world.modeTime);
		vignetteShader.setUniformf("noise", 1 - transition);
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
		sortedEntities.clear();
		sortedEntities.addAll(world.entities);
		sortedEntities.sort(new Comparator<Entity>() {
			@Override
			public int compare (Entity o1, Entity o2) {							
				return (int)Math.signum(o2.position.y - o1.position.y);
			}
		});
		
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		// blood at the bottom...
		for(Entity entity: sortedEntities) {
			if(entity.position.dst(camera.position.x, camera.position.y) > CULL_RADIUS) continue;
			if(!entity.isVisible) continue;
			if(entity instanceof Enemy && ((Enemy)entity).state == State.DEAD) {
				renderEnemy((Enemy)entity, false);
			}
		}
		
		for(Entity entity: sortedEntities) {
			if(entity.position.dst(camera.position.x, camera.position.y) > CULL_RADIUS) continue;
			if(!entity.isVisible) continue;
			if(entity instanceof Player) {
				renderPlayer((Player)entity, false);
			}
			else if(entity instanceof Enemy) {
				if(((Enemy)entity).state == State.DEAD) continue;
				renderEnemy((Enemy)entity, false);
			}
			else if(entity instanceof Pill) {
				if(world.mode == World.Mode.GHOST) batch.draw(pill, entity.position.x, entity.position.y, 1, 1);
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
					TextureRegion frame = new TextureRegion(doorOpen);
					frame = clip(frame, false);
					batch.draw(frame, entity.position.x, entity.position.y, 1, 1);
				} else {
					TextureRegion frame = new TextureRegion(doorClosed);
					frame = clip(frame, false);
					batch.draw(frame, entity.position.x, entity.position.y, 1, 1);
				}
			}
			else if(entity instanceof Axe) {
				if(world.mode == World.Mode.REAL) batch.draw(axe, entity.position.x, entity.position.y, 1, 1);
			}
			else if(entity instanceof Switch) {
				Switch trigger = (Switch)entity;
				TextureRegion frame = null;
				if(trigger.isUsed) {
					frame = new TextureRegion(switchOn);
				} else {
					frame = new TextureRegion(switchOff);
				}
				frame = clip(frame, false);
				batch.draw(frame, trigger.position.x, trigger.position.y, 1, 1);
			}
		}
		batch.end();
		
		tileMapRenderer.render(new int[] { LAYER_FLOOR_UPPER });
		

		// upper parts of player, entities and doors
		batch.begin();
		for(Entity entity: sortedEntities) {
			if(entity.position.dst(camera.position.x, camera.position.y) > CULL_RADIUS) continue;
			if(!entity.isVisible) continue;
			if(entity instanceof Player) {
				renderPlayer((Player)entity, true);
			}
			else if(entity instanceof Enemy) {
				if(((Enemy)entity).state != Enemy.State.DEAD && !(entity instanceof Enemy2)) {
					renderEnemy((Enemy)entity, true);
				}
			}	
			else if(entity instanceof Door) {				
				Door door = (Door)entity;
				if(door instanceof DoorVertical) continue;
				if(door.isOpened) {
					TextureRegion frame = new TextureRegion(doorOpen);
					frame = clip(frame, true);
					batch.draw(frame, entity.position.x, entity.position.y + 1, 1, 1);
				} else {
					TextureRegion frame = new TextureRegion(doorClosed);
					frame = clip(frame, true);
					batch.draw(frame, entity.position.x, entity.position.y + 1, 1, 1);
				}
			}
			else if(entity instanceof Switch) {
				Switch trigger = (Switch)entity;
				TextureRegion frame = null;
				if(trigger.isUsed) {
					frame = new TextureRegion(switchOn);
				} else {
					frame = new TextureRegion(switchOff);
				}
				frame = clip(frame, true);
				batch.draw(frame, trigger.position.x, trigger.position.y + 1, 1, 1);
			}
		}
		batch.end();
		
		// draw entity bounds
//		sr.begin(ShapeType.Line);
//		sr.setColor(0, 1, 0, 1);
//		for(Entity entity: world.entities) {			
//			sr.rect(entity.bounds.x, entity.bounds.y, entity.bounds.width, entity.bounds.height);	
//		}
//		sr.end();
	}

	private void renderEnemy (Enemy entity, boolean upper) {
		Animation[] anims = entity instanceof Enemy2?patient2Idle: patient1Idle;
		TextureRegion frame;
		float offset = 0;
		
		int mode = World.modeToInt(world.mode);
		
		switch(entity.state) {
			case IDLE:
				if(entity.heading == Enemy.Heading.Left) {					
					frame = anims[mode].getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					frame.flip(true, false);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				} else {
					frame = anims[mode].getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				}
				break;
			case WANDERING:
				if(entity.heading == Enemy.Heading.Left) {					
					frame = anims[mode].getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					frame.flip(true, false);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				} else {
					frame = anims[mode].getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				}
				break;
			case ATTACKING:
				if(entity.heading == Enemy.Heading.Left) {					
					frame = anims[mode].getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					frame.flip(true, false);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				} else {
					frame = anims[mode].getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				}
				break;
			case DEAD:
				if(world.mode == World.Mode.REAL) {
					batch.draw(blood, entity.position.x, entity.position.y, 1, 1);
				}
				else {
					if(!poof.isAnimationFinished(entity.stateTime)) {
						frame = poof.getKeyFrame(entity.stateTime, false);						
						batch.draw(frame, entity.position.x, entity.position.y, 1, 2);
					}
				}
			default:
		}
	}
	
	private TextureRegion clip(TextureRegion region, boolean upper) {
		if(region.getRegionWidth() == 64 && region.getRegionHeight() == 64) return new TextureRegion(region);
		if(upper) return new TextureRegion(region.getTexture(), 0, 0, region.getRegionWidth(), 64);
		else return new TextureRegion(region.getTexture(), 0, 64, region.getRegionWidth(), 64);
	}
	
	private float clipOffset(TextureRegion region, boolean upper) {
		if(region.getRegionWidth() == 64 && region.getRegionHeight() == 64) return 0;
		if(upper) return 1;
		else return 0;
	}
	
	private void renderPlayer (Player entity, boolean upper) {
		TextureRegion frame;
		Animation anim = null;
		if(entity.axe_hits > 0 && world.mode != World.Mode.REAL) {
			anim = mainAxeIdle;
		} else {
			anim = mainIdle;
		}
		float offset = 0;
		switch(entity.state) {
			case IDLE:
				if(entity.heading == Heading.Left) {					
					frame = anim.getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					frame.flip(true, false);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				} else {
					frame = anim.getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				}
				break;
			case MOVING:
				if(entity.heading == Heading.Left) {					
					frame = anim.getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					frame.flip(true, false);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				} else {
					frame = anim.getKeyFrame(entity.stateTime, true);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					batch.draw(frame, entity.position.x, entity.position.y + offset, 1, 1);
				}
				break;
			case ATTACK:
				if(entity.heading == Heading.Left) {					
					frame = mainAttack.getKeyFrame(entity.stateTime, false);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					frame.flip(true, false);
					batch.draw(frame, entity.position.x - 0.5f, entity.position.y + offset, 2, 1);
				} else {
					frame = mainAttack.getKeyFrame(entity.stateTime, false);
					offset = clipOffset(frame, upper);
					frame = clip(frame, upper);
					batch.draw(frame, entity.position.x - 0.5f, entity.position.y + offset, 2, 1);
				}
				break;
			case DEAD:
				if(!upper) batch.draw(mainDead, entity.position.x - 0.5f, entity.position.y + offset, 2, 1);
				break;
			default:
		}
	}

	private void cameraFollow (float deltaTime) {
		Vector2 dist = new Vector2(world.player.position).sub(camera.position.x, camera.position.y);
		camera.position.add(dist.x * deltaTime * CAM_DAMP, dist.y * deltaTime * CAM_DAMP, 0);
	}
}
