package com.strayvoltage.gameoff.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.strayvoltage.gameoff.GameOff;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		config.width=640;
    	config.height=360;

        //config.width= LwjglApplicationConfiguration.getDesktopDisplayMode().width;
        //config.height= LwjglApplicationConfiguration.getDesktopDisplayMode().height;

    	// fullscreen
    	config.fullscreen = false;
    	// vSync
    	config.vSyncEnabled = true;
		new LwjglApplication(new GameOff(), config);
	}
}
