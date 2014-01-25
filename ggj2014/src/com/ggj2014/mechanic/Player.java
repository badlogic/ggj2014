package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.ggj2014.DodgeBallHospital;
import com.ggj2014.ScreenManager;
	
public class Player extends Entity {
	public float sightRange = 50;
	public float speed = 3;
	public State state = State.IDLE;
	public double health = 100;
	public boolean actionPressed = false;
	public World world;
	
	public Player(Vector2 position) {
		super(position);
		ScreenManager.multiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown (int keycode) {
				if(keycode == Keys.SPACE) {	
					if(world != null) {
						world.toggleMode();
					}
				}
				return false;
			}
		});
	}
	
	@Override
	public void update(World world, float deltaTime) {
		this.world = world;
		State oldState = state;
		// as long as we aren't dead the player can 
		// perform actions
		if(state != State.DEAD) {
			processMove(world, deltaTime);
		}
		if(oldState != state) {
			stateTime = 0;
		}
		stateTime += deltaTime;		
	}
	
	private void processMove (World world, float deltaTime) {
		Vector2 movement = new Vector2();
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
		{
			movement.x -= 1;
			this.state = State.MOVING_LEFT;
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
		{
			movement.x += 1;
			this.state = State.MOVING_RIGHT;
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
		{
			movement.y -= 1;
			this.state = State.MOVING_DOWN;
		}
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
		{
			movement.y += 1;
			this.state = State.MOVING_UP;
		}
		movement.nor().scl(speed * deltaTime);
		world.clipCollision(bounds, movement);
		position.add(movement);
		bounds.set(position.x + 0.15f, position.y, 0.7f, 0.8f);
	}

	private void processAttack(World world, float deltaTime) {
		// TODO
	}
	
	public void attackedByEnemy(double damage) {
		this.health = this.health - damage;
		if(this.health <= 0) this.state = State.DEAD;
	}
	
	enum State{
		IDLE, MOVING_UP, MOVING_DOWN, MOVING_LEFT, MOVING_RIGHT, TAKINGPILL, DEAD
	}
}
