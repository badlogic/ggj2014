package com.ggj2014.mechanic;

import com.badlogic.gdx.math.Vector2;

public abstract class Entity {
	public Vector2 position = new Vector2();
	public boolean isVisible = true;
	public float delta;
	
	public Entity(float x, float y) {
		this.position.set(x, y);
	}
	
	public abstract void update(World world, float deltaTime);
}
