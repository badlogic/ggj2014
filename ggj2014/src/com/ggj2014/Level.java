package com.ggj2014;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class Level {
	private TiledMap map;
	private OrthogonalTiledMapRenderer renderer;
	private int tileSize;
	
	public Level() {
		map = new TmxMapLoader().load("data/testmap.tmx");
		tileSize = 32;
		renderer = new OrthogonalTiledMapRenderer(map, 1f / tileSize);
	}
	
	public void render(OrthographicCamera camera) {
		renderer.setView(camera);
		renderer.render();
	}

	public float getTileSize() {
		return tileSize;
	}
}
