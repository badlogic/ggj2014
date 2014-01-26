package com.ggj2014.mechanic;

import com.badlogic.gdx.math.Vector2;

public class Goal extends Entity {
	public Goal(Vector2 position) {
		super(position);
		bounds.set(position.x + 0.15f, position.y, 0.7f, 0.8f);
	}
	
	@Override
	public void update(World world, float deltaTime) {
	}
}
