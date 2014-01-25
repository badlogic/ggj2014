package com.ggj2014;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Game implements ApplicationListener {
	Texture img;
	SpriteBatch batch = new SpriteBatch();
	
	@Override
	public void create() {		
		img = new Texture(Gdx.files.internal("data/libgdx.png"));
	}

	@Override
	public void dispose() {

	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f ,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.draw(img, -100, -100);
		batch.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
