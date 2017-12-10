package com.strayvoltage.gameoff.desktop;

import com.badlogic.gdx.backends.lwjgl3.*;
import com.strayvoltage.gameoff.GameOff;

public class DesktopLauncher {
  
	public static void main (String[] arg) {
		
		/* 
		//old - switched to Lwjgl3 back end to fix controller support issues
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width=1280;
    config.height=720;

		//config.width= LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		//config.height= LwjglApplicationConfiguration.getDesktopDisplayMode().height;

		// fullscreen
		config.fullscreen = false;
		// vSync
		config.vSyncEnabled = true;

		new LwjglApplication(new GameOff(), config); 
		*/

			
			Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
			config.setWindowedMode(1280,720);
      new Lwjgl3Application(new GameOff(), config);

	}
}
