package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class Trampoline extends GameMapObject implements Box2dCollisionHandler{

	public static final int NONE = -1;
	public static final int UP = 0;
	public static final int DOWN = 1;
	
	public static float Bounce_Interval = .2f;
	
	int state = 0;
	float down_elapsed;
	
	TextureRegion up;
	TextureRegion down;
	
	int current_state;
	
	//can handle players 
	Player[] players;
	PowerUnit brain = null;
	
	@Override
	public void init(MapProperties mp, TextureAtlas textures) {
		m_isSensor = false;
		m_btype = BodyType.DynamicBody;
		m_filterMask = Box2dVars.BLOCK | Box2dVars.PLAYER_FOOT | Box2dVars.FLOOR | Box2dVars.PLAYER_NORMAL | Box2dVars.PLAYER_JUMPING | Box2dVars.BRAIN_FOOT;
		m_categoryBits = Box2dVars.OBJECT;
	    up = textures.findRegion("tramp_up");
	    down = textures.findRegion("tramp_down");
	    this.setRegion(up);
		setSize(Box2dVars.PIXELS_PER_METER, Box2dVars.PIXELS_PER_METER);
		current_state = UP;
		players = new Player[2];

		if (getBool("static",mp) == true)
		{
			m_btype = BodyType.StaticBody;
		}
	}
	

	@Override
	public void handleBegin(Box2dCollision collision) {
		if(collision.target_type == Box2dVars.PLAYER_FOOT) {
			current_state = DOWN;
			setRegion(down);
			Player p = (Player) collision.target;
			p.trampoline_state = DOWN;
			if(players[0] == null)
				players[0] = p;
			else
				players[1] = p;
		} else if(collision.target_type == Box2dVars.BRAIN_FOOT) {
			current_state = DOWN;
			setRegion(down);
			brain = (PowerUnit) collision.target;
			brain.trampoline_state = DOWN;
		}
	}

	@Override
	public void handleEnd(Box2dCollision collision) {
		if(collision.target_type == Box2dVars.PLAYER_FOOT) {
			
			Player p = (Player) collision.target;
			p.trampoline_state = NONE;
			if(players[0] == p)
				players[0] = null;
			else
				players[1] = null;
			if(players[0] == null && players[1] == null) {
				current_state = UP;
				setRegion(up);
			}
		} else if(collision.target_type == Box2dVars.BRAIN_FOOT) {
			
			PowerUnit brain = (PowerUnit)collision.target;
			brain.trampoline_state = NONE;
			brain = null;
			if(players[0] == null && players[1] == null) {
				current_state = UP;
				setRegion(up);
			}
		}
	}
	
	@Override
	public void update(float deltaTime) {
		if(current_state == DOWN) {
			down_elapsed+=deltaTime;
			if(down_elapsed>=Bounce_Interval) {
				down_elapsed = 0;
				for (int i = 0; i < players.length; i++) {
					if(players[i]!=null) {
						players[i].trampoline_state = NONE;
						players[i].jump();
					}
						
				}
				if (brain != null) brain.jump();
			}
			
		}
		this.setPositionToBody();
		super.update(deltaTime);
	}

	public void setBodyPosition(float xx, float yy)
  	{
    	this.setPosition(xx,yy);
    	m_body.setTransform((xx + this.getWidth()/2)/Box2dVars.PIXELS_PER_METER , (yy + this.getHeight()/2)/Box2dVars.PIXELS_PER_METER - 0.25f, this.getRotation()/180f * 3.14f);
  	}
	
	public void setPositionToBody()
	  {
	    float cx = m_body.getPosition().x*Box2dVars.PIXELS_PER_METER;
	    float cy = m_body.getPosition().y*Box2dVars.PIXELS_PER_METER;
	    this.setPosition(cx - this.getWidth()/2, cy-this.getHeight()*.25f);
	  }
	
	public void addToWorld(World world)
	  {

	    BodyDef bodyDef = new BodyDef();
	    bodyDef.type = m_btype;
	    bodyDef.fixedRotation = true;

	    m_body = world.createBody(bodyDef);
	    m_body.setUserData(this);

	    FixtureDef fixtureDef = new FixtureDef();

	    PolygonShape rect = null;
	    rect = new PolygonShape();
	    rect.setAsBox(this.getWidth()/(Box2dVars.PIXELS_PER_METER*2) * m_sizeScale, this.getHeight()/(Box2dVars.PIXELS_PER_METER*2) *.5f);
	    fixtureDef.shape = rect;

	    fixtureDef.density = m_density;
	    fixtureDef.friction = 0.1f;
	    fixtureDef.restitution = m_restitution;
	    fixtureDef.isSensor = m_isSensor;
	    fixtureDef.filter.categoryBits = m_categoryBits;
	    fixtureDef.filter.maskBits = m_filterMask;

	    m_fixture = m_body.createFixture(fixtureDef);
	    m_fixture.setUserData(this);
	    m_body.setGravityScale(m_gravityScale);
	    rect.dispose();

	    if (m_hasPhysics == false)
	    {
	      m_body.setActive(false);
	    }
	  }

}
