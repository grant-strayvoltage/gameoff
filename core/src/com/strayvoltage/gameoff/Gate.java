package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

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
	boolean horizontal;
	//magic number
	float startx = -1;
	float starty = -1;
	int m_animTick = 0;

	private float offset = 5;
	private TextureRegion m_region = null;

	int m_tileHeight;
	int m_tileWidth;

	@Override
	public void init(MapProperties mp, TextureAtlas textures) {

		boolean haz = getBool("hazard", mp);

		if(haz) 
			m_categoryBits = Box2dVars.HAZARD;
		else
			m_categoryBits = Box2dVars.OBJECT;
		open_speed = 100;
		m_filterMask = Box2dVars.PLAYER_NORMAL|Box2dVars.POWER|Box2dVars.OBJECT|Box2dVars.BLOCK;
		m_btype = BodyType.KinematicBody;
		setSize(mp.get("width", Float.class),mp.get("height",Float.class));
		instant = getBool("instant", mp);
		isOpen = getBool("isOpen",mp);
		direction = UP;
		switches = getArray("switches", mp);
		if(getBool("openDown", mp)) {
			direction = DOWN;
		}
		horizontal = getBool("horizontal", mp);


		m_tileHeight = (int) (this.getHeight()/Box2dVars.PIXELS_PER_METER);
		m_tileWidth = (int) (this.getWidth()/Box2dVars.PIXELS_PER_METER);

		if (haz)
		{
			if (m_tileHeight > m_tileWidth)
				m_region = textures.findRegion("gate_v_haz");
			else
				m_region = textures.findRegion("gate_h_haz");
		} else
		{
			if (m_tileHeight > m_tileWidth)
				m_region = textures.findRegion("gate_v");
			else
				m_region = textures.findRegion("gate_h");			
		}
	}
	
	@Override
	public void update(float deltaTime) {
		if(state==MOVING) {
			if(horizontal) {
				if(isOpen) {//OPEN GATE
					setBodyPosition(getX()+(direction*(open_speed*deltaTime)), getY());
					if(direction == DOWN) {
						if(getX() <= startx-(getWidth()+offset)) {
							setBodyPosition(startx-(getWidth()+offset), getY());
							state = OPEN;
						}
					}else {
						if(getX() >= startx+getWidth()+offset) {
							setBodyPosition(startx+getWidth()+offset, getY());
							state = OPEN;
						}
					}
				}else {
					setBodyPosition(getX()-(direction*(open_speed*deltaTime)), getY());
					if(direction == DOWN) {
						if(getX() >= startx) {
							setBodyPosition(startx, getY());
							state = OPEN;
						}
					}else {
						if(getX() <= startx) {
							setBodyPosition(startx, getY());
							state = OPEN;
						}
					}
				}
			}else {
				if(isOpen) {//OPEN GATE
					setBodyPosition(getX(), getY()+(direction*(open_speed*deltaTime)));
					if(direction == DOWN) {
						if(getY() <= starty-(getHeight()+offset)) {
							setBodyPosition(getX(), starty-(getHeight()+offset));
							state = OPEN;
						}
					}else {
						if(getY() >= starty+getHeight()) {
							setBodyPosition(getX(), starty+getHeight()+offset);
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
			
		}
		super.update(deltaTime);
	}
	
	public void open() {
		if(instant) {
			state = OPEN;
			isOpen = true;
			if(horizontal) {
				if(direction == DOWN)
					setBodyPosition(startx-(getWidth()+offset), getY());
				else
					setBodyPosition(startx+getWidth()+offset, getY());
			}else {
				if(direction == DOWN)
					setBodyPosition(getX(), starty-(getHeight()+offset));
				else
					setBodyPosition(getX(), starty+getHeight()+offset);
			}
			
		}else {
			state = MOVING;
		}
		isOpen = true;
	}
	
	public void close() {
		if(instant) {
			state = CLOSED;
			isOpen = false;
			setBodyPosition(startx, startx);
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

		float xx = this.getX();
		float yy = this.getY();
		for (int ix = 0; ix < m_tileWidth; ix++)
		{
			for (int iy = 0; iy < m_tileHeight; iy++)
			{
				batch.draw(m_region,xx + ix *Box2dVars.PIXELS_PER_METER, yy + iy *Box2dVars.PIXELS_PER_METER);
			}
		}
	}

	public void drawShape()
	{
		//shapeRenderer.begin(ShapeType.Line);
		//shapeRenderer.setColor(1, 0, 0, 1);
		//shapeRenderer.rect(this.getX(), this.getY(), this.getWidth(), this.getHeight());
		//shapeRenderer.end();
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
