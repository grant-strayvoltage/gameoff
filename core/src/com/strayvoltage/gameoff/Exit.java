package com.strayvoltage.gameoff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.strayvoltage.gamelib.GameMain;

public class Exit extends GameMapObject implements Box2dCollisionHandler{

	//how many players must touch exit to complete level
	static int REQUIREMENT = 1;
	
	int players_touched = 0;
	
	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		m_isSensor = true;
		m_btype = BodyType.StaticBody;
		m_filterMask = Box2dVars.PLAYER_NORMAL;
		m_categoryBits = Box2dVars.OBJECT;
		setSize(Box2dVars.PIXELS_PER_METER, Box2dVars.PIXELS_PER_METER*2);
	}
	
	@Override
	public void draw(Batch batch) {
	}
	
	@Override
	public void update(float deltaTime) {
		if(players_touched>=REQUIREMENT) {
			//LEVEL IS COMPLETE
			Gdx.app.log("Exit:","level complete");
			//New level
			boolean game_complete = Boolean.parseBoolean(GameMain.getSingleton().getGlobal("game_complete"));
			if(game_complete) {
				//go to the main menu for now?
			}else {
				int stage = Integer.parseInt(GameMain.getSingleton().getGlobal("m_stage"));
				int level = Integer.parseInt(GameMain.getSingleton().getGlobal("m_next_level"));
				MainLayer ml = new MainLayer();
		        ml.loadLevel(1,1);
		        GameMain.getSingleton().replaceActiveLayer(ml);
			}
		}
		super.update(deltaTime);
	}

	@Override
	public void handleBegin(Box2dCollision collision) {
		players_touched++;
		Gdx.app.log("Exit:","player_detected");
	}

	@Override
	public void handleEnd(Box2dCollision collision) {
		players_touched--;
		Gdx.app.log("Exit:","player_left");
	}

}
