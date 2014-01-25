package com.ggj2014.mechanic;

public class Switch extends Entity {

	public boolean isUsed = false;
	public String name;
	
	public Switch(float x, float y) {
		super(x, y);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(World world, float deltaTime) {
		// TODO Auto-generated method stub
		
	}
	
	public void useSwitch(){
		this.isUsed = true;
	}
}
