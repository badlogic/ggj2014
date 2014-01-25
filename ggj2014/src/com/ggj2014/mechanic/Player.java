package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
	
public class Player extends Entity {
	
	public float sightRange = 50;
	public float speed = 3;
	public State status = State.IDLE;
	
	public Player(Vector2 position) {
		super(position);
	}
	
	@Override
	public void update(World world, float deltaTime) {
		this.status = State.IDLE;
		
		Vector2 movement = new Vector2();
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			movement.x -= 1;
			this.status = State.MOVING;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			movement.x += 1;
			this.status = State.MOVING;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			movement.y -= 1;
			this.status = State.MOVING;
		}
		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			movement.y += 1;
			this.status = State.MOVING;
		}
		movement.nor().mul(speed * deltaTime);
		position.add(movement);
		bounds.set(position.x + 0.15f, position.y, 0.7f, 0.8f);
	}
	
	enum State{
		IDLE, MOVING, MOVING_UP, MOVING_DOWN, MOVING_LEFT, MOVING_RIGHT, TAKINGPILL 
	}

}
