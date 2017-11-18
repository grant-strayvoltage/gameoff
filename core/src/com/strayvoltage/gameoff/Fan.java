package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.strayvoltage.gamelib.GameSprite;

public class Fan extends GameMapObject implements Box2dCollisionHandler,SwitchHandler{

	//clockwise
	public static final int UP =0;
	public static final int RIGHT = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	
	public boolean isOn;
	boolean snap;
	//objects in fan 
	public Array<GameSprite> objects;
	public Array<String> switches;
	
	int direction = UP;
	
	int power;
	
	int fan_len = 2;
	
	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		m_btype = BodyType.StaticBody;
		m_isSensor = true;
		m_categoryBits = Box2dVars.OBJECT;
		m_filterMask = Box2dVars.PLAYER_NORMAL|Box2dVars.PLAYER_JUMPING|Box2dVars.BLOCK|Box2dVars.OBJECT|Box2dVars.POWER;
		isOn = getBool("startOn", mp);
		//get fan direction
		direction = getInt("dir", mp);
		power = Math.abs(getInt("power",mp));
		switches = getArray("switches", mp);
		objects = new Array<GameSprite>();
		snap = getBool("snap", mp);
		
		if(direction == UP || direction == DOWN )
			setSize(Box2dVars.PIXELS_PER_METER,Box2dVars.PIXELS_PER_METER*fan_len);
		else
			setSize(Box2dVars.PIXELS_PER_METER*fan_len,Box2dVars.PIXELS_PER_METER);
	}
	
	@Override
	public void update(float deltaTime) {
		if(isOn) {
			for(GameSprite o: objects) {
				if(direction == UP) {
					if(o.m_body.getLinearVelocity().y < 14)
						o.m_body.applyLinearImpulse(0,power, 0, 0, true);
				}else if(direction == DOWN) {
					if(o.m_body.getLinearVelocity().y > -14)
						o.m_body.applyLinearImpulse(0,-power, 0, 0, true);
				}else if(direction == RIGHT) {
					if(o.m_body.getLinearVelocity().x < 14)
						o.m_body.applyLinearImpulse(power,0, 0, 0, true);
				}else if(direction == LEFT) {
					if(o.m_body.getLinearVelocity().x > -14)
						o.m_body.applyLinearImpulse(-power,0, 0, 0, true);
				}	
				
			}
				
		}
		super.update(deltaTime);
	}
	
	@Override
	public void draw(Batch batch) {
	}

	@Override
	public void handleSwitch(Switch source) {
		if(switches.contains(source.name, false)) {
			isOn = !isOn;
		}
	}

	@Override
	public void handleBegin(Box2dCollision collision) {
		if(collision.target instanceof GameSprite) {
			if(collision.target instanceof PowerUnit)
				collision.target.m_body.getFixtureList().first().setDensity(1f);
			objects.add(collision.target);
		
		}
	}

	@Override
	public void handleEnd(Box2dCollision collision) {
		if(collision.target instanceof GameSprite) {
			if(collision.target instanceof PowerUnit)
				collision.target.m_body.getFixtureList().first().setDensity(.04f);
			objects.removeValue(collision.target,true);
		}
	}

}
