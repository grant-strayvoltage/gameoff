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

public class PowerUnit extends GameMapObject {

  Player m_owner = null;
  boolean m_pickedUp = true;

  public void init(MapProperties mp, TextureAtlas textures)
  {
    m_categoryBits = Box2dVars.POWER;
    m_filterMask = Box2dVars.BLOCK | Box2dVars.FLOOR | Box2dVars.PLAYER_NORMAL | Box2dVars.PLAYER_JUMPING | Box2dVars.PLATFORM;
    m_colBits = 16;
    TextureRegion texture = null;
    texture = textures.findRegion("power_F1");
    this.setRegion(texture);
    this.setSize(texture.getRegionWidth(),texture.getRegionHeight());
  }

  public void pickUp(Player p)
  {
      m_owner = p;
      m_pickedUp = true;
      this.setVisible(false);
      m_body.setActive(false);
      m_owner.setPowerUnit(this);
  }

  public void throwUnit(float dx, float dy)
  {
    m_pickedUp = false;
    this.setBodyPosition(m_owner.getX() + 4, m_owner.getY() + m_owner.getHeight() - 8);
    m_body.setLinearVelocity(0,0);
    m_body.setActive(true);
    m_owner = null;
    this.setVisible(true);
    m_body.setLinearVelocity(dx*2,dy*2);
    setPositionToBody();
  }

  public boolean canPickUp()
  {
      return !m_pickedUp;
  }

  public void update(float deltaTime)
  {
      if (m_pickedUp == false)
      {
        setPositionToBody();
      } else
      {
          this.setPosition(m_owner.getX() + 4, m_owner.getY() + m_owner.getHeight() - 8);
      }

  }
}