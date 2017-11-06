package com.strayvoltage.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimateRotateTo extends GameAnimation {

  protected float m_targetDegrees, m_currentDegrees, m_startDegrees, m_stepDegrees;

  public  AnimateRotateTo (float duration, float fromDegrees, float toDegrees) {
    super(duration, 1);
    m_targetDegrees = toDegrees;
    m_startDegrees = fromDegrees;
    m_currentDegrees = m_startDegrees;

    m_stepDegrees = (m_targetDegrees - m_startDegrees)/m_duration;

  }

  public  AnimateRotateTo (float duration, float fromDegrees, float toDegrees, int repeat) {
    super(duration, repeat);
    m_targetDegrees = toDegrees;
    m_startDegrees = fromDegrees;
    m_currentDegrees = m_startDegrees;
    m_stepDegrees = (m_targetDegrees - m_startDegrees)/m_duration;
  }

  public void animateStep(float deltaTime, float time)
  {
      m_currentDegrees += (deltaTime * m_stepDegrees);


      if (m_targetDegrees > m_startDegrees)
      {
        if (m_currentDegrees > m_targetDegrees)
        {
          m_currentDegrees = m_targetDegrees;
        }
      } else if (m_targetDegrees < m_startDegrees)
      {
        if (m_currentDegrees < m_targetDegrees)
        {
          m_currentDegrees = m_targetDegrees;
        }
      } 

      m_target.setRotation(m_currentDegrees);

  }

  public static GameAnimateable createRotateSequence(float dur1, float delta, int repeat)
  {
    float d = dur1/(4 * delta);  //time per degree
    AnimateRotateTo a = new AnimateRotateTo(d * delta, 0, delta);
    AnimateRotateTo b = new AnimateRotateTo(d * 2 * delta, delta, -delta);
    AnimateRotateTo c = new AnimateRotateTo(d * delta, -delta, 0);
    GameAnimateable[] a1 = {a,b,c};
    return new GameAnimationSequence(a1,repeat);
  }


  public void initializeAnimation()
  {
    m_currentDegrees = m_startDegrees;
  }
}