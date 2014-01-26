package com.ggj2014.mechanic;

public class Axe extends Entity {

	public Axe(float x, float y) {
		super(x, y);
		bounds.set(position.x + 0.15f, position.y, 0.7f, 0.8f);
	}
	
	@Override
	public void update(World world, float deltaTime) {
	}

	public void pickUp(World world) {
		world.delete.add(this);
	}
}
