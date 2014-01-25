package com.ggj2014.mechanic;

import com.badlogic.gdx.graphics.Texture.TextureFilter;
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
	public static int TILE_SIZE = 32;
	
	public TiledMap map;
	public Rectangle[][] walls;
	public Array<Entity> entities = new Array<Entity>();
	public Player player;
	public Goal goal;
	public int mode = REAL; 
	
	public World(String level) {
		loadLevel();
	}
	
	private void loadLevel () {
		// load tile map
		TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
		params.textureMinFilter = TextureFilter.Linear;
		params.textureMagFilter = TextureFilter.Linear;
		map = new TmxMapLoader().load("levels/testmap.tmx");		

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
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("wall");
		walls = new Rectangle[layer.getWidth()][layer.getHeight()];
		for(int x = 0; x < layer.getWidth(); x++) {
			for(int y = 0; y < layer.getHeight(); y++) {
				if(layer.getCell(x, y) != null) {
					walls[x][y] = new Rectangle(x, y, 1, 1);
				}
			}
		}
		
		// generate a few random enemies
		for(int i = 0; i < 10; i++) {
			Enemy enemy = new Enemy(MathUtils.random(0, 15), MathUtils.random(0, 15));
			entities.add(enemy);
		}
		
		for(int i = 0; i < 10; i++) {
			Enemy2 enemy = new Enemy2(MathUtils.random(0, 15), MathUtils.random(0, 15));
			entities.add(enemy);
		}
	}

	public void update(float deltaTime) {
		for(Entity entity: entities) {
			entity.update(this, deltaTime);
		}
	}
}
