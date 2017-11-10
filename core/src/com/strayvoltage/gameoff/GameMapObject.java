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
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.*;

public abstract class GameMapObject extends GameSprite {

  GameTileMap m_map;
  public byte m_colBits;
  float m_dx, m_dy;
  boolean m_noDrag = false;
  float m_dragX = 0.1f;
  boolean m_onGround = true;
  float m_gravity = -0.2f;
  Fixture m_fixture = null;
  short m_categoryBits = Box2dVars.OBJECT;
  short m_filterMask = 255;
  float m_gravityScale = 1f;
  float m_sizeScale = 1.0f;
  boolean m_hasPhysics = true;
  float m_restitution = 0.2f;
  float m_density = 1f;
  BodyType m_btype;

  public GameMapObject()
  {
    super();
    m_colBits = 0;
    m_btype = BodyType.DynamicBody;
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

  public GameMapObject checkObjectCollisions(float xx, float yy, int bits)
  {
    float oldx = this.getX();
    float oldy = this.getY();
    this.setPosition(xx,yy);
    MainLayer m = (MainLayer) getParent();
    for (GameMapObject o : m.m_gameMapObjects)
    {
      //TODO: might need to add players here?
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

  public void setPositionToBody()
  {
    float cx = m_body.getPosition().x*Box2dVars.PIXELS_PER_METER;
    float cy = m_body.getPosition().y*Box2dVars.PIXELS_PER_METER;
    this.setPosition(cx - this.getWidth()/2, cy - this.getHeight()/2);
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
    fixtureDef.friction = 0.1f;
    fixtureDef.restitution = m_restitution;

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
  }
}
