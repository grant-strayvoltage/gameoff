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

public class PlatformStop extends GameMapObject {
    
  public void init(MapProperties mp, TextureAtlas textures)
  {
	 m_categoryBits = Box2dVars.PLATFORM_STOP;
	m_filterMask = Box2dVars.PLATFORM;
    m_hasPhysics = false;
    m_colBits = 8;
    this.setSize(Box2dVars.PIXELS_PER_METER,Box2dVars.PIXELS_PER_METER);
  }

  @Override
  public void draw(SpriteBatch s)
  {
  }
}