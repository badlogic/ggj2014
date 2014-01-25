package com.ggj2014.mechanic;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	public Vector2 position;
	public boolean isVisible = true;
	public float stateTime;
	public Rectangle bounds = new Rectangle();
	
	public Entity(float x, float y) {
		this.position = new Vector2(x, y);
	}
	
	public Entity(Vector2 position) {
		this.position = position;
	}
	
	public Vector2 getCenter() {
		return new Vector2(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
	}
	
	public boolean collidesWith(Entity entity) {
		return bounds.overlaps(entity.bounds);
	}
	
	public abstract void update(World world, float deltaTime);


}
