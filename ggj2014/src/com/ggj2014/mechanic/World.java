package com.ggj2014.mechanic;

import com.badlogic.gdx.utils.Array;
import com.ggj2014.Level;

public class World {
	public Level level;
	public Array<Entity> entities = new Array<Entity>();
	public Entity player; 
	
	public World(Level level) {
		this.level = level;
		level.load(this);
		// TODO parse map, create objects and collision layer
	}
	
	public void update(float deltaTime) {
		for(Entity entity: entities) {
			entity.update(this, deltaTime);
		}
	}
	
	// TODO add methods for entities to use for collision detection etc.
	
}
