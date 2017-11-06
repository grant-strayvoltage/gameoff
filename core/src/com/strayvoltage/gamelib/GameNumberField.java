package com.strayvoltage.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameNumberField extends GamePanel {

  protected int m_number;
  protected float m_width = 0;
  protected boolean m_centered = false;
  int m_maxDigits = 1;
  GameSprite m_numberBack, m_numberFront;
  TextureAtlas m_textures = null;

  public GameNumberField(TextureAtlas textures)
  {
    super();
    m_numberBack = new GameSprite(textures.findRegion("number_back"));
    m_numberFront = new GameSprite(textures.findRegion("number_back"));
    this.add(m_numberBack);
    this.add(m_numberFront);
    m_number = -1;
    m_textures = textures;
  }

  public float getWidth()
  {
    return m_numberFront.getWidth();
  }

  public void setScale(float s) {
    m_numberFront.setScale(s);
    m_numberBack.setScale(s);
  }

  public void setScale(float s1, float s2) {
    m_numberFront.setScale(s1,s2);
    m_numberBack.setScale(s1,s2);
  }

  public void setColor(float r, float g, float b, float a)
  {
    m_numberFront.setColor(r,g,b,a);
  }

  public float getOpacity()
  {
    return m_numberFront.getOpacity();
  }

  public void setOpacity(float o)
  {
    m_numberFront.setOpacity(o);
    m_numberBack.setOpacity(o);
  }

  public void setNumber(int n)
  {
    m_number = n;
    if ((n >= 0) && (n < 10))
    {
      m_numberFront.setRegion(m_textures.findRegion("number" + m_number));
      m_numberFront.setVisible(true);
    }
    else
      m_numberFront.setVisible(false);
  }

  public void setPosition(float xx, float yy)
  {
    super.setPosition(xx,yy);
    m_numberFront.setPosition(xx,yy);
    m_numberBack.setPosition(xx,yy);
  }

}

