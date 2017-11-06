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

  public Player(TextureRegion texture, GameInputManager2 controller)
  {
    super(texture);
    m_controller = controller;
  }

  public void update(float deltaTime)
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

    float x = this.getX() + m_dx;
    this.setPosition(x,this.getY());
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

