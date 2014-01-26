package com.ggj2014;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class DodgeBallHospitalDesktop {
	public static void main(String[] args) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "ggj2014";
		cfg.useGL20 = true;
		cfg.width = 1024;
		cfg.height = 768;
		
		if(args.length > 0) {
			cfg.setFromDisplayMode(LwjglApplicationConfiguration.getDesktopDisplayMode());
		}
		
		new LwjglApplication(new DodgeBallHospital(), cfg);
	}
}
