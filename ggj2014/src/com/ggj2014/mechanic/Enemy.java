package com.ggj2014.mechanic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
 
public class Enemy extends Entity {
	private static Bresenham2 bresenham = new Bresenham2();
	
	public Vector2 velocity = new Vector2();
	public float combatRange = 1;
	public float sightRange = 5;
	public float flockRange = 4;
	public float min_speed = 0.25f;
	public float max_speed = 0.75f;
	public float attack_speed = 2;
	public float speed = 0;
	public float damage = 10;
	public State state = State.IDLE;
	
	public float targetSpeed = 0;
	public float stateDuration = 0;
	public float minStateDuration = 1.0f;
	public float maxStateDuration = 5.0f;
	public float acceleration = 4;
	public float maxTurnSpeed = (float)Math.PI * 10;
	public float turnSpeed = 0;
	public float pauseProbability = 0.3f;
	
	
	public float alignment_angle = (float)Math.PI * 0.5f;
	public float cohesion_angle = (float)Math.PI * 0.5f;
	public float separation_angle = (float)Math.PI * 0.4f;
	public float alignment_factor = 0.1f;
	public float cohesion_factor = 0.1f;
	public float separation_factor = 0.6f;
	public float player_factor = 0.8f;
	
	public Heading heading = Heading.Right;

	public Enemy(float x, float y) {
		super(x, y);
	}

	@Override
	public void update(World world, float deltaTime) {
		stateTime += deltaTime;
		if(stateTime < 0) stateTime = 0;
		if(state == State.DEAD) return;
		
		Vector2 playerpos = world.player.getCenter();
		Vector2 pos = getCenter();
		
		Vector2 new_velocity = velocity.nor();
		
		float length2 = playerpos.sub(pos).len2();
		
		if(world.mode == World.Mode.GHOST && length2 < sightRange * sightRange && world.player.state != Player.State.DEAD) {
			Array<GridPoint2> points = bresenham.line((int)pos.x, (int)pos.y, (int)(playerpos.x + pos.x), (int)(playerpos.y + pos.y));

			boolean in_sight = true;

			boolean displayDebug = false;

			if(displayDebug) {
				world.renderer.sr.setProjectionMatrix(world.renderer.camera.combined);
				world.renderer.sr.begin(ShapeType.Line);
			}

			for(GridPoint2 point : points) {
				if(displayDebug) {
					Color c = new Color(0, 0, world.walls[point.x][point.y] == null ? 1 : 0, 1);
					world.renderer.sr.rect(point.x, point.y, 1, 1, c, c, c, c);
				}
				if(world.walls[point.x][point.y] != null) {
					in_sight = false;
					break;
				}
			}

			if(displayDebug) {
				world.renderer.sr.end();
			}
			
			if(in_sight) {
				new_velocity = new_velocity.scl(1 - player_factor).add(playerpos.scl(player_factor / (float)Math.sqrt(length2)));
				setState(State.ATTACKING);
				targetSpeed = attack_speed;
			}
			else
				setState(targetSpeed > 0 ? State.WANDERING : State.IDLE);
		} else {
			setState(targetSpeed > 0 ? State.WANDERING : State.IDLE);
		}
		
		if(state == State.WANDERING || state == State.IDLE) {
			stateDuration -= deltaTime;
			
			if(stateDuration < 0) {
				// TODO: State change
				if(MathUtils.randomBoolean(pauseProbability)) {
					setState(State.IDLE);
					targetSpeed = 0;
					speed = 0;
				} else {
					if(state == State.IDLE) {
						float angle = MathUtils.random((float)Math.PI * 2);
						new_velocity = new Vector2((float)Math.cos(angle), (float)Math.sin(angle));
					}
					setState(State.WANDERING);
					targetSpeed = MathUtils.random(min_speed, max_speed);
					turnSpeed = MathUtils.random(-maxTurnSpeed, maxTurnSpeed);
				}
				
				stateDuration = MathUtils.random(minStateDuration, maxStateDuration);
			}

			if(state == State.WANDERING) {
				new_velocity.rotate(turnSpeed * deltaTime);
			}
		}
		
		if(speed != targetSpeed) {
			speed += acceleration * deltaTime * Math.signum(targetSpeed - speed);
		}
		
		Vector2 enemypos, relpos, t1;
		Vector2 normvel = velocity.cpy().nor();
		int count = 1;
		Vector2 avelocity = new Vector2(0, 0);
		Vector2 cpos = pos.cpy();
		Vector2 spos = new Vector2(0, 0);
		
		for(Enemy enemy : world.enemies) {
			if(enemy == this || enemy.state == State.DEAD)
				continue;
			enemypos = enemy.getCenter();
			
			float distance2 = enemypos.dst2(pos);
			if(distance2 < flockRange * flockRange) {
				relpos = enemypos.cpy().sub(pos);
				t1 = relpos.cpy().nor();
				float angle = (float)Math.acos(normvel.dot(t1));
				
				if(angle < alignment_angle) {
					avelocity.add(enemy.velocity);
				}
				
				if(angle < cohesion_angle) {
					cpos.add(enemypos);
					count++;
				}
				
				if(angle < separation_angle) {
					spos.add(relpos);
				}
			}
		}
		
		avelocity.nor();
		avelocity.scl(alignment_factor);
		
		cpos.div(count);
		cpos.sub(pos);
		cpos.nor();
		cpos.scl(cohesion_factor);
		
		spos.nor();
		spos.scl(separation_factor);
		
		new_velocity.add(avelocity).add(cpos).sub(spos).nor();
		
		// update position
		new_velocity.scl(deltaTime * speed);
		world.clipCollision(bounds, new_velocity);
		position.add(new_velocity);
		velocity = new_velocity;
		
		if(new_velocity.len2() == 0) {
			setState(State.IDLE);
		}
		
		bounds.set(position.x + 0.15f, position.y, 0.7f, 0.8f);
		heading = speed < 0.001f ? heading : (velocity.x < 0? Heading.Right: Heading.Left);
	}
	
	public void setState(State state) {
		if(this.state == state) return;
		
		if(this.state == State.ATTACKING)
			targetSpeed = MathUtils.random(min_speed, max_speed);
		
		this.state = state;
		this.stateTime = 0;
	}
	
	enum State {
		IDLE, ATTACKING, WANDERING, DEAD
	}
	
	enum Heading {
		Left, Right
	}
}
