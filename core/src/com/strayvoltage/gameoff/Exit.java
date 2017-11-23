package com.strayvoltage.gameoff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.strayvoltage.gamelib.*;

public class Exit extends GameMapObject implements Box2dCollisionHandler{
	
	//TURN THIS ON OR OFF TO ACTIVATE BRAN MODE
	//brain mode makes it so the exit only requires brain contact to go to the 
	//next level. 
	public static boolean BRAIN_MODE = false;

	//how many players must touch exit to complete level
	static int REQUIREMENT = 2;
	
	int players_touched = 0;
	boolean brain_touched = false;
	GameAnimateable m_doorOpen;
	int m_state = 0;
	
	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		m_isSensor = true;
		m_btype = BodyType.StaticBody;
		m_filterMask = Box2dVars.PLAYER_NORMAL;
		m_categoryBits = Box2dVars.OBJECT;
		TextureRegion texture = null;
		texture = textures.findRegion("door_F1");
		this.setRegion(texture);
		this.setSize(texture.getRegionWidth(),texture.getRegionHeight());
		m_doorOpen = new AnimateSpriteFrame(textures, new String[] {"door_F1", "door_F2", "door_F3", "door_F3","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F4","door_F3","door_F2","door_F1","door_F1","door_F1"}, 2.5f, 1);
	}
	
	@Override
	public void update(float deltaTime) {

		if (m_state < 1)
		{
			if(players_touched>=REQUIREMENT)
			{
				//LEVEL IS COMPLETE
				Gdx.app.log("Exit:","level complete");
				//New level
				m_state = 1;
				this.runAnimation(m_doorOpen);
			}
		}

		if (m_state == 1)
		{
			if (m_doorOpen.isRunning() == false)
			{
				//loadNextLevel();
			}
		}
		
		super.update(deltaTime);
	}

	public int getState()
	{
		return m_state;
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
