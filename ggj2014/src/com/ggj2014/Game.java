package com.ggj2014;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Game implements ApplicationListener {
	Texture img;
	Sound sound;
	Music music;
	
	Level level;

	OrthographicCamera camera;
	SpriteBatch batch;

	Rectangle bucket;

	@Override
	public void create() {
		img = new Texture(Gdx.files.internal("data/libgdx.png"));
		
		level = new Level();

		// load the drop sound effect and the rain background "music"
		sound = Gdx.audio.newSound(Gdx.files.internal("data/shotgun.ogg"));
		music = Gdx.audio.newMusic(Gdx.files.internal("data/8.12.mp3"));

		// start the playback of the background music immediately
		music.setLooping(true);
		music.play();

		camera = new OrthographicCamera();
		camera.position.x = 0;
		camera.position.y = 0;

		batch = new SpriteBatch();

		bucket = new Rectangle();
		bucket.x = -1;
		bucket.y = -1;
		bucket.width = 2;
		bucket.height = 2;
	}

	@Override
	public void dispose() {
		batch.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		camera.update();
		
		level.render(camera);

		/*batch.setProjectionMatrix(camera.combined);
		batch.begin();
		batch.draw(img, bucket.x, bucket.y);
		batch.end();*/
		
		if (Gdx.input.isKeyPressed(Keys.LEFT))
			camera.position.x -= 20 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.RIGHT))
			camera.position.x += 20 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.DOWN))
			camera.position.y -= 20 * Gdx.graphics.getDeltaTime();
		if (Gdx.input.isKeyPressed(Keys.UP))
			camera.position.y += 20 * Gdx.graphics.getDeltaTime();
	}

	@Override
	public void resize(int width, int height) {
		camera.setToOrtho(false, width / level.getTileSize(), height / level.getTileSize());
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}
