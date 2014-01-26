package com.ggj2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Input.Keys;

public abstract class Screen {
	protected final ScreenManager manager;
	
	protected InputAdapter adapter;
	
	public boolean keyPressed = false;
	
	public Screen(ScreenManager manager) {
		this.manager = manager;
		
		adapter = new InputAdapter() {
			@Override
			public boolean keyDown (int keycode) {
				if(keycode == Keys.ESCAPE)
					Gdx.app.exit();
				
				keyPressed = true;
				ScreenManager.multiplexer.removeProcessor(adapter);
				return true;
			}
		};
		
		ScreenManager.multiplexer.addProcessor(adapter);
	}
	
	public abstract void render();
	
	public abstract void dispose();
	
	public void pause() {
	}
	
	public void resume() {
	}
	
	protected boolean waitForKeypress() {
		if (Gdx.input.justTouched()) {
			return true;
		}
		
		return keyPressed;
	}
}
