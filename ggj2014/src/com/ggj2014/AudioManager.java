package com.ggj2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.ggj2014.mechanic.World;

public class AudioManager {
	private static final float FADE_TIME = 2;
	Music real;
	Music ghost;
	int mode = 0;
	float modeTime = 0;
	
	public AudioManager() {
		real = Gdx.audio.newMusic(Gdx.files.internal("audio/music-real.mp3"));
		real.setLooping(true);
		ghost = Gdx.audio.newMusic(Gdx.files.internal("audio/music-ghost.mp3"));
		ghost.setLooping(true);
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		modeTime = 0;
		if(mode == World.REAL) {
			real.play();
		} else {
			ghost.play();
		}
	}
	
	public void update(float deltaTime) {
		if(mode == World.REAL) {
			real.setVolume(Math.max(modeTime / FADE_TIME, 1));
			ghost.setVolume(Math.max(modeTime / FADE_TIME, 1));
			if(modeTime > FADE_TIME) ghost.stop();
		} else {
			ghost.setVolume(Math.max(modeTime / FADE_TIME, 1));
			real.setVolume(Math.max(modeTime / FADE_TIME, 1));
			if(modeTime > FADE_TIME) real.stop();
		}
		modeTime += deltaTime;
	}
	
	public void dispose() {
		real.dispose();
		ghost.dispose();
	}
}
