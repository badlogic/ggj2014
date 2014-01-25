package com.ggj2014.mechanic;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Bresenham2;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
 
public class Enemy extends Entity {
	private static Bresenham2 bresenham = new Bresenham2();
	
	public Vector2 velocity = new Vector2();
	public float combatRange = 1;
	public float sightRange = 5;
	public float flockRange = 4;
	public float speed = 2;
	public float damage = 10;
	public State state = State.IDLE;
	public float stateDuration;
	
	public float alignment_angle = (float)Math.PI * 0.5f;
	public float cohesion_angle = (float)Math.PI * 0.5f;
	public float separation_angle = (float)Math.PI * 0.4f;
	public float alignment_factor = 0.1f;
	public float cohesion_factor = 0.1f;
	public float separation_factor = 0.6f;
	public float player_factor = 1.0f;
	
	public Enemy(float x, float y) {
		super(x, y);
	}

	@Override
	public void update(World world, float deltaTime) {
		Vector2 playerpos = world.player.getCenter();
		Vector2 pos = getCenter();
		
		Vector2 new_velocity = new Vector2(0, 0);
		
		float length2 = playerpos.sub(pos).len2();
		if(length2 < sightRange * sightRange) {
			Array<GridPoint2> points = bresenham.line((int)pos.x, (int)pos.y, (int)(playerpos.x + pos.x), (int)(playerpos.y + pos.y));
			
			boolean in_sight = true;

			boolean displayDebug = true;
			
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
				new_velocity = playerpos.scl(player_factor / (float)Math.sqrt(length2));
				state = State.ATTACKING;
			}
			else
				state = State.IDLE;
		} else {
			state = State.IDLE;
		}
		
		Vector2 enemypos, relpos, t1;
		Vector2 normvel = velocity.cpy().nor();
		int count = 1;
		Vector2 avelocity = new Vector2(0, 0);
		Vector2 cpos = pos.cpy();
		Vector2 spos = new Vector2(0, 0);
		
		for(Enemy enemy : world.enemies) {
			if(enemy != this) {
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
		
		bounds.set(position.x + 0.15f, position.y, 0.7f, 0.8f);
	}
	
	enum State {
		IDLE, MOVING_LEFT, MOVING_RIGHT, ATTACKING, WANDERING 
	}
}
