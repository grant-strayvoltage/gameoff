package com.strayvoltage.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;

public class AnimateColorTo extends GameAnimation {

  float m_targetR, m_targetG, m_targetB, m_startR, m_startG, m_startB, m_currentR, m_currentG, m_currentB;
  float m_stepR, m_stepG, m_stepB;

	public  AnimateColorTo (float duration, float sr, float sg, float sb, float tr, float tg, float tb, int repeat) {
    super(duration, repeat);
    m_targetR = tr;
    m_targetB = tb;
    m_targetG = tg;
    m_startR = sr;
    m_startG = sg;
    m_startB = sb;

    m_currentB = m_startB;
    m_currentG = m_startG;
    m_currentR = m_startR;

    m_stepR = (m_targetR - m_startR)/m_duration;
    m_stepG = (m_targetG - m_startG)/m_duration;
    m_stepB = (m_targetB - m_startB)/m_duration;

  }

  public  AnimateColorTo (float duration, float sr, float sg, float sb, float tr, float tg, float tb) {
    super(duration, 1);
    m_targetR = tr;
    m_targetB = tb;
    m_targetG = tg;
    m_startR = sr;
    m_startG = sg;
    m_startB = sb;

    m_currentB = m_startB;
    m_currentG = m_startG;
    m_currentR = m_startR;

    m_stepR = (m_targetR - m_startR)/m_duration;
    m_stepG = (m_targetG - m_startG)/m_duration;
    m_stepB = (m_targetB - m_startB)/m_duration;

  }

  public void animateStep(float deltaTime, float time)
  {
    m_currentR += (deltaTime * m_stepR);
    m_currentG += (deltaTime * m_stepG);
    m_currentB += (deltaTime * m_stepB);

    if (m_currentR > 1)
      m_currentR = 1;
    else if (m_currentR < 0)
      m_currentR = 0;

    if (m_currentG > 1)
      m_currentG = 1;
    else if (m_currentG < 0)
      m_currentG = 0;

    if (m_currentB > 1)
      m_currentB = 1;
    else if (m_currentB < 0)
      m_currentB = 0;

    m_target.setColor(m_currentR, m_currentG, m_currentB,m_target.getOpacity());

  }

  public void initializeAnimation()
  {
    m_currentB = m_startB;
    m_currentG = m_startG;
    m_currentR = m_startR;

    m_target.setColor(m_currentR, m_currentG, m_currentB, m_target.getOpacity());
  }

  public static GameAnimateable createColorSequence(float duration, float pause, float sr, float sg, float sb, float tr, float tg, float tb, int repeat)
  {
    return AnimateColorTo.createColorSequence(duration, duration, pause, pause, sr, sg, sb, tr,tg,tb,repeat);
  }

  public static GameAnimateable createColorSequence(float dur1, float dur2, float pause1, float pause2, float sr, float sg, float sb, float tr, float tg, float tb, int repeat)
  {
    AnimateColorTo a = new AnimateColorTo(dur1,sr,sg,sb,tr,tg,tb);
    AnimateDelay d1 = new AnimateDelay(pause1);
    AnimateColorTo b = new AnimateColorTo(dur2,tr,tg,tb,sr,sg,sb);
    AnimateDelay d2 = new AnimateDelay(pause2);
    GameAnimateable[] a1 = {a,d1,b,d2};
    return new GameAnimationSequence(a1,repeat);
  }
}