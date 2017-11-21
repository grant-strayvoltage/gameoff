package com.strayvoltage.gameoff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.strayvoltage.gamelib.GameLayer;
import com.strayvoltage.gamelib.GameMain;

public class Exit extends GameMapObject implements Box2dCollisionHandler{
	
	public static boolean BRAIN_MODE = true;

	//how many players must touch exit to complete level
	static int REQUIREMENT = 2;
	
	int players_touched = 0;
	boolean brain_touched = false;
	
	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		m_isSensor = true;
		m_btype = BodyType.StaticBody;
		m_filterMask = Box2dVars.PLAYER_NORMAL|Box2dVars.BRAIN_FOOT;
		m_categoryBits = Box2dVars.OBJECT;
		setSize(Box2dVars.PIXELS_PER_METER, Box2dVars.PIXELS_PER_METER*2);
	}
	
	@Override
	public void draw(Batch batch) {
	}
	
	@Override
	public void update(float deltaTime) {
		if(BRAIN_MODE&&brain_touched) {
			//LEVEL IS COMPLETE
			Gdx.app.log("Exit:","level complete");
			//New level
			loadNextLevel();
		}else if(players_touched>=REQUIREMENT) {
			//LEVEL IS COMPLETE
			Gdx.app.log("Exit:","level complete");
			//New level
			loadNextLevel();
		}
		
		super.update(deltaTime);
	}
	

	  public static void loadNextLevel() {
		  final boolean game_complete = Boolean.parseBoolean(GameMain.getSingleton().getGlobal("game_complete"));
			Gdx.app.postRunnable(new Runnable() {
				
				@Override
				public void run() {
					if(game_complete) {
						//go to the main menu for now?
						GameLayer titleLayer = new TitleScreenLayer();
				        GameMain.getSingleton().replaceActiveLayer(titleLayer);
				        //reset game vars
				        GameMain.getSingleton().setGlobal("m_stage", "1");
				        GameMain.getSingleton().setGlobal("m_next_level", "1");
				        GameMain.getSingleton().setGlobal("Level", "1");
				        
				        //remove this for newgame+
				        GameMain.getSingleton().setGlobal("game_complete", "false");
				        //TODO: add newgame+
					}else {
						int stage = Integer.parseInt(GameMain.getSingleton().getGlobal("m_stage"));
						int level = Integer.parseInt(GameMain.getSingleton().getGlobal("m_next_level"));
						MainLayer ml = new MainLayer();
				        ml.loadLevel(stage,level);
				        GameMain.getSingleton().replaceActiveLayer(ml);
					}
					
				}
			});
	  }

	@Override
	public void handleBegin(Box2dCollision collision) {
		if(collision.target_type == Box2dVars.BRAIN_FOOT) {
			brain_touched = true;
		}else {
			players_touched++;
			Gdx.app.log("Exit:","player_touch_detected");
		}
		
	}

	@Override
	public void handleEnd(Box2dCollision collision) {
		if(collision.target_type == Box2dVars.BRAIN_FOOT) {
			brain_touched = false;
		}else {
			players_touched--;
			Gdx.app.log("Exit:","player_left_detected");
		}
	}

}
