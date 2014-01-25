package com.ggj2014.mechanic;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

public class Door extends Entity {
	public boolean isOpened;
	
	public Door (float x, float y) {
		super(x, y);
	}

	@Override
	public void update (World world, float deltaTime) {
		if(Gdx.input.isKeyPressed(Keys.SPACE)) {
			if(world.player.position.dst(position) < 1.2f) {
				isOpened = true;
				world.walls[(int)position.x][(int)position.y] = null;
			}
		}
	}
}
