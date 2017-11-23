package com.strayvoltage.gameoff;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.strayvoltage.gamelib.*;

public class MainLayer extends GameLayer  {
	
	public static final int MAX_STAGES = 1; //CHANGE IF YOU ADD MORE STAGES
	public static final int MAX_LEVELS_PER_STGE = 5; //CHANGE IF YOU ADD or REMOVE LEVELS --

int m_gameState = 10;
Exit m_exit;
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
static public Box2DDebugRenderer debug_renderer;
PowerUnit m_brain = null;

Player m_player1, m_player2;
//handles all switches
SwitchAdapter switch_adapter;
TextureAtlas m_backgrounds;
GameAnimateable m_mainBackAnim, m_lightning;
GameSprite m_backSprite;

int m_lightningTicks = 90;
private float acumm = 0; //box2d var
public MainLayer()
  {
    super();
    m_assets = getAssetManager();
    switch_adapter = new SwitchAdapter();
    gameState = 1;

    m_defaultMatrix = m_camera.combined.cpy();
    //m_defaultMatrix.setToOrtho2D(0, 0, 1280,720);
    this.setCameraPosition(GameMain.getSingleton().m_viewport.getWorldWidth()*.5f,
    		GameMain.getSingleton().m_viewport.getWorldHeight()*.5f);

    inputManager = MasterInputManager.getSharedInstance().getController(0);
    inputManager.setViewport(GameMain.getSingleton().m_viewport);

    m_assets.finishLoading();

    m_sprites = m_assets.get("game_sprites.txt", TextureAtlas.class);
    m_backgrounds = m_assets.get("backgrounds.txt", TextureAtlas.class);

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

    TiledMapTileLayer p_Layer = (TiledMapTileLayer) tiledMap.m_tiledMap.getLayers().get("platforms");

    FixtureDef fixtureDef = new FixtureDef();
    fixtureDef.density = 1.0f;
    fixtureDef.restitution = 0f;
    //floor is now floor lol
    fixtureDef.filter.categoryBits = Box2dVars.FLOOR;
    //i think without a mask it means the floor can collide with everything. This way it cant. 
    fixtureDef.filter.maskBits =  Box2dVars.BLOCK | Box2dVars.PLAYER_NORMAL | Box2dVars.PLAYER_JUMPING
    							| Box2dVars.POWER| Box2dVars.PLAYER_FOOT | Box2dVars.BRAIN_FOOT | Box2dVars.OBJECT;
    fixtureDef.friction = 0.5f;
    float w = 0;
    float boxY = 0;
    float boxLeftX = 0;
    float tilesize = tiledMap.getTilePixelWidth();//assuming we dont ever use weird size tiles. 
    Array<Vector2> chainVectors = new Array<Vector2>();
    
    for (int ty = 0; ty < m_mapHeight; ty++)
    {
      BodyDef bodyDef = null;
      PolygonShape chain = null;
      int startx = 0;
      
      for (int tx = 0; tx < m_mapWidth; tx++)
      {
    	  fixtureDef.filter.categoryBits = Box2dVars.FLOOR;
        TiledMapTileLayer.Cell c = p_Layer.getCell(tx,ty);
        int cellid = 0;

        if(c!=null)
        	cellid = c.getTile().getId();

        //Gdx.app.log("MainLayer","(" + tx + ", " + ty + " = " + cellid);

        if(cellid > 31) {

        	if(chainVectors.size == 0) {
        		//setup first vertex
        		startx = tx;
        		chainVectors.add(new Vector2(0,0));
        		chainVectors.add(new Vector2(0,tilesize));
        	}
        	chainVectors.add(new Vector2((chainVectors.peek().x+tilesize),tilesize));
          //Gdx.app.log("MainLayer","(" + tx + ", " + ty + " = " + cellid + " ADDED");
        }
        
        if ((cellid < 32 || tx+1 == m_mapWidth ) && chainVectors.size>0){
        	//Gdx.app.log("MainLayer","(" + tx + ", " + ty + " = " + cellid + " FLUSHED - END -1");
        	chainVectors.add(new Vector2((chainVectors.peek().x),0));
        	bodyDef = new BodyDef();
        	bodyDef.type = BodyType.StaticBody;
        	chain = new PolygonShape();
        	Vector2[] VX = new Vector2[4];
        	VX[0] = chainVectors.first().scl(1f/Box2dVars.PIXELS_PER_METER);
        	VX[1] = chainVectors.get(1).scl(1f/Box2dVars.PIXELS_PER_METER);;
        	VX[2] = chainVectors.get(chainVectors.size-2).scl(1f/Box2dVars.PIXELS_PER_METER);;
        	VX[3] = chainVectors.peek().scl(1f/Box2dVars.PIXELS_PER_METER);;
        	chain.set(VX);
        	chainVectors.clear();
        	fixtureDef.shape = chain;
          fixtureDef.restitution = 0;
        	bodyDef.position.set((startx*tilesize)/Box2dVars.PIXELS_PER_METER, (ty*tilesize)/Box2dVars.PIXELS_PER_METER);
        	world.createBody(bodyDef).createFixture(fixtureDef);
        	chain.dispose();
        	chain = null;
        	bodyDef = null;
        	startx = 0;
        }
        
        if (cellid > 0 && cellid < 32)//spikes.. add other hazard here
        {
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            boxY = ty;
            PolygonShape shape = new PolygonShape();
            
            Vector2[] points = new Vector2[4];
            points[0] = new Vector2(0,0).scl(1f/Box2dVars.PIXELS_PER_METER);
            points[1] = new Vector2(0,tilesize).scl(1f/Box2dVars.PIXELS_PER_METER);
            points[2] = new Vector2(tilesize,tilesize).scl(1f/Box2dVars.PIXELS_PER_METER);
            points[3] = new Vector2(tilesize,0).scl(1f/Box2dVars.PIXELS_PER_METER);
            
            shape.set(points);
            
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Box2dVars.HAZARD;
            bodyDef.position.set((tx*tilesize)/Box2dVars.PIXELS_PER_METER,(ty*tilesize)/Box2dVars.PIXELS_PER_METER);
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
    
    //KEEP TRACK FOR EXIT PURPOSE-----------------------------
    GameMain.getSingleton().setGlobal("m_stage", ""+stage);
    GameMain.getSingleton().setGlobal("m_level", ""+lv);
    //NEXT LEVEL
    if(lv+1 <= MAX_LEVELS_PER_STGE) //10 is an arbitrary number of levels per stage
    	GameMain.getSingleton().setGlobal("m_next_level", ""+(lv+1));
    else 
    	if(stage+1 <= MAX_STAGES) { //10 is an arbitrary number of stages
    		GameMain.getSingleton().setGlobal("m_stage", ""+(stage+1));
    		GameMain.getSingleton().setGlobal("m_next_level",""+1);
    	}else //if there is no more stages then the game is complete
    		GameMain.getSingleton().setGlobal("game_complete", "true");
    	
    //---------------------------------------------------------
    this.removeAll();

    if (world != null)
    {
      Array<Body> bodies = new Array<Body>();
      world.getBodies(bodies);
      for (Body b : bodies)
      {
        world.destroyBody(b);
      }
      world.setContactListener(null);
    }

    if (world == null) {
    	world = new World(new Vector2(0, -20), true);
    	debug_renderer = new Box2DDebugRenderer();
    }
      

    m_brain = new PowerUnit();
    m_brain.init(null,m_sprites);
    m_gameMapObjects.add(m_brain);
    m_brain.addToWorld(world);

    m_player1 = new Player(m_sprites,1,inputManager);
    //this.add(m_player1);

    m_player2 = new Player(m_sprites,2, inputManager);
    //this.add(m_player2);

    m_brain.pickUp(m_player1);

    m_player1.addToWorld(world);
    m_player2.addToWorld(world);

    //this.add(m_brain);

    //set music
    if (tiledMap != null)
    {
      tiledMap.dispose();
      tiledMap = null;
    }

    tiledMap = new GameTileMap("level_" + stage + "-" + lv + ".tmx", m_camera);
    m_player1.setMap(tiledMap, m_player2,24,40,m_brain,1);
    m_player2.setMap(tiledMap, m_player1,8,15,m_brain,2);
    m_brain.setMap(tiledMap);



    m_player1.m_playerControlled = true;
    m_player1.m_powered = true;

    //tiledMap.setupAnimations("level_small_tiles");
    //tiledMap.setupAnimations("level_big_tiles");

    MapProperties mapProps = tiledMap.m_tiledMap.getProperties();
    String title = (String) mapProps.get("Title");


    String backName = (String) mapProps.get("Back");


    if (backName.equals("back5"))
    {
      m_backSprite = new GameSprite(m_backgrounds.findRegion(backName +"_F1"));
      m_mainBackAnim = new AnimateSpriteFrame(m_backgrounds, new String[] {"back5_F1", "back5_F2", "back5_F3"}, 0.33f, -1);
      m_lightning = new AnimateSpriteFrame(m_backgrounds, new String[] {"back5_F4", "back5_F5"}, 0.2f, 1);
      m_backSprite.runAnimation(m_mainBackAnim);
    } else
    {
      m_backSprite = new GameSprite(m_backgrounds.findRegion(backName));
      m_mainBackAnim = null;
      m_lightning = null;
    }
    this.add(m_backSprite,true);


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
          if(o instanceof Switch) {
        	  ((Switch)o).adapter = switch_adapter;
        	  ((Switch)o).name = obj.getName();
          }else if(o instanceof SwitchHandler) {
        	  switch_adapter.addTarget((SwitchHandler) o);
          } else if (o instanceof Exit)
          {
            m_exit = (Exit)o;
          }
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


    this.add(m_player1);
    this.add(m_player2);
    this.add(m_brain);
    
    //ADD COLLISIONADAPTER After all world objects are set.
   	world.setContactListener(new Box2dCollisionAdapter());
	
	

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
    
    float frameTime = Math.min(deltaTime, 0.25f);
    acumm+=frameTime;
    while(acumm >= 1/60f) {
    	world.step(1/60f, 6, 2);
    	acumm-=1/60f;
    }

    if (m_mainBackAnim != null)
    {
      if (m_lightningTicks > 0) m_lightningTicks--;
      else
      {
        if (Math.random() > 0.8f)
        {
          m_backSprite.stopAllAnimations();
          m_backSprite.runAnimation(m_lightning);
          m_lightningTicks = 13;
        } else
        {
          if (m_mainBackAnim.isRunning() == false)
          {
            m_backSprite.runAnimation(m_mainBackAnim);
          }
          m_lightningTicks = 30;
        }
      }
    }

    if (m_gameState == 10)
    {
      if (m_exit.getState() == 1)
      {
        m_gameState = 20;
        m_player1.removeControl();
        m_player2.removeControl();
        
      }
    } else if (m_gameState == 20)
    {
    
    }


    
    //RELOAD CURRENT LEVEL
    if(inputManager.isTestPressed()) {
    	reset();
    }
    
    if(Gdx.input.isKeyJustPressed(Keys.Q)) {
    	Exit.loadNextLevel();
    }

    if (m_brain.isAlive() == false)
    {
    	reset();
    }
  }
  
  
  public void reset() {
	  Gdx.app.postRunnable(new Runnable() {
		
		@Override
		public void run() {
			MainLayer ml = new MainLayer();
		      ml.loadLevel(m_stage,m_level);
		      replaceActiveLayer(ml);
		}
	});
  }

  @Override
  protected void preCustomDraw()
  {
    m_spriteBatch.setProjectionMatrix(m_defaultMatrix);
    super.drawBackSprites();

    if (tiledMap != null)
      tiledMap.draw();
    
    //DEBUG RENDER BOX2D
    //debug_renderer.render(world, m_defaultMatrix.cpy().scl(Box2dVars.PIXELS_PER_METER));

  }

}

