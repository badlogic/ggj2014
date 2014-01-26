package com.ggj2014.mechanic;

import com.badlogic.gdx.math.Rectangle;

public class Door extends Entity {
	public boolean isOpened;
	public String switchname;

	public Door(float x, float y) {
		super(x, y);
	}

	@Override
	public void update(World world, float deltaTime) {

	}

	public void checkDoor(World world) {
		if (isOpened) {
			if (world.player.position.dst(position) < 2.0f
					&& !(((int) position.x == (int) world.player.position.x))
					&& ((int) position.y == (int) world.player.position.y)) {
				isOpened = false;
				world.walls[(int) position.x][(int) position.y] = new Rectangle(
						position.x, position.y, 1, 1);
				world.audio.doorClose.play();
			}
		} else {
			if (world.player.position.dst(position) < 1.2f) {
				if (switchname != null) {
					for (int i = 0; i < world.entities.size; i++) {
						Entity entity = world.entities.get(i);
						if (entity instanceof Switch) {
							if (((Switch) entity).name.equals(switchname)
									&& ((Switch) entity).isUsed) {
								isOpened = true;
								world.walls[(int) position.x][(int) position.y] = null;
								world.audio.trigger.play();
							}
						} else {
							world.audio.doorLocked.play();
						}
					}
				} else {
					isOpened = true;
					world.walls[(int) position.x][(int) position.y] = null;
					world.audio.doorOpen.play();
				}
			}
		}
	}
}
