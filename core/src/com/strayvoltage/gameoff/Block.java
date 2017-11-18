package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;

public class Block extends GameMapObject {

  public void init(MapProperties mp, TextureAtlas textures)
  {
	m_categoryBits = Box2dVars.BLOCK;
	m_filterMask =Box2dVars.SWITCH | Box2dVars.OBJECT| Box2dVars.BLOCK | Box2dVars.PLAYER_FOOT | 
					Box2dVars.POWER | Box2dVars.BRAIN_FOOT | Box2dVars.PLATFORM | Box2dVars.PLAYER_NORMAL | 
					Box2dVars.PLAYER_JUMPING | Box2dVars.PLATFORM_STOP| Box2dVars.FLOOR|Box2dVars.HAZARD;
    m_density = 1.5f;
    m_colBits = 3;
    TextureRegion texture = null;
    texture = textures.findRegion("block1_moveable");
    this.setRegion(texture);
    this.setSize(texture.getRegionWidth(),texture.getRegionHeight());
  }

  public void update(float deltaTime)
  {
      this.setPositionToBody();
  }

}