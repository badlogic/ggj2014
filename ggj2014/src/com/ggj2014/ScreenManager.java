package com.ggj2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;

public class ScreenManager {
	public static InputMultiplexer multiplexer;
	Screen current;
	
	public ScreenManager() {
		multiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(multiplexer);
	}
	
	public void render() {
		if(current != null) {
			current.render();
		}
	}
	
	public void dispose() {
		if(current != null) {
			current.dispose();
			current = null;
		}
	}
	
	public void pause() {
		if(current != null) {
			current.pause();
		}
	}
	
	public void resume() {
		if(current != null) {
			current.resume();
		}
	}
	
	public void setScreen(Screen screen) {
		if(current != null) {
			current.pause();
			current.dispose();
		}		
		current = screen;
	}
}
