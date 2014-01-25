package com.ggj2014;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class Game implements ApplicationListener {
	Texture img;
	SpriteBatch batch;
	Music music;
	Sound sound;
	Vector3 touch = new Vector3();
	OrthographicCamera camera;
	
	@Override
	public void create() {		
		img = new Texture(Gdx.files.internal("data/libgdx.png"));
		batch = new SpriteBatch();		
		music = Gdx.audio.newMusic(Gdx.files.internal("data/8.12.mp3"));
		music.setLooping(true);
//		music.play();
		sound = Gdx.audio.newSound(Gdx.files.internal("data/shotgun.ogg"));
		camera = new OrthographicCamera(480, 320);
	}

	@Override
	public void dispose() {

	}

	@Override
	public void render() {		
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f ,1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		camera.update();
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, touch.x, touch.y);
		batch.end();
		
		if(Gdx.input.justTouched()) {
			long id = sound.play();
			sound.setPan(id, MathUtils.random(-1, 1), 1);
		}
		
		if(Gdx.input.isTouched()) {		
			camera.unproject(touch.set(Gdx.input.getX(), Gdx.input.getY(), 0));
		}
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
