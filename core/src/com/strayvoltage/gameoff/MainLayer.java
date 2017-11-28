package com.strayvoltage.gameoff;

import java.util.ArrayList;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
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
	
<<<<<<< HEAD
	public static final int MAX_STAGES = 4; //CHANGE IF YOU ADD MORE STAGES
=======
	public static final int MAX_STAGES = 3; //CHANGE IF YOU ADD MORE STAGES
>>>>>>> fc55bbbda7dcf8d96a48f013e66e4c69906b7317
	public static final int MAX_LEVELS_PER_STGE = 8; //CHANGE IF YOU ADD or REMOVE LEVELS --

boolean m_musicStarted = false;
int m_gameState = 5;
Exit m_exit;
int m_stage, m_level, gameState;
AssetManager m_assets;
GameInputManager2 inputManager;
Matrix4 m_defaultMatrix;
public GameTileMap tiledMap = null;
float stateTime;
float tw = 16f;
float th = 16f;
int m_mapWidth, m_mapHeight;
static BitmapFont m_font16 = null;
static BitmapFont m_font24 = null;
static BitmapFont m_font32 = null;
TextureAtlas m_sprites = null;
public ArrayList<GameMapObject> m_gameMapObjects = new ArrayList<GameMapObject>();

float gameTime = 0f;
static public World world;
static public Box2DDebugRenderer debug_renderer;
PowerUnit m_brain = null;

Player m_player1, m_player2;

SwitchAdapter switch_adapter;//handles all switches
TextureAtlas m_backgrounds;
GameAnimateable m_mainBackAnim, m_lightning;
GameSprite m_backSprite;
GameSprite m_titleBackSprite;

int m_lightningTicks = 90;
private float acumm = 0; //box2d var
GameText m_titleText;
GameSprite m_fadeOutSprite;
GameText m_statsText;

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

    //m_fadeOutTexture = m_assets.get("fade_out.png", Texture.class);
    m_fadeOutSprite = new GameSprite(m_assets.get("fade_out.png", Texture.class));
    m_fadeOutSprite.setOpacity(0);

    

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

  public void fadeIn(float duration)
  {
    m_fadeOutSprite.setOpacity(1.0f);
    m_fadeOutSprite.runAnimation(new AnimateFadeOut(duration));
  }

  public void fadeToBlack(float duration)
  {
    m_fadeOutSprite.setOpacity(0f);
    m_fadeOutSprite.runAnimation(new AnimateFadeIn(duration));
  }

  public void stopAllLoops()
  {
      stopSound("brainMove");
      stopSound("smasherLoop");
      stopSound("smasherDrop");
      stopSound("1Move");
      stopSound("2Move");
      stopSound("fan");
      
        //add all other looping sounds here
  }

  public void doVictory()
  {
        CutSceneImage scene1 = new CutSceneImage("vic1");
        CutSceneImage scene2 = new CutSceneImage("vic2");
        scene2.setVictory();

        scene1.setNextScene(scene2);
        scene2.setNextScene(new TitleScreenLayer());
        this.replaceActiveLayer(scene1);
  }

  private void setupTileMapBox2D()
  {

	final int SOLID_TILE_START = 32; // the start of all solid tiles(end of hazards)
	final int HAZARD_TILE_START = 3; //the start of all hazard tiles
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

        if(cellid >= SOLID_TILE_START) {

        	if(chainVectors.size == 0) {
        		//setup first vertex
        		startx = tx;
        		chainVectors.add(new Vector2(0,0));
        		chainVectors.add(new Vector2(0,tilesize));
        	}
        	chainVectors.add(new Vector2((chainVectors.peek().x+tilesize),tilesize));
          //Gdx.app.log("MainLayer","(" + tx + ", " + ty + " = " + cellid + " ADDED");
        }
        
        if ((cellid < SOLID_TILE_START || tx+1 == m_mapWidth ) && chainVectors.size>0){
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
        
        if (cellid > 2 && cellid < 32)//spikes.. add other hazard here

        {
            bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            boxY = ty;
            PolygonShape shape = new PolygonShape();
            
            Vector2[] points = new Vector2[4];

            float lx = 0;
            float rx = tilesize;
            float bmy = 0;
            float tpy = tilesize;

            if ((cellid == 4) || (cellid == 5))
            {
              lx = 6;
              rx = 26;
            } else
            {
              lx = 6;
              rx = 26;
              tpy = 20;
              bmy = 12;
            }

            points[0] = new Vector2(lx,bmy).scl(1f/Box2dVars.PIXELS_PER_METER);
            points[1] = new Vector2(lx,tpy).scl(1f/Box2dVars.PIXELS_PER_METER);
            points[2] = new Vector2(rx,tpy).scl(1f/Box2dVars.PIXELS_PER_METER);
            points[3] = new Vector2(rx,bmy).scl(1f/Box2dVars.PIXELS_PER_METER);
            
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

    this.saveGame();
    	
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
    m_gameMapObjects.add(m_brain);
    m_brain.init(null,m_sprites);
    m_brain.addToWorld(world);

    m_player1 = new Player(m_sprites,1,inputManager);
    //this.add(m_player1);

    m_player2 = new Player(m_sprites,2, inputManager);
    //this.add(m_player2);

    m_brain.pickUp(m_player1,false);

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

    m_brain.m_map = tiledMap;

    m_player1.setMap(tiledMap, m_player2,24,40,m_brain,1);
    m_player2.setMap(tiledMap, m_player1,8.5f,15,m_brain,2);
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

    int byo = getInt("backOffY", mapProps);
    int bxo = getInt("backOffX", mapProps);
    m_backSprite.setPosition(bxo,byo);

    m_titleBackSprite = new GameSprite(m_backgrounds.findRegion("title_back"));
    


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
        m_player1.setBodyPosition(px,py-18);
        m_player1.setPositionToBody();
      } else if (t.equals("PlayerStart2"))
      {
        m_player2.setBodyPosition(px,py);
        m_player2.setPositionToBody();
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
          if (o instanceof Fan)
          {
            ((Fan)o).addFanSprite(this,px,py);
          }
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

    this.add(m_titleBackSprite);
    m_titleBackSprite.setPosition(140,580);

    m_titleText = new GameText(m_font24, 980);
    m_titleText.setText(title);
    m_titleText.setPosition(150,655);
    this.add(m_titleText);

    String timeString = GameMain.getSingleton().getTime();
    String deaths = GameMain.getSingleton().getDeaths();

    m_statsText = new GameText(m_font16, 980);
    m_statsText.setText("Deaths: " + deaths + "      Time: " + timeString);
    m_statsText.setPosition(150,607);
    this.add(m_statsText);
    
    //ADD COLLISIONADAPTER After all world objects are set.
   	world.setContactListener(new Box2dCollisionAdapter());
	
    m_fadeOutSprite.setOpacity(1.0f);
    fadeIn(0.6f);

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

    if (m_gameState == 5)
    {
      m_fadeOutSprite.animate(deltaTime);

      if ((stateTime > 0.25f) && (!m_musicStarted))
      {
        this.setMusic("VS_GO_gameplay_BG.ogg");
        loopSound("music",1f);
        m_musicStarted = true;
      }

      //title displaying, waiting to fade
      if ((stateTime > 0.62f) && (inputManager.anythingPressed()))
      {
        m_gameState = 10;
        m_titleBackSprite.runAnimation(new AnimateMoveTo(0.5f,m_titleBackSprite.getX(), m_titleBackSprite.getY(), m_titleBackSprite.getX(), m_titleBackSprite.getY() + 200f));
        m_titleText.runAnimation(new AnimateMoveTo(0.5f, m_titleText.getX(), m_titleText.getY(), m_titleText.getX(), m_titleText.getY() + 200f));
        m_statsText.runAnimation(new AnimateMoveTo(0.5f, m_statsText.getX(), m_statsText.getY(), m_statsText.getX(), m_statsText.getY() + 200f));
        stateTime = 0;
        startLevel();
      }

      return;
    }

    if (gameState < 10)
      gameTime += deltaTime;

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
        m_gameState = 15;
        m_player1.doExit(m_exit.getX() + m_exit.getWidth()/2);
        m_player2.doExit(m_exit.getX() + m_exit.getWidth()/2);
        stateTime = 0;
        GameMain.getSingleton().setGlobal("time","" + gameTime);
      }
    } else if (m_gameState == 15)
    {
      if (stateTime > 2.4f)
      {
        fadeToBlack(0.5f);
        m_gameState = 20;
        stateTime = 0;
      }
    } else if (m_gameState == 20)     
    {
      m_fadeOutSprite.animate(deltaTime);
      if (stateTime > 0.5f)
      {
        if ((m_stage == 5) && (m_level == 8))
        {
          doVictory();
        } else
          m_exit.loadNextLevel();

        stopAllLoops();
      }
    }
    
    //RELOAD CURRENT LEVEL
    if((inputManager.isTestPressed()) || (inputManager.isSpeedPressed())) {
      GameMain.getSingleton().addDeath();
    	reset();
      stopAllLoops();
    }
    
    //LOAD NEXT LEVEL
    if(Gdx.input.isKeyJustPressed(Keys.Q)) {
    	Exit.loadNextLevel();
      stopAllLoops();
      //m_exit.m_state = 1;
    }

    if (!m_brain.isAlive()||!m_player1.isAlive()||!m_player2.isAlive())
    {
    	reset();
    }
  }

  public int getInt(String key, MapProperties mp)
  {
    //if missing, or the default value from Tiled, assume 1
    //If you don't want false as the default value, set the value explicitly in Tiled for this object.
    Integer i = (Integer)mp.get(key);
    if (i == null) return 0;
    return i.intValue();
  }
  
  public void startLevel()
  {
    m_player1.startLevel();
    m_player2.startLevel();
    String tm = GameMain.getSingleton().getGlobal("time");
    gameTime  = Float.parseFloat(tm);
  }
  
  public void reset() {
	  Gdx.app.postRunnable(new Runnable() {
		
		@Override
		public void run() {
      GameMain.getSingleton().setGlobal("time","" + gameTime);
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
    
    if(GameOff.DEBUG)
    	debug_renderer.render(world, m_defaultMatrix.cpy().scl(Box2dVars.PIXELS_PER_METER));

  }

   @Override
  protected void postCustomDraw()
  {
    m_spriteBatch.setProjectionMatrix(m_defaultMatrix);
    m_spriteBatch.begin();

    if (m_fadeOutSprite.getOpacity() > 0)
      m_fadeOutSprite.draw(m_spriteBatch);

    m_spriteBatch.end();

  }

}

