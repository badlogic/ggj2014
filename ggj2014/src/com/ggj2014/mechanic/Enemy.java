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
	}

	@Override
	public void update(World world, float deltaTime) {
		Player player = world.player;
		if(status == State.IDLE){
			if(this.position.dst(player.position)<combatRange) //If Player is in combat range beat the shit out of the player
					{
						//TODO: A* Zielfindung. Derweil nur zufällige Beweung in eine Richtung maximal mit geschwindigkeit zwischen 0 - speed
					this.position = this.position.add(new Vector2(-1 + (int)(Math.random() * speed * ((2) + 1)),-1 + (int)(Math.random() * speed * ((2) + 1))));
					this.status = State.MOVING;
					}
			else if(this.position.dst(player.position)<combatRange) //Else if Player in Line of Sight & Range of Site -> move towards player.
					{
					player.attackedByEnemy(Math.random()*damage);
					this.status = State.ATTACKING;
					}
			else{ // Else if wander arround randomly
				
				this.position = this.position.add(new Vector2(-1 + (int)(Math.random() * ((2) + 1)),-1 + (int)(Math.random() * ((2) + 1))));
				this.status = State.MOVING;
			}
	
		}
		

		
	}
	
	enum State{
		IDLE, MOVING, ATTACKING, WANDERING 
	}

}
