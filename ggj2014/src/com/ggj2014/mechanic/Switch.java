package com.ggj2014.mechanic;

public class Switch extends Entity {

	public boolean isUsed = false;
	public String name;
	
	public Switch(float x, float y) {
		super(x, y);
	}

	@Override
	public void update(World world, float deltaTime) {
	}
	
	public void check(World world) {
		if (!isUsed && world.player.position.dst(position) < 1.2f) {
			isUsed = true;
		}
	}
}
