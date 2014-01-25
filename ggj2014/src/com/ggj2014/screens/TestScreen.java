package com.ggj2014.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.ggj2014.Screen;
import com.ggj2014.ScreenManager;

public class TestScreen extends Screen {
	Color color;
	
	public TestScreen(ScreenManager manager, Color color) {
		super(manager);
		this.color = color;
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(color.r, color.g, color.b, color.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		if(Gdx.input.justTouched()) {
			manager.setScreen(new TestScreen(manager, new Color(MathUtils.random(), 0, 0, 1)));
		}
	}

	@Override
	public void dispose () {
	}
}
