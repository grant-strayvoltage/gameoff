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

public class GameSprite extends Sprite implements GameDrawable {

  boolean m_visible = true;
  public float m_opacity = 1.0f;
  public ArrayList<GameAnimateable> m_animations = new ArrayList<GameAnimateable>();
  GameAnimateable m_nextAnimation = null;
  protected GameContainer m_parent = null;
  public boolean m_pause = false;
  public int m_groupId = -1;
  public int code = 0;
  public int m_collisionState = -1;
  public float m_damage = 1.0f;
  public boolean m_fx = false;
  public boolean m_fy = false;

  public GameSprite(Texture texture)
  {
    super(texture);

  }
  
  public void setFlip()
  {
    super.setFlip(m_fx,m_fy);
  }


  public void setFlip(boolean fx, boolean fy)
  {
    super.setFlip(fx,fy);
    m_fx = fx;
    m_fy = fy;
  }

  public void setDamage(float dmg)
  {
    m_damage = dmg;
  }

  public void addChildrenToLayer()
  {

  }


  public void setCollisionState(int i)
  {
      m_collisionState = i;
  }

  public void setActive(boolean b)
  {
    this.setVisible(b);
  }

  public void setCode(int i)
  {
    //Gdx.app.debug("EnemySprite","SetCode = " + i + " FROM " + code);
    code = i;
  }

  public GameSprite()
  {
    super();
  }

	public  GameSprite (TextureRegion region) {
    super(region);
  }

  public void setGroupId(int groupId)
  {
    m_groupId = groupId;
  }

  public int getGroupId()
  {
    return m_groupId;
  }

  public boolean isVisible()
  {
    return m_visible;
  }

  public void setVisible(boolean vis)
  {
    m_visible = vis;
  }

  public void update(float deltaTime)
  {

  }

  public void die()
  {

  }

  public void hit(float dmg, GameSprite s)
  {

  }

  @Override
  public void dispose()
  {

  }

  @Override
  public void draw(SpriteBatch s)
  {
    //Gdx.app.log("INFO", "Draw GameSprite called. m_opacity = " + m_opacity);
    super.draw(s, m_opacity);
  }

  public float getOpacity()
  {
    return m_opacity;
  }

  public void setOpacity(float o)
  {
    m_opacity = o;
  }

  public void runAnimation(GameAnimateable a)
  {
    if (a != null)
    {
      a.run(this);
      m_animations.add(a);
      m_nextAnimation = null;
    }
  }

  public void chainAnimations(GameAnimateable a, GameAnimateable b)
  {
    a.run(this);
    m_animations.add(a);
    m_nextAnimation = b;
  }

  public void stopAllAnimations()
  {
    m_nextAnimation = null;
    Iterator iter = m_animations.iterator();
    while (iter.hasNext())
    {
      GameAnimateable a = (GameAnimateable) iter.next();
      if (a.ignoreStop() == false)
      {
        a.stop();
        iter.remove();
      }
    }
  }

  public void animate(float deltaTime)
  {
    if (!m_pause)
    {
      if (m_animations.size() == 0)
        return;

      boolean animationRemoved = false;

      if (m_animations.size() == 1)
      {
        GameAnimateable a = (GameAnimateable) m_animations.get(0);
        boolean notDone = a.step(deltaTime);
        if (!notDone)
        {
          m_animations.remove(0);
          animationRemoved = true;
        }
      } else
      {
        
        Iterator iter = m_animations.iterator();
        while (iter.hasNext())
        {
          GameAnimateable a = (GameAnimateable) iter.next();
          boolean notDone = a.step(deltaTime);
          if (!notDone)
          {
            iter.remove();
            animationRemoved = true;
          }
        }
      }

      if (animationRemoved)
      {
        if (m_nextAnimation != null)
        {
          this.runAnimation(m_nextAnimation);
        }
      }
    }
  }

  public long playSound(String soundName, float volume)
  {
    return GameMain.getSingleton().playSound(soundName, volume);
  }

  public long loopSound(String soundName, float volume)
  {
    return GameMain.getSingleton().loopSound(soundName, volume);
  }

  public long loopSoundManageVolume(String soundName, GameSprite sprite, GameSprite player, float max, float min)
  {
    return GameMain.getSingleton().loopSoundManageVolume(soundName, sprite, player, max, min);
  }

  public long playSound(String soundName, GameSprite player, GameSprite target, float max, float min)
  {
    return GameMain.getSingleton().playSound(soundName, player, target, max, min);
  }
    

  public void stopSound(String soundName)
  {
    GameMain.getSingleton().stopSound(soundName);
  }

  public void stopSound(String soundName, long soundId)
  {
      GameMain.getSingleton().stopSound(soundName, soundId);
  }

  public void setGlobal(String key, String value)
  {
      GameMain.getSingleton().setGlobal(key,value);
  }

  public String getGlobal(String key)
  {
      return GameMain.getSingleton().getGlobal(key);
  }

  public int getGlobalInt(String key)
  {
    return Integer.parseInt(GameMain.getSingleton().getGlobal(key));
  }

  public GameContainer getParent()
  {
    return m_parent;
  }

  public boolean isOnScreen()
  {
    return m_parent.isOnScreen(this.getX(), this.getY(), this.getWidth(), this.getHeight());
  }

  public void setParent(GameContainer layer)
  {
    m_parent = layer;
  }

  public void setRotation(float a)
  {
    super.setRotation(a);
  }

  public void pause()
  {
    m_pause = true;
  }

  public void resume()
  {
    m_pause = false;
  }

  public boolean isAlive()
  {
    return true;
  }

}

