package com.ggj2014;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Array;
import com.ggj2014.mechanic.World;

public class AudioManager {
	private static final float FADE_TIME = 1.5f;
	Music real;
	Music ghost;
	World.Mode mode = World.Mode.GHOST;
	float modeTime = 0;
	long nextAtmoTime;
	
	Array<Sound> allSounds = new Array<Sound>();
	Array<Sound> atmoSounds = new Array<Sound>();
	Array<Sound> atmoSoundsGhost = new Array<Sound>();
	public Sound axeHit;
	public Sound axeNotHit;
	public Sound axePickup;
	public Sound doorClose;
	public Sound doorOpen;
	public Sound enemyEat;
	public Sound enemyWalk;
	public Sound playerDie;
	public Sound playerSwallow;
	public Sound trigger;
	
	
	public AudioManager() {
		real = Gdx.audio.newMusic(Gdx.files.internal("audio/music-real.mp3"));
		real.setLooping(true);
		ghost = Gdx.audio.newMusic(Gdx.files.internal("audio/music-ghost.mp3"));
		ghost.setLooping(true);
		
		for(int i = 1; i <= 7; i++) {
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("audio/effects/atmo_ghost" + i + ".mp3"));
			atmoSoundsGhost.add(sound);
			allSounds.add(sound);
		}
		
		for(int i = 1; i <= 4; i++) {
			Sound sound = Gdx.audio.newSound(Gdx.files.internal("audio/effects/atmo_scream" + i + ".mp3"));
			atmoSounds.add(sound);
			allSounds.add(sound);
		}
		
		axeHit = Gdx.audio.newSound(Gdx.files.internal("audio/effects/axe_hit.mp3"));
		allSounds.add(axeHit);
		axeNotHit = Gdx.audio.newSound(Gdx.files.internal("audio/effects/axe_nothit.mp3"));
		allSounds.add(axeNotHit);
		axePickup = Gdx.audio.newSound(Gdx.files.internal("audio/effects/axe_pickup.mp3"));
		allSounds.add(axePickup);
		doorClose = Gdx.audio.newSound(Gdx.files.internal("audio/effects/door_close.mp3"));
		allSounds.add(doorClose);
		doorOpen = Gdx.audio.newSound(Gdx.files.internal("audio/effects/door_open.mp3"));
		allSounds.add(doorOpen);
		enemyEat = Gdx.audio.newSound(Gdx.files.internal("audio/effects/enemy_eat.mp3"));
		allSounds.add(enemyEat);
		enemyWalk = Gdx.audio.newSound(Gdx.files.internal("audio/effects/enemy_walk.mp3"));
		allSounds.add(enemyWalk);
		playerDie = Gdx.audio.newSound(Gdx.files.internal("audio/effects/player_die.mp3"));
		allSounds.add(playerDie);
		playerSwallow = Gdx.audio.newSound(Gdx.files.internal("audio/effects/player_swallow.mp3"));
		allSounds.add(playerSwallow);
		trigger = Gdx.audio.newSound(Gdx.files.internal("audio/effects/switch.mp3"));
		allSounds.add(trigger);
	}
	
	public void setMode(World.Mode mode) {
		this.mode = mode;
		modeTime = 0;
		if(mode == World.Mode.REAL) {
			real.play();
		} else {
			ghost.play();
		}
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
		
		if(System.nanoTime() > nextAtmoTime) {
			if(mode == World.Mode.REAL) {
				
			} else {
				
			}
		}
	}
	
	public void dispose() {
		real.dispose();
		ghost.dispose();
		for(Sound sound: allSounds) {
			sound.dispose();
		}
	}
}
