package com.ggj2014;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;

public class Level {
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private int tileSize;
	
	private Vector2 playerStartPosition;
	private Vector2 goalPosition;
	
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
		renderer.setView(camera);
		renderer.render();
	}

	public float getTileSize() {
		return tileSize;
	}
}
