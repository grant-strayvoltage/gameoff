package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;

public class PlatformStop extends GameMapObject {
    
  public void init(MapProperties mp, TextureAtlas textures)
  {
	 m_categoryBits = Box2dVars.PLATFORM_STOP;
	m_filterMask = Box2dVars.PLATFORM;
    m_hasPhysics = false;
    m_isSensor = true;
    m_colBits = 8;
    this.setSize(Box2dVars.PIXELS_PER_METER,Box2dVars.PIXELS_PER_METER);
  }

  @Override
  public void draw(SpriteBatch s)
  {
  }
}