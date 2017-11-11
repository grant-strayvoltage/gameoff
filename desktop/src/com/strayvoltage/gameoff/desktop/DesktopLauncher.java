package com.strayvoltage.gameoff.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.strayvoltage.gameoff.GameOff;

public class DesktopLauncher {
	//set to false when we deploy
	public static boolean AUTO_PACK = true;
	
	public static void main (String[] arg) {
		
		//TEXTURE PACKING AT RUNTIME ------------------------------
		if(AUTO_PACK) {
			String input_dir = "../../input_assets";
			String output_dir = "image";
			
			Settings settings = new Settings();
			settings.useIndexes = true;
			//use power of two - faster on older OpenGl versions
			settings.pot = true;
			//huge atlases. this is a lot bigger than what im usually comfortable with lol
			settings.maxWidth = 2048; 
			settings.atlasExtension = ".txt";
			settings.maxHeight = 2048;
			settings.stripWhitespaceX = true;
			settings.stripWhitespaceY = true;
			//i dont usually use rotation because it restricts to the use of atlas_sprites but 
			//you can use them if you want its not a big deal 
			settings.rotation = false;
			
			//You can process several different input sub_dirs
			
			//TEST
			TexturePacker.process(settings,input_dir+"/test", output_dir, "super_test");
			
			//GAMESPRITES -------------
			//TexturePacker.process(input_dir+"/game_sprites", output_dir, "game_sprites");
		}
		
		
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
