package com.ggj2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.ggj2014.mechanic.World;

public class AudioManager {
	private static final float FADE_TIME = 1.5f;
	Music real;
	Music ghost;
	World.Mode mode = World.Mode.GHOST;
	float modeTime = 0;
	
	public AudioManager() {
		real = Gdx.audio.newMusic(Gdx.files.internal("audio/music-real.mp3"));
		real.setLooping(true);
		ghost = Gdx.audio.newMusic(Gdx.files.internal("audio/music-ghost.mp3"));
		ghost.setLooping(true);
	}
	
	public void setMode(World.Mode mode) {
		this.mode = mode;
		modeTime = 0;
		if(mode == World.Mode.REAL) {
			real.play();
		} else {
			ghost.play();			
		}
		System.out.println("Mode change: " + mode);
	}
	
	public void update(float deltaTime) {
		if(mode == World.Mode.REAL) {
			real.setVolume(Math.min(modeTime / FADE_TIME, 1));
			ghost.setVolume(Math.max(1 - modeTime / FADE_TIME, 0));
			if(modeTime > FADE_TIME) ghost.stop();
		} else {
			ghost.setVolume(Math.min(modeTime / FADE_TIME, 1));
			real.setVolume(Math.max(1 - modeTime / FADE_TIME, 0));
			if(modeTime > FADE_TIME) real.stop();
		}
		modeTime += deltaTime;
	}
	
	public void dispose() {
		real.dispose();
		ghost.dispose();
	}
}
