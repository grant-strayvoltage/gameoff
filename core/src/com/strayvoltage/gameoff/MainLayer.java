package com.strayvoltage.gameoff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
//import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.assets.AssetManager;
import com.strayvoltage.gamelib.*;
import com.badlogic.gdx.physics.box2d.*;

public class MainLayer extends GameLayer  {

int m_stage, m_level, gameState;
AssetManager m_assets;
GameInputManager2 inputManager;
Matrix4 m_defaultMatrix;
public GameTileMap tiledMap;
float stateTime;
float tw = 16f;
float th = 16f;
int m_mapWidth, m_mapHeight;
static BitmapFont m_font16 = null;
static BitmapFont m_font24 = null;
static BitmapFont m_font32 = null;
TextureAtlas m_sprites = null;
public ArrayList<GameMapObject> m_gameMapObjects = new ArrayList<GameMapObject>();
static public World world;

Player m_player1, m_player2;

public MainLayer()
  {
    super();
    m_assets = getAssetManager();

    gameState = 1;

    m_defaultMatrix = m_camera.combined.cpy();
    m_defaultMatrix.setToOrtho2D(0, 0, 640, 360);
    this.setCameraPosition(320,180);

    inputManager = MasterInputManager.getSharedInstance().getController(0);
    inputManager.setViewport(GameMain.getSingleton().m_viewport);

    m_sprites = m_assets.get("game_sprites.txt", TextureAtlas.class);

    //gameSpritesTextures = m_assets.get("game_sprites.txt", TextureAtlas.class);

    /*
    m_backTexture = new Texture("back-1.png");
    m_backTextureCode = 1;
    m_backSprite = new GameSprite(m_backTexture); 
    m_backSprite.setPosition(0,0);
    m_backSprite.setVisible(true);
    */

    if (m_font32 == null)
    {
      m_font32 = new BitmapFont(Gdx.files.internal("Font32.fnt"), Gdx.files.internal("Font32.png"), false);
      m_font24 = new BitmapFont(Gdx.files.internal("Font24.fnt"), Gdx.files.internal("Font24.png"), false);
      m_font16 = new BitmapFont(Gdx.files.internal("Font16.fnt"), Gdx.files.internal("Font16.png"), false);
    }

    /*
    if (bombEffectPool == null)
    {
      ParticleEffect bombEffect = new ParticleEffect();
      bombEffect.load(Gdx.files.internal("player_die.p"), Gdx.files.internal(""));
      bombEffect.setEmittersCleanUpBlendFunction(true);
      bombEffectPool = new ParticleEffectPool(bombEffect, 5, 15);
    }
    */
  }

public float getFloat(String key, MapObject mp)
  {
      Float f = (Float) (mp.getProperties().get(key));
      float ff = f.floatValue();
      return ff;

  }

  public float safeGetFloatFromObject(Object o, float nullValue)
  {
    if (o != null)
    {
      String f = (String) (o);
      Float ff = new Float(f);
      return ff.floatValue();
    }
    return nullValue;
  }

  public float getStrToFloat(String key, MapObject mp)
  {
      String f = (String) (mp.getProperties().get(key));
      Float ff = new Float(f);
      return ff.floatValue();

  }

  public int getStrToInt(String key, MapObject mp)
  {
      String f = (String) (mp.getProperties().get(key));
      Integer ff = new Integer(f);
      return ff.intValue();
  }

  private void setupTileMapBox2D()
  {

    TiledMapTileLayer pLayer = (TiledMapTileLayer) tiledMap.m_tiledMap.getLayers().get("platforms");

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.density = 1.0f;
    fixtureDef.restitution = 0.25f;
    fixtureDef.filter.categoryBits = 1;
    fixtureDef.friction = 0.5f;
    float w = 0;
    float boxY = 0;
    float boxLeftX = 0;
    
    for (int ty = 0; ty < m_mapHeight; ty++)
    {
      BodyDef bodyDef = null;
      for (int tx = 0; tx < m_mapWidth; tx++)
      {
        TiledMapTileLayer.Cell c = pLayer.getCell(tx,ty);
        if (c != null)
        {
            float pw = c.getTile().getTextureRegion().getRegionWidth();
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            w = (pw/16);
            boxLeftX = tx;
            boxY = ty;
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(w/2,(pw-1)/16/2);
            fixtureDef.shape = shape;
            bodyDef.position.set(boxLeftX + w/2,boxY + (pw-1)/16/2 + 2/16);
            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);
            shape.dispose();
            //bodyDef.dispose();
            bodyDef = null;
            w = 0;
        }
      }
    }
  }

  public void loadLevel(int stage, int lv)
  {
    m_stage = stage;
    m_level = lv;

    this.removeAll();

    if (world != null)
    {
      Array<Body> bodies = new Array<Body>();
      world.getBodies(bodies);
      for (Body b : bodies)
      {
        world.destroyBody(b);
      }
    }

    if (world == null)
      world = new World(new Vector2(0, -20), true);

    PowerUnit pu = new PowerUnit();
    pu.init(null,m_sprites);
    m_gameMapObjects.add(pu);
    pu.addToWorld(world);

    m_player1 = new Player(m_sprites.findRegion("player1_stand"), inputManager);
    this.add(m_player1);

    m_player2 = new Player(m_sprites.findRegion("player2_stand"), inputManager);
    this.add(m_player2);

    pu.pickUp(m_player1);

    m_player1.addToWorld(world);
    m_player2.addToWorld(world);

    this.add(pu);

    //set music
    if (tiledMap != null)
    {
      tiledMap.dispose();
      tiledMap = null;
    }

    tiledMap = new GameTileMap("level_" + stage + "-" + lv + ".tmx", m_camera);
    m_player1.setMap(tiledMap, m_player2,23,pu);
    m_player2.setMap(tiledMap, m_player1,8,pu);
    pu.setMap(tiledMap);



    m_player1.m_playerControlled = true;
    m_player1.m_powered = true;

    //tiledMap.setupAnimations("level_small_tiles");
    //tiledMap.setupAnimations("level_big_tiles");

    MapProperties mapProps = tiledMap.m_tiledMap.getProperties();
    String title = (String) mapProps.get("Title");

    /*
    String levelType = "2";
    m_levelType = 2;
    m_levelScale = 1.0f;
    if (mapProps.containsKey("LevelType"))
      levelType = (String) mapProps.get("LevelType");

    m_levelType = Integer.parseInt(levelType);
    int lt = m_levelType;
    */

    m_mapWidth = tiledMap.getMapWidth();
    m_mapHeight = tiledMap.getMapHeight();

    tw = (float) tiledMap.getTilePixelWidth();
    th = (float) tiledMap.getTilePixelHeight();

    setupTileMapBox2D();

    stateTime = 0;
    
    //TiledMapTileLayer pLayer = (TiledMapTileLayer) tiledMap.m_tiledMap.getLayers().get("platforms");
    MapLayer objectsLayer = (MapLayer) tiledMap.m_tiledMap.getLayers().get("objects");
    MapObjects mapObjects = objectsLayer.getObjects();
    float px,py;

    for (MapObject obj : mapObjects)
    {
      MapProperties p = obj.getProperties();
      String t = (String) p.get("type");
      px = this.getFloat("x", obj);
      py = this.getFloat("y", obj);
      if (t.equals("PlayerStart1"))
      {
        m_player1.setBodyPosition(px,py);
      } else if (t.equals("PlayerStart2"))
      {
        m_player2.setBodyPosition(px,py);
      } else
      {
        Gdx.app.log("MainLayer","Add Map Object - type = " + t);
        try
        {
          Object o = Class.forName("com.strayvoltage.gameoff." + t).newInstance();
          GameMapObject gmo = (GameMapObject)o;
          gmo.setMap(tiledMap);
          gmo.init(p,m_sprites);
          this.add(gmo);
          gmo.addToWorld(world);
          m_gameMapObjects.add(gmo);
          gmo.setBodyPosition(px,py);
        } catch (InstantiationException e)
        {
          Gdx.app.log("MainLayer","InstantiationException = " + e.getMessage());
        } catch (ClassNotFoundException ce)
        {
          Gdx.app.log("MainLayer","ClassNotFoundException = " + ce.getMessage());
        } catch (IllegalAccessException ie)
        {
          Gdx.app.log("MainLayer","IllegalAccessException = " + ie.getMessage());
        }
      }
    }

    //TODO: we'll add particle effects when player dies and in other spots
    /*
    if (ufoEffectPool == null)
    {
      ParticleEffect ufoEffect = new ParticleEffect();
      ufoEffect.load(Gdx.files.internal("ufo_explode.p"), Gdx.files.internal(""));
      ufoEffect.setEmittersCleanUpBlendFunction(true);
      ufoEffectPool = new ParticleEffectPool(ufoEffect, 30, 15);
    } */
  }

  @Override
  public void update (float deltaTime) {
    stateTime += deltaTime;
    inputManager.handleInput();
    //if (inputManager.isJumpPressed())
    //{
     //   if (stateTime > 2)
     //     System.exit(0);
    //}
    world.step(1/60f, 6, 2);
  }

  @Override
  protected void preCustomDraw()
  {
    m_spriteBatch.setProjectionMatrix(m_defaultMatrix);
    //m_spriteBatch.begin();
    //m_backSprite.draw(m_spriteBatch);
    //m_spriteBatch.end();
    //super.drawBackSprites();

    if (tiledMap != null)
      tiledMap.draw();

  }

}

