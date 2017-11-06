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

public class Platform extends GameMapObject {

  boolean m_shapeHorizontal = false;
  boolean m_moveHorizontal = false;

  public void init(MapProperties mp, TextureAtlas textures)
  {
    TextureRegion texture = null;
    if (getBool("shapeVertical",mp) == false)
    {
        //Horizontal shaped platform
        m_shapeHorizontal = true;
        //TODO: based on size, set width to 3, 4, or 5.
        texture = textures.findRegion("platform_3w");
        this.setRegion(texture);
    } else
    {
        //Vertical shaped platform
        m_shapeHorizontal = false;
        //TODO: based on size, set height to 3, 4, or 5.
        texture = textures.findRegion("platform_3v");
        this.setRegion(texture);
    }

    this.setSize(texture.getRegionWidth(),texture.getRegionHeight());

    //TODO: setup movement triggers
    //on contact, timed (default), or switch based

    //then override update, to handle movement, etc. as required.

  }
}