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
	public float speed = 2;
	public float damage = 10;
	public State state = State.IDLE;
	public float stateDuration;
	
	public Enemy(float x, float y) {
		super(x, y);
	}

	@Override
	public void update(World world, float deltaTime) {
		Vector2 movement = world.player.position.cpy();
		float length2 = movement.sub(position).len2();
		if(length2 < 25) {
			Vector2 p1 = getCenter();
			Vector2 p2 = world.player.getCenter();
			
			Array<GridPoint2> points = bresenham.line((int)p1.x, (int)p1.y, (int)p2.x, (int)p2.y);
			
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
				movement.scl(deltaTime * speed / (float)Math.sqrt(length2));
				world.clipCollision(bounds, movement);
				position.add(movement);
				state = State.ATTACKING;
			}
			else
				state = State.IDLE;
		} else {
			state = State.IDLE;
		}
		
		Vector2 enemypos;
		int count = 0;
		Vector2 velocity = new Vector2(0, 0);
		Vector2 pos = getCenter();
		Vector2 mypos = pos.cpy();
		
		for(Enemy enemy : world.enemies) {
			if(enemy != this) {
				enemypos = enemy.getCenter().sub(mypos);
				float distance2 = enemypos.dst2(enemy.getCenter());
				if(distance2 < 16) {
					count++;
					velocity.add(enemy.velocity);
					pos.add(enemy.getCenter());
				}
			}
		}
		
		pos.div(count);
		pos.sub(mypos);
		pos.nor();
		velocity.nor();
		
		bounds.set(position.x + 0.15f, position.y, 0.7f, 0.8f);
	}
	
	enum State {
		IDLE, MOVING_LEFT, MOVING_RIGHT, ATTACKING, WANDERING 
	}
}
