package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Door extends Entity {
	public boolean isOpened;
	
	public Door (float x, float y) {
		super(x, y);
	}

	@Override
	public void update (World world, float deltaTime) {

	}
	
	public void checkDoor(World world){
		if(isOpened)
		{
			if(world.player.position.dst(position) < 2.0f && !(((int)position.x == (int)world.player.position.x))&&((int)position.y == (int)world.player.position.y)) {
				isOpened = false;
				world.walls[(int)position.x][(int)position.y] = new Rectangle(position.x, position.y, 1, 1);
			}
		}
		else {
			if(world.player.position.dst(position) < 1.2f) {
				isOpened = true;
				world.walls[(int)position.x][(int)position.y] = null;
			}
		}
	}
}
