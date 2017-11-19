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
	
	static final int UP = 1;
	static final int DOWN = -1;
	
	//is this gate open
	boolean isOpen;
	//does this gate open instantly or do a moving animation
	boolean instant;
	//current state of this gate
	int state;
	//move speed
	float open_speed;
	//switch filters
	Array<String> switches;
	//max duration moving
	float move_duration;
	//elapsed time in moving state
	float moving_elapsed;
	
	int direction;
	//magic number
	float startx = -1;
	float starty = -1;

	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		if(getBool("hazard", mp)) 
			m_categoryBits = Box2dVars.HAZARD;
		else
			m_categoryBits = Box2dVars.OBJECT;
		open_speed = 100;
		m_filterMask = Box2dVars.PLAYER_NORMAL|Box2dVars.POWER|Box2dVars.OBJECT|Box2dVars.BLOCK;
		m_btype = BodyType.KinematicBody;
		setSize(Box2dVars.PIXELS_PER_METER,Box2dVars.PIXELS_PER_METER*3);
		instant = getBool("instant", mp);
		isOpen = getBool("isOpen",mp);
		direction = UP;
		switches = getArray("switches", mp);
		if(getBool("openDown", mp)) {
			direction = DOWN;
		}
	}
	
	@Override
	public void update(float deltaTime) {
		if(state==MOVING) {
			System.out.println("moving gate");
			if(isOpen) {//OPEN GATE
				setBodyPosition(getX(), getY()+(direction*(open_speed*deltaTime)));
				if(direction == DOWN) {
					if(getY() <= starty-getHeight()) {
						setBodyPosition(getX(), starty-getHeight());
						state = OPEN;
					}
				}else {
					if(getY() >= starty+getHeight()) {
						setBodyPosition(getX(), starty+getHeight());
						state = OPEN;
					}
				}
			}else {
				setBodyPosition(getX(), getY()-(direction*(open_speed*deltaTime)));
				if(direction == DOWN) {
					if(getY() >= starty) {
						setBodyPosition(getX(), starty);
						state = OPEN;
					}
				}else {
					if(getY() <= starty) {
						setBodyPosition(getX(), starty);
						state = OPEN;
					}
				}
			}
		}
		super.update(deltaTime);
	}
	
	public void open() {
		if(instant) {
			state = OPEN;
			m_body.setTransform(m_body.getPosition().x,m_body.getPosition().y+(direction*getHeight()/Box2dVars.PIXELS_PER_METER), m_body.getAngle());
		}else {
			state = MOVING;
		}
		isOpen = true;
	}
	
	public void close() {
		if(instant) {
			state = CLOSED;
			m_body.setTransform(m_body.getPosition().x,m_body.getPosition().y-(direction*getHeight()/Box2dVars.PIXELS_PER_METER), m_body.getAngle());
		}else {
			state = MOVING;
		}
		isOpen = false;
	}
	
	@Override
	public void setBodyPosition(float xx, float yy) {
		if(startx == -1 && starty == -1) {
			startx = xx;
			starty = yy;
		}
		super.setBodyPosition(xx, yy);
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
		if(switches.contains(source.name, false)) {
			if(isOpen)
				close();
			else
				open();
		}
	}

	

}
