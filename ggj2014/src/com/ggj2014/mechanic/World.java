package com.ggj2014.mechanic;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;

public class World {
	public TiledMap map;
	public Array<Entity> entities = new Array<Entity>();
	
	public World(TiledMap map) {
		this.map = map;
		// TODO parse map, create objects and collision layer
	}
	
	public void update(float deltaTime) {
		for(Entity entity: entities) {
			entity.update(this, deltaTime);
		}
	}
}
