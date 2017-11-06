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

  public Player(TextureRegion texture, GameInputManager2 controller)
  {
    super(texture);
    m_controller = controller;
  }

  public void setMap(GameTileMap m, Player p, float jumpDY)
  {
    m_map = m;
    m_otherPlayer = p;
    m_jumpDY = jumpDY;
  }

  public void update(float deltaTime)
  {
    if ((m_powered) && (m_playerControlled))
    {
      if (m_controller.isRightPressed())
      {
        m_dx += 0.1f;
        if (m_dx > 1) m_dx = 1;
      } else if (m_controller.isLeftPressed())
      {
        m_dx -= 0.1f;
        if (m_dx < -1) m_dx = -1;
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
      b = m_map.getCollisionBitsAt(x+16,this.getY());
      b = (byte) (b | m_map.getCollisionBitsAt(x+16,this.getY() + this.getHeight() - 6));
    }
    if (b > 0)
    {
      x -= m_dx;
      m_dx = 0;
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
    }

    this.setPosition(this.getX(),y);

  }


  
  public void playerTakeControl()
  {
    m_playerControlled = true;
    m_controlDelayTicks = 30;
    //temp (for now always powered)
    m_powered = true;
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

