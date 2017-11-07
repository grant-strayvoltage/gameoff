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
    m_colBits = 3;
    TextureRegion texture = null;
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
    //Gdx.app.log("Platform", "moveVertical currDir= " + m_currDir + " : " + m_dy);
    m_dy += (m_currDir * m_accel);
    if (m_dy > m_maxSpeed) m_dy = m_maxSpeed;
    if (m_dy < (-m_maxSpeed)) m_dy = - m_maxSpeed;

    float y = this.getY() + m_dy;

    float yCheck = y;
    if (m_dy > 0) yCheck += this.getHeight();

    //check if near tile and should slow down.
    if (isSolid(this.getX() + this.getWidth()/2, yCheck + (m_currDir * 20)))
    {
      m_state += 1;
      m_currDir = -m_currDir;
    } else
    {
      GameMapObject o = checkObjectCollisions(this.getX(),y + (m_currDir*20), 8);
      if (o != null)
      {
        m_state += 1;
        m_currDir = -m_currDir;
      }
    }
      

    this.setPosition(this.getX(),y);
  }

  public void moveHorizontal()
  {

  }

  public void slowVertical()
  {
    //Gdx.app.log("Platform", "slowVertical currDir= " + m_currDir + " : " + m_dy);
    m_dy += (m_currDir * m_accel);
    float y = this.getY() + m_dy;

    if ((m_dy < 0.01f) && (m_dy > -0.01f))
    {
      m_dy = 0;
      m_state += 1;
      m_ticks = 180;
      y = (float)java.lang.Math.ceil(this.getY());
    }

    this.setPosition(this.getX(),y);

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
  }
}