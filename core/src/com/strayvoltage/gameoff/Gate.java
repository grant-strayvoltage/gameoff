package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

public class Gate extends GameMapObject implements SwitchHandler,Box2dCollisionHandler{
	
	static final int CLOSED = 0;
	static final int MOVING = 1;
	static final int OPEN = 2;
	
	//is this gate open
	boolean isOpen;
	//does this gate open instantly or do a moving animation
	boolean instant;
	
	//current state of this gate
	int state;
	
	//interpolation to use
	Interpolation equation;
	
	//switch filters
	Array<String> switches;

	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		if(getBool("hazard", mp)) 
			m_categoryBits = Box2dVars.HAZARD;
		else
			m_categoryBits = Box2dVars.OBJECT;
		m_filterMask = Box2dVars.PLAYER_NORMAL|Box2dVars.POWER|Box2dVars.OBJECT|Box2dVars.BLOCK;
		m_btype = BodyType.KinematicBody;
		setSize(Box2dVars.PIXELS_PER_METER,Box2dVars.PIXELS_PER_METER*3);
	}
	
	@Override
	public void draw(Batch batch) {
		//TODO:implement graphic
	}
	
	@Override
	public void handleBegin(Box2dCollision collision) {
		
	}

	@Override
	public void handleEnd(Box2dCollision collision) {
		
	}

	@Override
	public void handleSwitch(Switch source) {
		
	}

	

}
