package com.ggj2014.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ggj2014.Screen;
import com.ggj2014.ScreenManager;

public class GameOverScreen extends Screen {

	Texture texture = new Texture(Gdx.files.internal("graphics/gameover-screen.png"));
	SpriteBatch batch;
	
	public GameOverScreen(ScreenManager manager) {
		super(manager);
		batch = new SpriteBatch();
	}

	@Override
	public void render() {

		Gdx.gl20.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
		
		if (waitForKeypress()) {
	    	 manager.setScreen(new CreditScreen(manager));
	     }
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
	}

}
