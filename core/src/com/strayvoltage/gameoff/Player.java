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
import com.strayvoltage.gamelib.*;
import com.badlogic.gdx.math.Intersector;

public class Player extends GameSprite {

  GameInputManager2 m_controller;
  float m_dx = 0;
  float m_dy = 0;
  GameTileMap m_map = null;
  boolean m_powered = false;
  boolean m_playerControlled = false;
  Player m_otherPlayer = null;
  int m_controlDelayTicks = 5;
  float m_jumpDY = 10;
  float m_gravity = -0.2f;
  int m_jumpTicks = 0;
  boolean m_onGround = true;
  PowerUnit m_powerUnit = null;
  float m_lastDx = 1;
  boolean m_ownsPowerUnit = false;
  int m_firePressedTicks = 0;
  int m_throwDelayTicks = 60;

  public Player(TextureRegion texture, GameInputManager2 controller)
  {
    super(texture);
    m_controller = controller;
  }

  public void setMap(GameTileMap m, Player p, float jumpDY, PowerUnit pu)
  {
    m_map = m;
    m_otherPlayer = p;
    m_jumpDY = jumpDY;
    m_powerUnit = pu;
  }

  public GameMapObject checkObjectCollisions(float xx, float yy)
  {
    float oldx = this.getX();
    float oldy = this.getY();
    this.setPosition(xx,yy);
    MainLayer m = (MainLayer) getParent();
    for (GameMapObject o : m.m_gameMapObjects)
    {
      if ((o.colBits() & 3) > 0)
      {
        if (Intersector.overlaps(this.getBoundingRectangle(), o.getBoundingRectangle()))
        {
          this.setPosition(oldx,oldy);
          return o;
        }
      }
    }
    this.setPosition(oldx,oldy);
    return null;
  }

  public float dist(float xx, float yy)
  {
    float px = this.getX() + this.getWidth()/2;
    float py = this.getY() + this.getHeight()/2;
    return (float)Math.sqrt(Math.pow((double)(xx- px), 2) + Math.pow((double)(yy-py), 2));
  }

  public void update(float deltaTime)
  {
    if (m_playerControlled)
    {
      if (m_ownsPowerUnit == false)
      {
        m_powered = false;
        if (m_powerUnit.canPickUp())
        {
          if ((dist(m_powerUnit.getX(), m_powerUnit.getY())) < 100)
          {
            m_powered = true;
          }
        }
      }
    }

    if ((m_powered) && (m_playerControlled))
    {
      if (m_controller.isRightPressed())
      {
        m_dx += 0.1f;
        if (m_dx > 1) m_dx = 1;
        m_lastDx = 1;
      } else if (m_controller.isLeftPressed())
      {
        m_dx -= 0.1f;
        if (m_dx < -1) m_dx = -1;
        m_lastDx = -1;
      } else
      {
        if (m_dx > 0)
        {
          m_dx -= 0.25f;
          if (m_dx < 0) m_dx = 0;
        } else if (m_dx < 0)
        {
          m_dx += 0.25f;
          if (m_dx > 0) m_dx = 0;
        }
      }

      if (m_controller.isFirePressed())
      {
        if (m_ownsPowerUnit)
        {
          m_firePressedTicks++;
        }
      } else
      {
        if (m_firePressedTicks > 0)
        {
          throwUnit();
          m_firePressedTicks = 0;
        }
      }

    } else
    {
      //not powered, or player no longer controlled
      // still apply horizontal drag
      //refactor this (and above) to common function
      if (m_dx > 0)
      {
        m_dx -= 0.25f;
        if (m_dx < 0) m_dx = 0;
      } else if (m_dx < 0)
      {
        m_dx += 0.25f;
        if (m_dx > 0) m_dx = 0;
      }
    }

    if (m_controlDelayTicks > 0)
    {
      m_controlDelayTicks--;
    } else if (m_playerControlled)
    {
      if (m_controller.isTriggerPressed())
      {
        m_playerControlled = false;
        m_otherPlayer.playerTakeControl();
      }
    }

    float x = this.getX() + m_dx;

    byte b = 0;
    if (m_dx < 0)
    {
      //going left
      b = m_map.getCollisionBitsAt(x,this.getY());
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
      GameMapObject o = checkObjectCollisions(x,this.getY()+1);
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
        //m_dx = 0;
      }
    }

    this.setPosition(x,this.getY());


    //now do vertical

    if ((m_powered) && (m_playerControlled))
    {
      if (m_onGround)
      {
        if (m_controller.isJumpPressed())
        {
          m_dy = m_jumpDY;
          m_jumpTicks = 12;
          m_onGround = false;
        }
      } else
      {
        if (m_jumpTicks > 0) m_jumpTicks--;
        if ((m_jumpTicks > 0) && (m_controller.isJumpPressed()))
        {
          if (m_dy < (m_jumpDY * 0.75f)) m_dy = m_jumpDY*0.75f;
        } else
        {
          m_jumpTicks = 0;
        }
      }
    }

    float y = this.getY();
    m_dy += m_gravity;
    if (m_dy < -4) m_dy = -4;
    y += m_dy;
    if (m_dy < 0)
    {
      b = (byte) ((m_map.getCollisionBitsAt(this.getX()+2,y)) | (m_map.getCollisionBitsAt(this.getX()+14,y)));
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
        float deltaY = y - o.getY();
        if (deltaY > 5)
        {
          m_onGround = true;
          m_dy = 0;
          m_jumpTicks = 0;
          y = (float)java.lang.Math.ceil(o.getY() + o.getHeight());
        }
      }
    } else if (m_dy > 0)
    {
      b = (byte) ((m_map.getCollisionBitsAt(this.getX()+2,y+this.getHeight())) | (m_map.getCollisionBitsAt(this.getX()+14,y+this.getHeight())));
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
          m_jumpTicks = 0;
          m_onGround = false;
          y = o.getY() - this.getHeight() - 1;
        }
      }

    }

    this.setPosition(this.getX(),y);

    if (m_throwDelayTicks > 0) m_throwDelayTicks--;
    else
    {
      if (m_powerUnit.canPickUp())
      {
        if (Intersector.overlaps(this.getBoundingRectangle(), m_powerUnit.getBoundingRectangle()))
        {
          m_powerUnit.pickUp(this);
        }
      }
    }

  }

  public void powerOn()
  {
    m_powered = true;
  }

  public void setPowerUnit(PowerUnit pu)
  {
    m_powerUnit = pu;
    m_powered = true;
    m_ownsPowerUnit = true;
  }

  public void powerOff()
  {
    m_powered = false;
  }

  public void throwUnit()
  {
    if (m_ownsPowerUnit == false) return;
    if (m_powerUnit == null) return;
    if (m_powered == false) return;

    m_ownsPowerUnit = false;
    m_powered = false;

    if (m_firePressedTicks > 30) m_firePressedTicks = 30;

    float r = ((float)m_firePressedTicks / 30f) * 8;

    if (m_lastDx > 0)
    {
      m_powerUnit.throwUnit(r,2);
       m_throwDelayTicks = 60;
    } else
    {
      m_powerUnit.throwUnit(-r,2);
      m_throwDelayTicks = 60;
    }
  }

  public void playerTakeControl()
  {
    m_playerControlled = true;
    m_controlDelayTicks = 30;
  }

  public void setActive(boolean b)
  {
    this.setVisible(b);
  }

  public boolean isAlive()
  {
    return true;
  }
  
}

