package com.ggj2014.mechanic;
 
public class Enemy2 extends Entity {
	public float combatRange = 1;
	public float sightRange = 5;
	public float speed = 2;
	public float damage = 10;
	public State state = State.IDLE;
	public float stateDuration;
	
	
	public Enemy2(float x, float y) {
		super(x, y);
	}

	@Override
	public void update(World world, float deltaTime) {
	}
	
	enum State {
		IDLE, MOVING_LEFT, MOVING_RIGHT, ATTACKING, WANDERING 
	}
}
