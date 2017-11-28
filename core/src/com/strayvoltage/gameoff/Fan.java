package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.strayvoltage.gamelib.*;

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
	
	int fan_len = 14;
	float m_force = 250f;
	float m_offX = 0;
	float m_offY = 0;

	float m_fy = 0;
	float m_fx = 0;

	GameAnimateable m_rotateAnimation;
	GameSprite m_fanSprite;
	TextureAtlas m_textures;
	GameParticleSystem m_particleSystem;

	long m_soundId = -1;
	
	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		m_btype = BodyType.StaticBody;
		m_isSensor = true;
		m_categoryBits = Box2dVars.FAN;
		m_filterMask = Box2dVars.PLAYER_NORMAL|Box2dVars.PLAYER_JUMPING|Box2dVars.BLOCK|Box2dVars.OBJECT|Box2dVars.POWER;
		isOn = getBool("startOn", mp);
		//get fan direction
		direction = getInt("dir", mp);
		power = Math.abs(getInt("power",mp));
		switches = getArray("switches", mp);
		objects = new Array<GameSprite>();
		snap = getBool("snap", mp);

		m_force = 3000f * (power/100);
		
		if(direction == UP || direction == DOWN )
			setSize(Box2dVars.PIXELS_PER_METER,Box2dVars.PIXELS_PER_METER*fan_len);
		else if (direction == RIGHT)
		{
			setSize(Box2dVars.PIXELS_PER_METER*fan_len,Box2dVars.PIXELS_PER_METER*3);
			m_offX = 0;
			m_offY = -Box2dVars.PIXELS_PER_METER;			
		} else if (direction == LEFT)
		{
			setSize(Box2dVars.PIXELS_PER_METER*fan_len,Box2dVars.PIXELS_PER_METER*3);
			m_offX = -this.getWidth() + Box2dVars.PIXELS_PER_METER;
			m_offY = -Box2dVars.PIXELS_PER_METER;
		}

		m_fanSprite = new GameSprite(textures.findRegion("fan_F1"));
		m_rotateAnimation = new AnimateRotateTo(0.25f, 0f, 359.9f, -1);
		m_textures = textures;
		
	}

	public void startFanSound()
	{
		if (m_soundId < 0)
		{
			m_soundId = loopSound("fan",0.9f);
		}
	}

	public void stopFanSound()
	{
		if (m_soundId >= 0)
		{
			stopSound("fan", m_soundId);
			m_soundId = -1;
		}
	}

	public void addFanSprite(GameLayer l, float xx, float yy)
	{
		l.add(m_fanSprite);
		m_fanSprite.setPosition(xx,yy);
		m_particleSystem = new GameParticleSystem(l, m_textures, "fan_particle", 25, direction, 5, 55,0.13f,0.0025f);
		if (direction == RIGHT)
		{
			m_particleSystem.setLocation(xx + 34,yy-28,1,88);
		} else
		{
			m_particleSystem.setLocation(xx -2,yy-28,1,88);
		}
		
	}

	public void setBodyPosition(float xx, float yy)
	{
		xx += m_offX;
		yy += m_offY;

		if (m_offX == 0)
		{
			m_fx = xx;
		} else
		{
			m_fx = xx + this.getWidth();
		}

		super.setBodyPosition(xx,yy);
	}
	
	@Override
	public void update(float deltaTime) {
		if(isOn) {
			startFanSound();
			if (m_rotateAnimation.isRunning() == false) m_fanSprite.runAnimation(m_rotateAnimation);
			m_particleSystem.update(deltaTime);
			for(GameSprite o: objects) {
				if(o instanceof PowerUnit) {
					if(direction == RIGHT) {
						float dx = (o.getX() - m_fx)/Box2dVars.PIXELS_PER_METER;
						o.m_body.applyForceToCenter((m_force/25) / (dx*dx),0,true);
					}else if(direction == LEFT) {
						//float dx = (o.getX() - m_fx)/Box2dVars.PIXELS_PER_METER;
						//o.m_body.applyForceToCenter(-m_force / (dx*dx),0,true);
						float dx = (o.getX() - m_fx)/(Box2dVars.PIXELS_PER_METER*2);
						o.m_body.applyForceToCenter(-(m_force/25) / (dx*dx),0,true);
					}
				} else if (o instanceof Player)
				{
					if (((Player)o).m_state < 9)
					{
						if(direction == RIGHT) {
							if (((Player)o).checkDir(1) == false)
							{
								float dx = (o.getX() - m_fx)/Box2dVars.PIXELS_PER_METER;
								o.m_body.applyForceToCenter(m_force / (dx*dx),0,true);
							}
						}else if(direction == LEFT) {
							if (((Player)o).checkDir(0) == false)
							{
								float dx = (o.getX() - m_fx)/(Box2dVars.PIXELS_PER_METER*2);
								o.m_body.applyForceToCenter(-m_force / (dx*dx),0,true);
							}
						}	
					}				
					
				} else {
					if(direction == UP) {
						if(o.m_body.getLinearVelocity().y < 14)
							o.m_body.applyLinearImpulse(0,power, 0, 0, true);
					}else if(direction == DOWN) {
						if(o.m_body.getLinearVelocity().y > -14)
							o.m_body.applyLinearImpulse(0,-power, 0, 0, true);
					}else if(direction == RIGHT) {
						float dx = (o.getX() - m_fx)/Box2dVars.PIXELS_PER_METER;
						o.m_body.applyForceToCenter(m_force / (dx*dx),0,true);
					}else if(direction == LEFT) {
						//float dx = (o.getX() - m_fx)/Box2dVars.PIXELS_PER_METER;
						//o.m_body.applyForceToCenter(-m_force / (dx*dx),0,true);
						float dx = (o.getX() - m_fx)/(Box2dVars.PIXELS_PER_METER*2);
						o.m_body.applyForceToCenter(-m_force / (dx*dx),0,true);
					}
				}
					
				
			}
				
		} else
		{
			stopFanSound();
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
			objects.add(collision.target);
		}
	}

	@Override
	public void handleEnd(Box2dCollision collision) {
		if(collision.target instanceof GameSprite) {
			objects.removeValue(collision.target,true);
		}
	}

}
