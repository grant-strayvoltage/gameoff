package com.strayvoltage.gameoff.pack;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
import com.badlogic.gdx.tools.texturepacker.TexturePacker.Settings;

public class Packer {
  
	public static void main (String[] arg) {
		
		String input_dir = "../../input_assets";
		String output_dir = "./";
		
		Settings settings = new Settings();
		settings.useIndexes = true;
		settings.pot = true;  //use power of two - faster on older OpenGl versions

		settings.maxWidth = 4096;
		settings.atlasExtension = ".txt";
		settings.maxHeight = 4096;
		settings.stripWhitespaceX = false;
		settings.stripWhitespaceY = false;
		settings.rotation = false;
		
		//GAMESPRITES -------------
		TexturePacker.process(settings,input_dir+"/game_sprites", output_dir, "game_sprites");
		TexturePacker.process(settings,input_dir+"/backgrounds", output_dir, "backgrounds");
		TexturePacker.process(settings,input_dir+"/cut_sprites", output_dir, "cut_sprites");
		
	}
}
