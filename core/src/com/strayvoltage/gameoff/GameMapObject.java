package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.strayvoltage.gamelib.GameSprite;
import com.strayvoltage.gamelib.GameTileMap;

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
  //
  boolean m_isSensor = false;
  float m_restitution = 0.2f;
  float m_density = 1f;
  BodyType m_btype;
  
  public boolean m_triggered;

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

  public String getString(String key, MapProperties mp)
  {
    String s = (String)mp.get(key);
    return s;
  }
  
  public Array<String> getArray(String key,MapProperties mp){
	  Array<String> array = new Array<String>();
	  String s = (String) mp.get(key);//get array string
	  if(s!=null) {
		  if(s.contains(",")) {
			  String[] ss = s.split(",");//split values by ","
			  for (int i = 0; i < ss.length; i++) {
				 array.add(ss[i]);
			  }
		  }else {
			  array.add(s);
		  }
		 
	  }
	  return array;
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

  public void setPositionToBody(float ox, float oy)
  {
    float cx = m_body.getPosition().x*Box2dVars.PIXELS_PER_METER;
    float cy = m_body.getPosition().y*Box2dVars.PIXELS_PER_METER;
    this.setPosition(cx - this.getWidth()/2 + ox, cy - this.getHeight()/2 + oy);
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
    fixtureDef.friction = 0.5f;
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
  }
}
