package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Switch extends GameMapObject implements Box2dCollisionHandler{
	
	public SwitchAdapter adapter;
	//can this switch be toggled on and off - if false it can only be turned on.
	public boolean toggle;
	//is this switch pressure sensitive? if yes then it requires a player to stay on. overrides toggle
	public boolean sensitive;
	//name of the switch (requires unique switch names)
	public String name;
	//check if switch is on
	public boolean is_On;
	//number of contacts used to track how many players are touching the switch
	//very useful to determine if the switch is already on to prevent other player
	//from also triggering the switch
	int contacts;
	//check to see if this switch has already been triggered atleast once
	public boolean first_trigger;
	TextureRegion on;
	TextureRegion off;
	
	@Override
	public void handleBegin(Box2dCollision collision) {
		System.out.println("player touched switch");
		contacts++;
		if(contacts==1) {
			if(toggle)
				if(is_On)
				{
					setOff();
					playSound("switchToggle",1f);
				}
				else
				{
					setOn();
					playSound("switchToggle",1f);
				}

			else if(!is_On && !first_trigger) 
			{
				setOn();
				playSound("switchToggle",1f);
			}
		}
	}

	@Override
	public void handleEnd(Box2dCollision collision) {
		contacts--;
		if(sensitive&&contacts==0) {
			playSound("switchToggle",1f);			
			setOff();
		}
	}

	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		toggle = getBool("toggle", mp);
		sensitive = getBool("sensitive",mp);
		m_visible = getBool("visible",mp);
		is_On = getBool("startOn",mp);
		contacts = 0;
		m_isSensor = true;

		if (sensitive)
		{
			on = textures.findRegion("switch2_on");
			off = textures.findRegion("switch2_off");	
		} else
		{
			on = textures.findRegion("switch_on");
			off = textures.findRegion("switch_off");
		}
		
		
		m_btype = BodyType.StaticBody;
		m_categoryBits = Box2dVars.SWITCH;
		m_filterMask = Box2dVars.PLAYER_NORMAL|Box2dVars.PLAYER_JUMPING|Box2dVars.BLOCK|Box2dVars.OBJECT|Box2dVars.ENEMY;
		
		if(is_On)
			setOn();
		else
			setOff();
		
		setSize(Box2dVars.PIXELS_PER_METER, Box2dVars.PIXELS_PER_METER);
		
	}	
	
	public void setOn() {
		if(!first_trigger)
			first_trigger = true;
		if(!is_On) {
			is_On = true;
			adapter.trigger(this);
		}
		setRegion(this.on);
		
		
	}
	
	public void setOff() {
		setRegion(this.off);
		if(is_On){
			is_On = false;
			adapter.trigger(this);
		}
			
		
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
	}
	

}
