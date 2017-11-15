package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;

public class PlatformStop extends GameMapObject {

	
  public void init(MapProperties mp, TextureAtlas textures)
  {
	 m_categoryBits = Box2dVars.PLATFORM_STOP;
	m_filterMask = Box2dVars.PLATFORM;
	m_btype = BodyType.DynamicBody;
    m_isSensor = false;
    m_sizeScale = 1.1f;
    m_gravityScale = 0;
    m_colBits = 8;
    this.setSize(Box2dVars.PIXELS_PER_METER,Box2dVars.PIXELS_PER_METER);
    
  }
  
  @Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		setBodyPosition(initx, inity);
	}
  
  //hack
  float initx = -1;
  float inity = -1;
  
  public void setBodyPosition(float xx, float yy)
  {
    this.setPosition(xx,yy);
    if(initx==-1) {
    	initx = xx;
    	inity = yy;
    }
    m_body.setTransform((xx + this.getWidth()/2)/Box2dVars.PIXELS_PER_METER , ((yy+1.4f) + this.getHeight()/2)/Box2dVars.PIXELS_PER_METER, this.getRotation()/180f * 3.14f);
  }

  @Override
  public void draw(SpriteBatch s)
  {
  }
}