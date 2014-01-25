package com.ggj2014;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.ggj2014.mechanic.Goal;
import com.ggj2014.mechanic.Player;
import com.ggj2014.mechanic.World;

public class Level {
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private int tileSize;
	
	private Vector2 playerStartPosition = new Vector2();
	private Vector2 goalPosition = new Vector2();
	
	public Level() {
		map = new TmxMapLoader().load("data/testmap.tmx");
		tileSize = 32;
		renderer = new OrthogonalTiledMapRenderer(map, 1f / tileSize);

		MapObjects objects = map.getLayers().get("objects").getObjects();
		MapProperties player = objects.get("player").getProperties();
		MapProperties goal = objects.get("goal").getProperties();
		
		playerStartPosition.x = player.get("x", Float.class);
		playerStartPosition.y = player.get("y", Float.class);
		goalPosition.x = goal.get("x", Float.class);
		goalPosition.y = goal.get("y", Float.class);
	}
	
	public void render(OrthographicCamera camera) {
		camera.update();
		renderer.setView(camera);
		renderer.render();
	}

	public float getTileSize() {
		return tileSize;
	}

	public void load(World world) {
		world.entities.add(new Player(playerStartPosition));
		world.entities.add(new Goal(goalPosition));
		
		TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get("wall");
		world.walls = new Rectangle[layer.getWidth()][layer.getHeight()];
		
		for(int x = 0; x < layer.getWidth(); x++) {
			for(int y = 0; y < layer.getHeight(); y++) {
				if(layer.getCell(x, y) != null) {
					world.walls[x][y] = new Rectangle(x, y, 1, 1);
				}
			}
		}
	}
}
