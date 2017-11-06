package com.strayvoltage.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.Gdx;

public class AnimateFollow extends GameAnimation {

  protected float m_x, m_y;
  protected GameSprite m_followSprite;

  public AnimateFollow(float duration, GameSprite followSprite, float offX, float offY) {
    super(duration, 1);
    m_x = offX;
    m_y = offY;
    m_followSprite = followSprite;
  }

  public void animateStep(float deltaTime, float time)
  {
    float fs = m_followSprite.getScaleX();
    float fx = m_followSprite.getX() + m_x * fs;
    float fy = m_followSprite.getY() + m_y * fs;

    m_targetSprite.setScale(fs);
    m_targetSprite.setPosition(fx,fy);

  }

  public void initializeAnimation()
  {

    float fs = m_followSprite.getScaleX();
    float fx = m_followSprite.getX() + m_x * fs;
    float fy = m_followSprite.getY() + m_y * fs;

    m_targetSprite.setScale(fs);
    m_targetSprite.setPosition(fx,fy);
  }

}