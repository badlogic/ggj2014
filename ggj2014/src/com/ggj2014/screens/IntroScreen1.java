package com.ggj2014.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ggj2014.Screen;
import com.ggj2014.ScreenManager;

public class IntroScreen1 extends Screen {

	Texture texture = new Texture(Gdx.files.internal("graphics/intro-panel-1.png"));
	SpriteBatch batch;
	InputAdapter input;
	
	public IntroScreen1(final ScreenManager manager) {
		super(manager);
		batch = new SpriteBatch();
		input = new InputAdapter(){

			@Override
			public boolean keyDown(int keycode) {
				// TODO Auto-generated method stub
				manager.setScreen(new IntroScreen2(manager));
				return super.keyDown(keycode);
			}
			
		};
		manager.multiplexer.addProcessor(input);
	}

	@Override
	public void render() {

		Gdx.gl20.glClearColor(0.4f, 0.4f, 1, 1);
		Gdx.gl20.glClear(Gdx.gl20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();
		
	}

	@Override
	public void dispose() {
		batch.dispose();
		texture.dispose();
		manager.multiplexer.removeProcessor(input);
	}

}
