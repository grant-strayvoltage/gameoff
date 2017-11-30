package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.strayvoltage.gamelib.*;

public class Smasher extends GameMapObject implements SwitchHandler,Box2dCollisionHandler{

	
	private static final int WAITING = 0; //smasher waiting to be triggerd;
	private static final int FALLING = 1; //smasher is falling down and accelerating
	private static final int GROUNDED = 2;//smasher has hit the ground and is going to start heading up
	private static final int RISING = 3; //smasher is now slowly returning back to its initial state. 
	private static final int SHAKING = 4; //smasher is shaking just before dropping
	
	int active_detections; //how many switches are currently active 
	int current_state = WAITING;		//the current sate of this smasher
	
	float riseSpeed;		//speed at which the grounder rises back to its original position(until it hits solid object/floor)
	float acceleration;		//acceleration at which the smasher falls
	
	Array<String> switches;
	
	float grounded_elapsed; //time that the smasher has been on the ground
	float ground_time;		//time that the smasher should remain on the ground
	
//	boolean spiked;			//if this smasher is spiked then any contact with player will cause death
//							//otherwise only when falling on top of the player will this cause death

	GameAnimateable m_fallingAnimation;
	GameAnimateable m_restingAnimation;
	float m_offX = 0;
	float m_offY = 0;
	int m_state = 0;
	int m_ticks = 0;
	

	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		active_detections = 0;
		switches = getArray("switches", mp);
		m_btype = BodyType.KinematicBody;
		m_categoryBits = Box2dVars.OBJECT;
		m_filterMask = Box2dVars.PLAYER_NORMAL|Box2dVars.POWER|Box2dVars.PLATFORM_STOP|Box2dVars.PLAYER_FOOT;
		//setSize(mp.get("width", Float.class),mp.get("height",Float.class));
		riseSpeed = mp.get("riseSpeed",.8f,Float.class);
		acceleration = mp.get("acceleration",1f,Float.class);
		grounded_elapsed = 0f;
		ground_time = mp.get("groundTime",1f,Float.class);
//		spiked = getBool("spiked", mp);

		TextureRegion texture = null;
		texture = textures.findRegion("smasher_F1");
		this.setRegion(texture);
		this.setSize(texture.getRegionWidth(),texture.getRegionHeight());
		
		m_fallingAnimation = new AnimateSpriteFrame(textures, new String[] {"smasher_F2"}, 0.5f, 1);
		m_restingAnimation = new AnimateSpriteFrame(textures, new String[] {"smasher_F1"}, 10f, -1);

	}
	
	
	@Override
	public void update(float deltaTime) {
		if(current_state == WAITING) {
			if (m_restingAnimation.isRunning() == false)
			{
				this.runAnimation(m_restingAnimation);
			}

			m_body.setLinearVelocity(0, 0);//should not be moving

			//wait for trigger
			if(isTriggered()) {
				current_state = SHAKING;
				m_ticks = 30;
				this.playSound("smasherDrop",1f);
			}
		}else if (current_state == SHAKING)
		{
			m_ticks--;
			m_offX = -2f + (float)Math.random()*4f;
			m_offY = -1f + (float)Math.random()*2f;
			if (m_ticks < 1)
			{
				current_state = FALLING;
				this.stopAllAnimations();
				this.runAnimation(m_fallingAnimation);
				m_offX = 0;
				m_offY = 0;
				this.playSound("smasherLoop",1f);
			}
		}else if(current_state == FALLING) {
			m_body.setLinearVelocity(0,m_body.getLinearVelocity().y-acceleration*deltaTime);
			
		}else if (current_state == GROUNDED) {
			
			m_body.setLinearVelocity(0, 0); //stop smasher
			//check ground time
			grounded_elapsed+=deltaTime;
			if(grounded_elapsed>= ground_time) {
				grounded_elapsed = 0;
				current_state = RISING;
				this.runAnimation(m_restingAnimation);

			}
			
		}else if(current_state == RISING) {
			m_body.setLinearVelocity(0,riseSpeed);
		}

		setPositionToBody(m_offX, m_offY);
		
		super.update(deltaTime);
	}
	
	@Override
	public void handleBegin(Box2dCollision collision) {
		if(collision.target_type == Box2dVars.PLAYER_NORMAL || collision.target_type == Box2dVars.POWER || collision.target_type == Box2dVars.PLAYER_FOOT) {
			//Kill the player or brain ----
			
			if(collision.target instanceof Player) {
				Player p = (Player) collision.target;
				p.die();
			}else {
				PowerUnit p = (PowerUnit) collision.target;
				p.die();
			}
			
			
		}else { //hit something else need to stop(most likely the floor)
			if(current_state == FALLING) {
				current_state = GROUNDED;
				this.playSound("boom",1f);
				//this.stopSound("smasherLoop");
			}
			if(current_state == RISING) {
				current_state = WAITING;
			}
		}
		
	}

	@Override
	public void handleEnd(Box2dCollision collision) {
		
	}

	@Override
	public void handleSwitch(Switch source) {
		if(switches.contains(source.name, false)) {
			//switch triggered
			if(source.is_On) {
				//switch was turned on
				active_detections++;
				
			}else {
				//switch was turned off
				active_detections--;
			}
		}
	}
	
	private boolean isTriggered() {
		return active_detections>=1;
	}


}
