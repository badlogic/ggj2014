package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
	
public class Player extends Entity {
	
	public float sightRange = 50;
	public float speed = 2;
	public State status = State.IDLE;
	
	public Player(Vector2 position) {
		super(position);
	}
	
	@Override
	public void update(World world, float deltaTime) {
		this.status = State.IDLE;
		
		if (Gdx.input.isKeyPressed(Keys.LEFT))
		{
			this.position = this.position.sub(new Vector2(speed*delta,0));
			this.status = State.MOVING;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
		{
			this.position = this.position.add(new Vector2(speed*delta,0));
			this.status = State.MOVING;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN))
		{
			this.position = this.position.sub(new Vector2(0, speed*delta));
			this.status = State.MOVING;
		}
		if (Gdx.input.isKeyPressed(Keys.UP))
		{
			this.position = this.position.add(new Vector2(0, speed*delta));
			this.status = State.MOVING;
		}
	}
	
	enum State{
		IDLE, MOVING, MOVING_UP, MOVING_DOWN, MOVING_LEFT, MOVING_RIGHT, TAKINGPILL 
	}

}
