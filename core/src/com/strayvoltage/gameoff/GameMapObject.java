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
import com.strayvoltage.gamelib.*;

public abstract class GameMapObject extends GameSprite {

  GameTileMap m_map;
  public byte m_colBits;
  float m_dx, m_dy;
  boolean m_noDrag = false;
  float m_dragX = 0.1f;
  boolean m_onGround = true;
 float m_gravity = -0.2f;

  public GameMapObject()
  {
    super();
    m_colBits = 0;
  }

  public void setMap(GameTileMap map)
  {
    m_map = map;
  }

  public abstract void init(MapProperties mp, TextureAtlas textures);

  public boolean getBool(String key, MapProperties mp)
  {
    //if missing, or the default value from Tiled, assume false.
    //If you don't want false as the default value, set the value explicitly in Tiled for this object.
    Boolean b = (Boolean)mp.get(key);
    if (b == null) return false;
    return b.booleanValue();
  }

  public int getInt(String key, MapProperties mp)
  {
    //if missing, or the default value from Tiled, assume 1
    //If you don't want false as the default value, set the value explicitly in Tiled for this object.
    Integer i = (Integer)mp.get(key);
    if (i == null) return 1;
    return i.intValue();
  }

  public byte colBits()
  {
    return m_colBits;
  }

  public byte getCollisionBitsAt(float xx, float yy)
  {
    return m_map.getCollisionBitsAt(xx,yy);
  }

  public void updatePush(float dx)
  {

  }

  public GameMapObject checkObjectCollisions(float xx, float yy, int bits)
  {
    float oldx = this.getX();
    float oldy = this.getY();
    this.setPosition(xx,yy);
    MainLayer m = (MainLayer) getParent();
    for (GameMapObject o : m.m_gameMapObjects)
    {
      //todo: might need to add players here?
      if (o != this)
      {
        if ((o.colBits() & bits) > 0)
        {
          if (Intersector.overlaps(this.getBoundingRectangle(), o.getBoundingRectangle()))
          {
            this.setPosition(oldx,oldy);
            return o;
          }
        }
      }
    }

    this.setPosition(oldx,oldy);
    return null;
  }

  public GameMapObject checkObjectCollisions(float xx, float yy)
  {
    return checkObjectCollisions(xx,yy,3);
  }

  public boolean isSolid(float xx, float yy)
  {
    int v = (m_map.getCollisionBitsAt(xx,yy) & 7);
    //Gdx.app.log("GameMapObject", "isSolid " + xx + ", " + yy + " = " + v);
    if (v > 0) return true;
    return false;
  }

  public void handlePhysics()
  {
    //call this from update if you want this object to fall, have drag, etc. like moveable blocks
    
    //drag
    if (m_noDrag == false)
    {
      if (m_dx > 0)
      {
        m_dx -= m_dragX;
        if (m_dx < 0) m_dx = 0;
      } else if (m_dx < 0)
      {
        m_dx += m_dragX;
        if (m_dx > 0) m_dx = 0;
      }
    }

    float x = this.getX() + m_dx;
    byte b = 0;
    if (m_dx < 0)
    {
      //going left
      b = m_map.getCollisionBitsAt(x, this.getY());
      b = (byte) (b | m_map.getCollisionBitsAt(x,this.getY() + this.getHeight() - 6));
    } else if (m_dx > 0)
    {
      //going right
      b = m_map.getCollisionBitsAt(x+this.getWidth(),this.getY());
      b = (byte) (b | m_map.getCollisionBitsAt(x+this.getWidth(),this.getY() + this.getHeight() - 6));
    }

    if (b > 0)
    {
      x -= m_dx;
      m_dx = 0;
    }

    if (m_dx != 0)
    {
      GameMapObject o = checkObjectCollisions(x,this.getY());
      if (o != null)
      {
        if (m_dx > 0)
        {
          x = o.getX() - this.getWidth();
          o.updatePush(m_dx);
        }
        else if (m_dx < 0)
        {
          x = o.getX() + o.getWidth();
          o.updatePush(m_dx);
        }
        //m_dx= 0;
      }
    }

    this.setPosition(x,this.getY());

    float y = this.getY();
    m_dy += m_gravity;
    if (m_dy < -4) m_dy = -4;
    y += m_dy;
    if (m_dy < 0)
    {
      b = (byte) (m_map.getCollisionBitsAt(this.getX()+this.getWidth()/2,y));
      if (b > 0)
      {
        int ty = (int) (y / 16f);
        y = (ty + 1) * 16f;
        m_onGround = true;
        m_dy = 0;
      } else{
        m_onGround = false;
      }

      GameMapObject o = checkObjectCollisions(this.getX(),y);
      if (o != null)
      {
        //o is the GameMapObject I'm colliding with below me
        m_onGround = true;
        m_dy = 0;
        y = (float)java.lang.Math.ceil(o.getY() + o.getHeight());
      }
    } else if (m_dy > 0)
    {
      b = (byte) (m_map.getCollisionBitsAt(this.getX()+this.getWidth()/2,y+this.getHeight()));
      if (b > 0)
      {
        int ty = (int) (y / 16f);
        y = (ty) * 16f;
        m_onGround = false;
        m_dy = 0;
      }

      GameMapObject o = checkObjectCollisions(this.getX(),y);
      if (o != null)
      {
        //o is the GameMapObject I'm colliding with above me
        if (o.getY() > y)
        {
          m_dy = 0f;
          m_onGround = false;
          y = o.getY() - this.getHeight() - 1;
        }
      }
    }

    this.setPosition(this.getX(),y);

    //reset for next update
    m_noDrag = false;
  }
}

