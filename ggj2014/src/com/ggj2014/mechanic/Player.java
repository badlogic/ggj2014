package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector2;
import com.ggj2014.DodgeBallHospital;
import com.ggj2014.ScreenManager;
	
public class Player extends Entity {
	public float sightRange = 50;
	public float attackRange = 2;
	public float attackAngle = (float)Math.PI / 3;
	public float speed = 3;
	public State state = State.IDLE;
	public double health = 100;
	public boolean actionPressed = false;
	public World world;
	public Vector2 heading = new Vector2(1, 0);
	
	public Player(World world_, Vector2 position) {
		super(position);
		world = world_;
		ScreenManager.multiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown (int keycode) {
				switch (keycode) {
				case Keys.TAB:
					world.toggleMode();
					return true;
					
				case Keys.SPACE:
				case Keys.E:
					if(world.mode == world.GHOST) {
						attack();
					}
					return true;

				default:
					break;
				}
				return false;
			}
		});
	}
	
	public void attack() {
		Vector2 pos = getCenter();
		Vector2 enemypos;
		
		for(Enemy enemy : world.enemies) {
			if(enemy.state == Enemy.State.DEAD)
				continue;
			
			enemypos = enemy.getCenter().sub(pos);
			
			if(enemypos.len2() < attackRange * attackRange) {
				enemypos.nor();
				float angle = (float)Math.acos(heading.dot(enemypos));
				
				System.out.println("In Range");
				
				if(angle < attackAngle) {
					System.out.println("Killed!");
					enemy.state = Enemy.State.DEAD;
				}
			}
		}
	}

	@Override
	public void update(World world, float deltaTime) {
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
		
		for(int i = 0; i <world.entities.size; i++)
		{
			Entity entity = world.entities.get(i);
		
			if(entity instanceof Pill) {
				if(entity.bounds.overlaps(this.bounds)) {
					((Pill) entity).pickUp();
					world.mode = world.REAL;
				}
			}
		}

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
		
		if(movement.x != 0 && movement.y != 0)
			heading = movement.nor();
	}
	
	public void attackedByEnemy(double damage) {
		this.health = this.health - damage;
		if(this.health <= 0) this.state = State.DEAD;
	}
	
	enum State{
		IDLE, MOVING_UP, MOVING_DOWN, MOVING_LEFT, MOVING_RIGHT, TAKINGPILL, DEAD
	}
}
