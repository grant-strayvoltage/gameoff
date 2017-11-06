package com.strayvoltage.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import java.util.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.*;

public class GameTileMap implements GameDrawable {

  boolean m_visible = true;
  public TiledMap m_tiledMap;
  public OrthogonalTiledMapRenderer m_tiledMapRenderer;
  private OrthographicCamera m_camera;
  protected GameContainer m_parent = null;
  public boolean m_pause = false;
  protected int m_mapWidth, m_mapHeight, m_tilePixelWidth, m_tilePixelHeight;

	public  GameTileMap (String file, OrthographicCamera camera) {
    super();
    m_camera = camera;
    m_tiledMap = new TmxMapLoader().load(file);
    m_tiledMapRenderer = new OrthogonalTiledMapRenderer(m_tiledMap);
    MapProperties prop = m_tiledMap.getProperties();
    m_mapWidth = prop.get("width", Integer.class);
    m_mapHeight = prop.get("height", Integer.class);
    m_tilePixelWidth = prop.get("tilewidth", Integer.class);
    m_tilePixelHeight = prop.get("tileheight", Integer.class);
  }

  private String getAnimatingTileName(TiledMapTile tile)
  {
    MapProperties property = tile.getProperties();
    java.util.Iterator iter = property.getKeys();
    while(iter.hasNext())
    {
      String key = (String)iter.next();
      Gdx.app.debug("getAnimatingTileName","property: " + key);
      if (key.startsWith("TS"))
      {
        return key;
      }
    }

    return null;
  }

  public void setupAnimations(String tilesetName)
  {
    Gdx.app.debug("setupAnimations","Start: " + tilesetName);
    TiledMapTileSet tileset =  m_tiledMap.getTileSets().getTileSet(tilesetName);
    if (tileset == null)
    {
      Gdx.app.debug("setupAnimations","tileset was null: " + tilesetName);
      return;
    }

    Hashtable<String,Array<StaticTiledMapTile>> aTileSets = new Hashtable<String,Array<StaticTiledMapTile>>();

    int f = 0;
    int t = 1;
    String key = "";
    Array<StaticTiledMapTile> ts =  new Array<StaticTiledMapTile>();

    int min = 100000;
    for (TiledMapTile tile : tileset)
    {
      int c = tile.getId();
      if (c < min) min = c;
    }

    int tn = min;
    TiledMapTile tile = tileset.getTile(tn);
    while(tile != null)
    {
      //Gdx.app.debug("setupAnimations","id = " + tile.getId());
      if (f == 0)
      {
        key = "TS" +  tile.getId();
        aTileSets.put(key,ts);
        //Gdx.app.debug("setupAnimations","new row " + key);
      }

      f++;
      if (f > 4)
      {
        t++;
        f = 1;

        key = "TS" +  tile.getId();
        ts =  new Array<StaticTiledMapTile>();
        aTileSets.put(key,ts);
         //Gdx.app.debug("setupAnimations","new row " + key);
      }
      StaticTiledMapTile s = new StaticTiledMapTile(tile.getTextureRegion());
      ts.add(s);

      tn++;
      tile = tileset.getTile(tn);
    }

    TiledMapTileLayer layer = (TiledMapTileLayer) m_tiledMap.getLayers().get("platforms");
    for(int x = 0; x < layer.getWidth();x++)
    {
      for(int y = 0; y < layer.getHeight();y++){
          TiledMapTileLayer.Cell cell = layer.getCell(x,y);
          if (cell != null)
          {
            String tsname = "TS" + cell.getTile().getId();
             //Gdx.app.debug("setupAnimations","check " + tsname);
            if(tsname != null)
            {
              ts = aTileSets.get(tsname);
              if (ts != null)
              {              
                cell.setTile(new AnimatedTiledMapTile(0.2f,ts));
                //Gdx.app.debug("setupAnimations","replaced " + tsname);
              }
            }
          }
      }
    }
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

  public void addChildrenToLayer()
  {
    
  }

  public void draw(SpriteBatch s)
  {
  }

  public void draw()
  {
    if (m_visible)
    {
      m_tiledMapRenderer.setView(m_camera);
      m_tiledMapRenderer.render();
    }
  }

  
  public void setColor(float r, float g, float b, float a) { }
  public void setPosition(float x, float y) { }
  public float getX() { return 0; }
  public float getY() { return 0; }

  public void setScale(float s) { }
  public void setScale(float sx, float sy) { }
  
  public void rotate(float a)
  {

  }
  
  public void runAnimation(GameAnimateable a)
  {
  }

  public void stopAllAnimations()
  {
    /*Iterator iter = m_animations.iterator();
    while (iter.hasNext())
    {
      GameAnimateable a = (GameAnimateable) iter.next();
      a.stop();
      iter.remove();
    }*/
  }

  public void animate(float deltaTime)
  {

  }

  public float getOpacity() { return 1.0f;}
  public void setOpacity(float o) {}

  public long playSound(String soundName, float volume)
  {
    return GameMain.getSingleton().playSound(soundName, volume);
  }

  public GameContainer getParent()
  {
    return m_parent;
  }

  public void setParent(GameContainer layer)
  {
    m_parent = layer;
  }

  public void setRotation(float a)
  {

  }

  public void pause()
  {
    m_pause = true;
  }

  public void resume()
  {
    m_pause = false;
  }

  public int getMapWidth()
  {
    return m_mapWidth;
  }

  public int getMapHeight()
  {
    return m_mapHeight;
  }

  public int getTilePixelWidth()
  {
    return m_tilePixelWidth;
  }

  public int getTilePixelHeight()
  {
    return m_tilePixelHeight;
  }

  public void dispose() {
    m_tiledMap.dispose();
    m_tiledMapRenderer.dispose();
  }

}

