package com.ggj2014.mechanic;

import com.badlogic.gdx.math.Vector2;

public class Enemy extends Entity {
	
	public float combatRange = 1;
	public float sightRange = 50;
	public float speed = 2;
	public float damage = 10;
	public State status = State.IDLE;
	
	
	public Enemy(float x, float y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(World world, float deltaTime) {
		Player player = world.player;
		if(status == State.IDLE){
			if(this.position.dst(player.position)<combatRange) //If Player is in combat range beat the shit out of the player
					{
				
					}
			else if(this.position.dst(player.position)<combatRange) //Else if Player in Line of Sight & Range of Site -> move towards player.
					{
				
					}
			else{ // Else if wander arround randomly
				
				this.position = this.position.add(new Vector2(-1 + (int)(Math.random() * ((2) + 1)),-1 + (int)(Math.random() * ((2) + 1))));
			}
	
		}

		
	}
	
	enum State{
		IDLE, MOVING, ATTACKING, WANDERING 
	}

}
