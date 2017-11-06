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
import com.strayvoltage.gamelib.*;

public abstract class GameMapObject extends GameSprite {

  public GameMapObject()
  {
    super();
  }

  public abstract void init(MapProperties mp, TextureAtlas textures);

  public boolean getBool(String key, MapProperties mp)
  {
    //if missing, or the default value from Tiled, assume false.
    //If you don't want false as the default value, set the value explicitly in Tiled for this object.
    Boolean b = (Boolean)mp.get(key);
    if (b == null) return false;
    return b.booleanValue();
  }
}

