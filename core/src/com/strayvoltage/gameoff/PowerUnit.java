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
      m_owner.setPowerUnit(this);
  }

  public void throwUnit(float dx, float dy)
  {
      m_dx = dx;
      m_dy = dy;
      m_pickedUp = false;
      this.setPosition(m_owner.getX() + 4, m_owner.getY() + m_owner.getHeight() - 8);
      m_owner = null;
      this.setVisible(true);
  }

  public boolean canPickUp()
  {
      return !m_pickedUp;
  }

  public void update(float deltaTime)
  {
      if (m_pickedUp == false)
      {
        handlePhysics();
      } else
      {
          this.setPosition(m_owner.getX() + 4, m_owner.getY() + m_owner.getHeight() - 8);
      }

  }
}