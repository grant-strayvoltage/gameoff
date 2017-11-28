package com.strayvoltage.gameoff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class Platform extends GameMapObject implements Box2dCollisionHandler,SwitchHandler{

  boolean m_shapeHorizontal = false;
  boolean m_moveVertical = false;
  int m_triggerType = 0;

  int m_state = 0;
  int m_startDir = 1;
  int m_currDir = 1;
  int m_nextDir = 0;
  float m_dx = 0;
  float m_dy = 0;
  float m_accel = 0.025f;
  float m_maxSpeed = 1f;
  int m_ticks = 0;
  
  int direction = 0;
  float speed = 2;//meters per second
  float stop_elapsed = 0;
  float stop_duration = 3f;
  public boolean oneWay;
  
  Body sensor_bod;

  Array<String> switches;

  public void init(MapProperties mp, TextureAtlas textures)
  {
	oneWay = true;
	m_triggered = getBool("startOn", mp);
	m_btype = BodyType.KinematicBody;
	m_categoryBits = Box2dVars.PLATFORM;
	m_filterMask =Box2dVars.PLATFORM_STOP | Box2dVars.PLAYER_FOOT | Box2dVars.BRAIN_FOOT | Box2dVars.POWER | 
				  Box2dVars.FLOOR | Box2dVars.PLAYER_NORMAL | Box2dVars.BLOCK;
    m_gravityScale = 0;
    m_colBits = 3;
    m_sizeScale = 0.9f;
    TextureRegion texture = null;
    m_restitution = 0;
    m_density = 500f;
    if (getBool("shapeVertical",mp) == false)
    {
        //Horizontal shaped platform
        m_shapeHorizontal = true;
        //TODO: based on size, set width to 3, 4, or 5.
        texture = textures.findRegion("platform_3w");
        this.setRegion(texture);
    } else
    {
        //Vertical shaped platform
        m_shapeHorizontal = false;
        //TODO: based on size, set height to 3, 4, or 5.
        texture = textures.findRegion("platform_3v");
        this.setRegion(texture);
    }

    if (getBool("moveHorizontal",mp))
      m_moveVertical = false;
    else
     m_moveVertical = true;

    direction = getInt("startDir",mp);

    this.setSize(texture.getRegionWidth(),texture.getRegionHeight());

    Gdx.app.log("Platform", "m_moveVertical = " + m_moveVertical);

    //TODO: setup movement triggers
    //on contact, timed (default), or switch based

    //then override update, to handle movement, etc. as required.
    switches = getArray("switches", mp);
    //check if one way
    if(mp.containsKey("oneWay")) {
    	oneWay = getBool("oneWay", mp);
    }
    
    if(mp.containsKey("speed")) {
    	speed = mp.get("speed", Float.class);
    }
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
    rect.setAsBox(this.getWidth()/(Box2dVars.PIXELS_PER_METER*2) * m_sizeScale, this.getHeight()/(Box2dVars.PIXELS_PER_METER*2) * m_sizeScale);
    fixtureDef.shape = rect;

    fixtureDef.density = m_density;
    fixtureDef.friction = 1f;
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
  
  public void setBodyPosition(float xx, float yy)
  {
    this.setPosition(xx,yy);
    m_body.setTransform((xx + this.getWidth()/2)/Box2dVars.PIXELS_PER_METER , (yy + this.getHeight()/2)/Box2dVars.PIXELS_PER_METER, this.getRotation()/180f * 3.14f);
    if(sensor_bod!=null)sensor_bod.setTransform((xx + this.getWidth()/2)/Box2dVars.PIXELS_PER_METER , (yy + this.getHeight()/2)/Box2dVars.PIXELS_PER_METER, this.getRotation()/180f * 3.14f);
  }


  public void update(float deltaTime)
  {
    if (m_triggered)
    {
      if (m_state == 0)
      {
        //moving
        if (m_moveVertical) 
        	m_body.setLinearVelocity(0,direction*speed);
        else
        	m_body.setLinearVelocity(direction*speed, 0);
        
      } else if (m_state == 1)
      {
    	//stop
    	  stop_elapsed+=deltaTime;
    	  if(stop_elapsed>=stop_duration) {
    		  stop_elapsed = 0;
    		  m_state = 0;
    	  }
    	  
      }
    }
    
    setPositionToBody();
  }
  
  public void setPositionToBody()
  {
	if(sensor_bod!=null)sensor_bod.setTransform(m_body.getPosition(), sensor_bod.getAngle());
    float cx = m_body.getPosition().x*Box2dVars.PIXELS_PER_METER;
    float cy = m_body.getPosition().y*Box2dVars.PIXELS_PER_METER;
    
    this.setPosition(cx - this.getWidth()/2, cy - this.getHeight()/2);
  }

	@Override
	public void handleBegin(Box2dCollision collision) {
	
		if(collision.target_type == Box2dVars.PLATFORM_STOP) {
			direction=-direction;
			m_state = 1;
			m_body.setLinearVelocity(0, 0);
		}else {
			System.out.println("collision with :"+collision.target_type);
		}
		
		
	}

	@Override
	public void handleEnd(Box2dCollision collision) {
	}

	@Override
	public void handleSwitch(Switch source) {
		//Gdx.app.log("Platform-switch-test","switch registered :"+source.name);
		if(switches.contains(source.name, false)) {
			m_body.setLinearVelocity(0, 0);
			m_state = 0;
			m_triggered = !m_triggered;
		}
		
	}
}