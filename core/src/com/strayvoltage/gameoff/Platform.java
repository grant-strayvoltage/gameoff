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

public class Platform extends GameMapObject {

  boolean m_shapeHorizontal = false;
  boolean m_moveVertical = false;
  boolean m_triggered = true;
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

 

  public void init(MapProperties mp, TextureAtlas textures)
  {
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

    m_startDir = getInt("startDir",mp);
    m_currDir = m_startDir;

    this.setSize(texture.getRegionWidth(),texture.getRegionHeight());

    Gdx.app.log("Platform", "m_moveVertical = " + m_moveVertical);

    //TODO: setup movement triggers
    //on contact, timed (default), or switch based

    //then override update, to handle movement, etc. as required.

  }

  public void moveVertical()
  {
    float ly = m_body.getLinearVelocity().y;
    if (Math.abs(ly) < 3) m_body.applyForceToCenter(0,m_currDir*8*m_density,true);
    m_body.setLinearVelocity(m_body.getLinearVelocity().clamp(0,3));
    
    float y = this.getY();

    float yCheck = y;
    if (m_currDir > 0) yCheck += this.getHeight();

    //check if near tile and should slow down.
    if (isSolid(this.getX() + this.getWidth()/2, yCheck + (m_currDir * 20)))
    {
      m_state += 1;
      m_currDir = -m_currDir;
    } else
    {
      GameMapObject o = checkObjectCollisions(this.getX(),y + (m_currDir*21), 8);
      if (o != null)
      {
        m_state += 1;
        m_currDir = -m_currDir;
      }
    }

  }

  public void moveHorizontal()
  {

  }

  public void slowVertical()
  {
    m_body.applyForceToCenter(0,m_currDir*8f*m_density,true);

    if((m_body.getLinearVelocity().y < 0.25f) && (m_body.getLinearVelocity().y > -0.25f))
    {
      m_body.setLinearVelocity(0,0);
      m_state += 1;
      m_ticks = 160;
    }
  }

  public void slowHorizontal()
  {

  }

  public void update(float deltaTime)
  {
    if (m_triggered)
    {
      if (m_state == 0)
      {
        //moving
        if (m_moveVertical) moveVertical();
        else moveHorizontal();
      } else if (m_state == 1)
      {
        //slowing down
        if (m_moveVertical) slowVertical();
        else slowHorizontal();
      } else if (m_state == 2)
      {
        //stopped and waiting to move 
        m_ticks--;
        if (m_ticks < 1)
        {
          m_state = 0;
        }
      }
    }
  
    setPositionToBody();
  }
}