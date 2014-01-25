package com.ggj2014.mechanic;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class World {
	public static final int REAL = 0;
	public static final int GHOST = 1;
	public static int TILE_SIZE = 64;
	
	public TiledMap map;
	public Rectangle[][] walls;
	public Array<Entity> entities = new Array<Entity>();
	public Array<Enemy> enemies = new Array<Enemy>();
	public Player player;
	public Goal goal;
	private int mode = REAL;
	public WorldRenderer renderer;
	
	public World(String level) {
		loadLevel(level);
	}
	
	public void setRenderer(WorldRenderer renderer) {
		this.renderer = renderer;
	}
	
	private void loadLevel (String level) {
		// load tile map
		TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
		params.textureMinFilter = TextureFilter.Linear;
		params.textureMagFilter = TextureFilter.Linear;
		map = new TmxMapLoader().load(level);

		// load objects from map
		MapObjects objects = map.getLayers().get("objects").getObjects();
		MapProperties playerProps = objects.get("player").getProperties();
		MapProperties goalProps = objects.get("goal").getProperties();
		player = new Player(new Vector2(playerProps.get("x", Float.class), playerProps.get("y", Float.class)));
		player.position.scl(1f / TILE_SIZE);		
		entities.add(player);
		goal = new Goal(new Vector2(goalProps.get("x", Float.class), goalProps.get("y", Float.class)));
		goal.position.scl(1f / TILE_SIZE);
		
		// load collision map
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("collisionmap");
		walls = new Rectangle[layer.getWidth()][layer.getHeight()];
		for(int x = 0; x < layer.getWidth(); x++) {
			for(int y = 0; y < layer.getHeight(); y++) {
				if(layer.getCell(x, y) != null) {
					walls[x][y] = new Rectangle(x, y, 1, 1);
				}
			}
		}
		layer = (TiledMapTileLayer) map.getLayers().get("interieur");
		for(int x = 0; x < layer.getWidth(); x++) {
			for(int y = 0; y < layer.getHeight(); y++) {
				if(layer.getCell(x, y) != null) {
					walls[x][y] = new Rectangle(x, y, 1, 1);
				}
			}
		}
		
		// create objects
		for(int i = 0; i < objects.getCount(); i++) {
			MapProperties object = objects.get(i).getProperties();
			String type = object.get("type", String.class);
			if(type.equals("enemy1")) {
				Enemy enemy = new Enemy(object.get("x", Float.class), object.get("y", Float.class));
				enemy.position.scl(1f / TILE_SIZE);
				entities.add(enemy);
				enemies.add(enemy);
			}
			if(type.equals("enemy2")) {
				Enemy enemy = new Enemy2(object.get("x", Float.class), object.get("y", Float.class));
				enemy.position.scl(1f / TILE_SIZE);
				entities.add(enemy);
				enemies.add(enemy);
			}
			if(type.equals("pille")) {
				Pill pill = new Pill(object.get("x", Float.class), object.get("y", Float.class));
				pill.position.scl(1f / TILE_SIZE);
				entities.add(pill);
			}
		}
	}

	public void update(float deltaTime) {
		for(Entity entity: entities) {
			entity.update(this, deltaTime);
		}
	}
	
	public void clipCollision(Rectangle bounds, Vector2 movement) {
		Rectangle newbounds = new Rectangle(bounds.x + movement.x, bounds.y + movement.y, bounds.width, bounds.height);
	
		int sx, sy, ex, ey, ux, uy;
		if(movement.x > 0) {
			sx = Math.max((int)Math.floor(bounds.x), 0);
			ex = Math.min((int)Math.ceil(newbounds.x + bounds.width) + 1, walls.length) ;
			ux = 1;
		}
		else {
			sx = Math.min((int)Math.ceil(bounds.x + bounds.width), walls.length - 1);
			ex = Math.max((int)Math.floor(newbounds.x) - 1, -1);
			ux = -1;
		}
		
		if(movement.y > 0) {
			sy = Math.max((int)Math.floor(bounds.y), 0);
			ey = Math.min((int)Math.ceil(newbounds.y + bounds.height) + 1, walls[0].length);
			uy = 1;
		}
		else {
			sy = Math.min((int)Math.ceil(bounds.y + bounds.height), walls[0].length - 1);
			ey = Math.max((int)Math.floor(newbounds.y) - 1, -1);
			uy = -1;
		}
		
		Color c = new Color(0, 0, 1, 1);
		boolean displayDebug = false;
		
		if(displayDebug) {
			renderer.sr.setProjectionMatrix(renderer.camera.combined);
			renderer.sr.begin(ShapeType.Line);
		}
		
		if(sx < 0) sx = 0;
		if(sx >= walls.length) sx = walls.length - 1;
		if(sy < 0) sy = 0;
		if(sy >= walls[0].length) sy = walls[0].length - 1;
		if(ex < 0) ex = 0;
		if(ex >= walls.length) ex = walls.length - 1;
		if(ey < 0) ey = 0;
		if(ey >= walls[0].length) ey = walls[0].length - 1;
		
		for(int x = sx; x != ex; x += ux) {
			for(int y = sy; y != ey; y += uy) {
				Rectangle r = walls[x][y];
				if(r != null) {
					if(displayDebug) {
						renderer.sr.rect(r.x, r.y, r.width, r.height, c, c, c, c);
					}
					
					if(r.overlaps(newbounds)) {
						float x1, x2, y1, y2;
						
						if(movement.x > 0) {
							x1 = bounds.x + bounds.width;
							x2 = r.x;
						}
						else {
							x1 = bounds.x;
							x2 = r.x + r.width;
						}
						
						if(movement.y > 0) {
							y1 = bounds.y + bounds.height;
							y2 = r.y;
						}
						else {
							y1 = bounds.y;
							y2 = r.y + r.height;
						}
						
						float d1 = (x2 - x1) / movement.x;
						float d2 = (y2 - y1) / movement.y;
						
						if(d1 >= 0 && d1 <= 1) {
							// collision in x direction
							movement.x = 0;
						}
						
						if(d2 >= 0 && d2 <= 1) {
							// collision in y direction
							movement.y = 0;
						}
						
						newbounds.x = bounds.x + movement.x;
						newbounds.y = bounds.y + movement.y;
					}
				}
			}
		}
		
		if(displayDebug) {
			c = new Color(1, 0, 1, 1);
			float rx = Math.min(sx, ex - 1);
			float ry = Math.min(sy, ey - 1);
			float rwidth = Math.max(sx, ex - 1) - rx + 1;
			float rheight = Math.max(sy, ey - 1) - ry + 1;
			renderer.sr.rect(rx, ry, rwidth, rheight, c, c, c, c);
			renderer.sr.end();
		}
	}
	
	public void toggleMode() {
		
	}
	
	public int getMode() {
		return mode;
	}
}
