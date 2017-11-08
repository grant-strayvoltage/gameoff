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

  int m_pushState = 0;
  int m_pushTicks = 0;
  int m_lastPushTicks = 0;
  int m_pushDir = 0;
  float m_pushDx = 0;

  public void init(MapProperties mp, TextureAtlas textures)
  {
    m_density = 1.5f;
    m_colBits = 3;
    TextureRegion texture = null;
    texture = textures.findRegion("block1_moveable");
    this.setRegion(texture);
    this.setSize(texture.getRegionWidth(),texture.getRegionHeight());
  }

  public void updatePush(float dx)
  {
      if (m_pushState == 0)
      {
          //first contact
          m_pushState = 1;
          m_pushTicks = 1;
          m_lastPushTicks = 0;
          m_pushDx = dx;
      } else
      {
        m_pushTicks++;
        m_pushDx = dx;
      } 
  }

  public void update(float deltaTime)
  {
      if (m_pushState > 1)
      {
          if (m_pushTicks != m_lastPushTicks)
          {
              m_lastPushTicks = m_pushTicks;
          } else
          {
              //push over
              m_pushState = 0;
          }
      }

      if (m_pushState == 1)
      {
        if (m_pushTicks > 20)
        {
            m_pushState = 2;
        }
      } else if (m_pushState == 2)
      {
          m_dx = m_pushDx;
      }

      this.setPositionToBody();

      //handlePhysics();
  }
}