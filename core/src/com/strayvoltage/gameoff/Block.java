package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapProperties;

public class Block extends GameMapObject {

  public void init(MapProperties mp, TextureAtlas textures)
  {
	m_categoryBits = Box2dVars.BLOCK;
	m_filterMask = Box2dVars.BLOCK | Box2dVars.PLAYER_FOOT | Box2dVars.PLATFORM | Box2dVars.PLAYER_NORMAL | Box2dVars.PLAYER_JUMPING | Box2dVars.PLATFORM_STOP| Box2dVars.FLOOR;
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