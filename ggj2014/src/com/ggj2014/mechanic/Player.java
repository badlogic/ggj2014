package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.ggj2014.ScreenManager;
	
public class Player extends Entity {
	public static final float ATTACK_TIME = 0.2f;
	public static final int AXE_HITS = 5;
	public float sightRange = 50;
	public float attackRange = 1.2f;
	public float attackAngle = (float)Math.PI / 4;
	public float speed = 3;
	public State state = State.IDLE;
	public double health = 100;
	public boolean actionPressed = false;
	public World world;
	public Heading heading = Heading.Right;
	public int axe_hits = 4;
	
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
					for(Entity e : world.entities) {
						if(e instanceof Door)
							((Door)e).checkDoor(world);
					}
					
					if(world.mode == world.GHOST && axe_hits > 0) {
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
		
		boolean hit = false;
		
		for(Enemy enemy : world.enemies) {
			if(enemy.state == Enemy.State.DEAD)
				continue;
			
			enemypos = enemy.getCenter().sub(pos);
			
			if(enemypos.len2() < attackRange * attackRange) {
				enemypos.nor();
				float angle = (float)Math.acos(heading == Heading.Left ? -enemypos.x : enemypos.x);
				
				if((enemypos.y >= 0 && angle <= Math.PI / 2) || (enemypos.y < 0 && angle < attackAngle)) {
					enemy.state = Enemy.State.DEAD;
					hit = true;
				}
			}
		}
		
		if(hit)
			axe_hits--;
		
		setState(State.ATTACK);
	}
	
	public void setState(State state) {
		if(this.state == state) return;
		this.state = state;
		this.stateTime = 0;
	}

	@Override
	public void update(World world, float deltaTime) {
		if(state == State.ATTACK && stateTime > ATTACK_TIME) setState(State.IDLE);
		if(state == State.IDLE || state == State.MOVING) processMove(world, deltaTime);
		stateTime += deltaTime;		
		
		if(world.mode == world.GHOST)
		{
			for(int i = 0; i <world.entities.size; i++)
			{
				Entity entity = world.entities.get(i);
			
				if(entity instanceof Pill) {
					if(entity.bounds.overlaps(this.bounds)) {
						((Pill) entity).pickUp(world);
						world.mode = world.REAL;
					}
				}
			}
		} else {
			for(int i = 0; i <world.entities.size; i++)
			{
				Entity entity = world.entities.get(i);
			
				if(entity instanceof Axe) {
					if(entity.bounds.overlaps(this.bounds)) {
						((Axe) entity).pickUp(world);
						axe_hits += AXE_HITS;
					}
				}
			}
		}

		boolean displayDebug = false;

		if(displayDebug) {
			world.renderer.sr.setProjectionMatrix(world.renderer.camera.combined);
			world.renderer.sr.begin(ShapeType.Line);
			Vector2 pos = getCenter();
			world.renderer.sr.arc(pos.x, pos.y, attackRange, heading == Heading.Left ? 90 : -attackAngle * MathUtils.radiansToDegrees, attackAngle * MathUtils.radiansToDegrees + 90, 30);
			world.renderer.sr.end();
		}
	}
	
	private void processMove (World world, float deltaTime) {
		Vector2 movement = new Vector2();
		if (Gdx.input.isKeyPressed(Keys.LEFT) || Gdx.input.isKeyPressed(Keys.A))
		{
			movement.x -= 1;
			setState(State.MOVING);
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT) || Gdx.input.isKeyPressed(Keys.D))
		{
			movement.x += 1;
			setState(State.MOVING);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN) || Gdx.input.isKeyPressed(Keys.S))
		{
			movement.y -= 1;
			setState(State.MOVING);
		}
		if (Gdx.input.isKeyPressed(Keys.UP) || Gdx.input.isKeyPressed(Keys.W))
		{
			movement.y += 1;
			setState(State.MOVING);
		}
		movement.nor().scl(speed * deltaTime);
		world.clipCollision(bounds, movement);
		position.add(movement);
		bounds.set(position.x + 0.15f, position.y, 0.7f, 0.8f);
		
		if(movement.x != 0)
			heading = movement.x < 0 ? Heading.Left : Heading.Right;
	}
	
	public void attackedByEnemy(double damage) {
		this.health = this.health - damage;
		if(this.health <= 0) this.state = State.DEAD;
	}
	
	enum State{
		IDLE, MOVING, ATTACK, DEAD
	}
	
	enum Heading {
		Left, Right
	}
}
