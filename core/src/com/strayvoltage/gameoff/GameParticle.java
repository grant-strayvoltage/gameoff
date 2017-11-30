package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.Gdx;
import com.strayvoltage.gamelib.*;

public class GameParticle extends GameSprite {

  public int ticksToLive = 60;
  public float dx = 0;
  public float dy = 0;
  public float ddx = 0;
  public float ddy = 0;
  public int m_state = 0;
  public GameAnimateable m_fadeOut = new AnimateFadeOut(0.5f);
 
    
  public GameParticle(TextureAtlas textures, String imgName)
  {
    super(textures.findRegion(imgName));
    m_state = 0;
    this.setVisible(false);
  }

  public void setParticle(float xx, float yy, float w, float h, int dir, float spd, int life, float grav)
  {

    dx = 0;
    dy = 0;

    if (dir == 0) dy = spd;
    if (dir == 1) dx = spd;
    if (dir == 2) dy = -spd;
    if (dir == 3) dx = -spd;

    xx += Math.random() * w;
    yy += Math.random() * h;

    if ((dir == 1) || (dir == 3))
    {
      ddx = 0.995f;
      ddy = grav;
    } else
    {
      ddx = 0f;
      ddy = grav;
    }

    ticksToLive = (int)(Math.random() * 10) + life;
    setPosition(xx,yy);
    this.setVisible(true);
    this.setOpacity(1f);
    m_state = 10;
  }

  public void move()
  {
    float xx = this.getX() + dx;
    float yy = this.getY() + dy;

    dx = dx * ddx;
    dy = dy - ddy;

    this.setPosition(xx,yy);
  }

  public void update(float deltaTime)
  {
    if (m_state == 0) return;
    if (m_state == 10)
    {
      //active
      ticksToLive--;
      if (ticksToLive < 1)
      {
        this.runAnimation(m_fadeOut);
        m_state = 20;
      }
      move();
    } else if (m_state == 20)
    {
      if (m_fadeOut.isRunning() == false)
      {
        this.setVisible(false);
        m_state = 0;
      }
      move();
    }
  }
}
