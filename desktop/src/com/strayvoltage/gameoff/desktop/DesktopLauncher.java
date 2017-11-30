package com.strayvoltage.gameoff.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;
import com.strayvoltage.gameoff.GameOff;

public class DesktopLauncher {
	//set to false when we deploy
	public static boolean AUTO_PACK = true;//&& GameOff.DEBUG;
  
	public static void main (String[] arg) {
		
		//TEXTURE PACKING AT RUNTIME ------------------------------
		if(AUTO_PACK) {
			String input_dir = "../../input_assets";
			String output_dir = "./";
			
			Settings settings = new Settings();
			settings.useIndexes = true;
			//use power of two - faster on older OpenGl versions
			settings.pot = true;

			settings.maxWidth = 4096;
			settings.atlasExtension = ".txt";
			settings.maxHeight = 4096;
			settings.stripWhitespaceX = false;
			settings.stripWhitespaceY = false;
			//i dont usually use rotation because it restricts to the use of atlas_sprites but 
			//you can use them if you want its not a big deal 
			settings.rotation = false;
			
			//You can process several different input sub_dirs
			
			//TEST
			//TexturePacker.process(settings,input_dir+"/sprites", output_dir, "sprites");
			
			//GAMESPRITES -------------
			TexturePacker.process(settings,input_dir+"/game_sprites", output_dir, "game_sprites");
			TexturePacker.process(settings,input_dir+"/backgrounds", output_dir, "backgrounds");
			TexturePacker.process(settings,input_dir+"/cut_sprites", output_dir, "cut_sprites");
			
		}
		
		
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
	}
}
