package com.ggj2014.mechanic;

import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	public Vector2 position;
	public boolean isVisible = true;
	public float delta;
	
	public Entity(float x, float y) {
		this.position = new Vector2(x, y);
	}
	
	public Entity(Vector2 position) {
		this.position = position;
	}
	
	public abstract void update(World world, float deltaTime);
}
