package com.ggj2014;

public abstract class Screen {
	protected final ScreenManager manager;
	
	public Screen(ScreenManager manager) {
		this.manager = manager;
	}
	
	public abstract void render();
	
	public abstract void dispose();
	
	public void pause() {
	}
	
	public void resume() {
	}
}
