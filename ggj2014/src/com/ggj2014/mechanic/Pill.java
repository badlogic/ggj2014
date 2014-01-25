package com.ggj2014.mechanic;

public class Pill extends Entity {

	public Pill(float x, float y) {
		super(x, y);
		bounds.set(position.x + 0.15f, position.y, 0.7f, 0.8f);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(World world, float deltaTime) {
		// TODO Auto-generated method stub
		
	}

	public void pickUp() {
		this.isVisible = false;
		// TODO Auto-generated method stub
		
	}
}
