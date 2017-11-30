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
import com.badlogic.gdx.math.Matrix4;
import com.strayvoltage.gamelib.*;
import com.badlogic.gdx.assets.AssetManager;

public class TitleScreenLayer extends GameLayer implements GameMenuListener {
  GameInputManager2 m_inputManager;
  Matrix4 m_defaultMatrix = new Matrix4();
  GameSprite m_introSprite;
  GameSprite m_pressPlay;
  float xx,yy; 
  boolean m_musicStarted = false;
  static TextureAtlas m_gameTextures = null;
  static Texture m_introScreenTexture = null;
  int m_delayTicks = 0;
  int m_buttonDelay = 30;
  AssetManager m_assets = null;
  GameMenu m_menu;

  public TitleScreenLayer() {
    //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

    //this.setMusic("VS_SA_title_bgm.mp3");

    m_assets = getAssetManager();
    m_assets.finishLoading();

    m_introScreenTexture = m_assets.get("title.png", Texture.class);

    m_introSprite = new GameSprite(m_introScreenTexture);
    m_introSprite.setPosition(0,0);

    GameSprite titleText = new GameSprite(m_assets.get("escape_title.png", Texture.class));
    this.add(titleText);
    titleText.setOpacity(0);
    GameAnimateable an = new AnimateFadeIn(2.0f);
    titleText.runAnimation(an);

    m_delayTicks = 0;

    m_inputManager = MasterInputManager.getSharedInstance().getController(0);
    m_inputManager.setViewport(GameMain.getSingleton().m_viewport);

    m_defaultMatrix = m_camera.combined.cpy();
    m_defaultMatrix.setToOrtho2D(0, 0, 1280, 720);

    if (m_assets.isLoaded("game_sprites.txt") == false)
    {
      m_assets.finishLoading();
    }

    m_gameTextures = m_assets.get("game_sprites.txt", TextureAtlas.class);

    GameImageButton newGame = new GameImageButton(m_gameTextures, "newButton");
    GameImageButton contGame = new GameImageButton(m_gameTextures, "continue");

    int defaultButton = 1;

    if (GameMain.getSingleton().doesGameExist(0)) defaultButton = 2;
    else
    {
      contGame.setDisabled(true);
    }

    m_menu = new GameMenu(newGame, contGame, defaultButton, 25, false, m_inputManager, this);
    this.add(m_menu);
    m_menu.setPosition(440,65);

    m_menu.setOpacity(0);
    GameAnimateable an2 = new AnimateFadeIn(2.0f);
    m_menu.runAnimation(an2);


    m_musicStarted = false;
    this.setCameraPosition(640,360);
  }


  @Override
  public void update (float deltaTime) {

    m_buttonDelay++;

    m_inputManager.handleInput(); 

    m_delayTicks++;

    if (!m_musicStarted)
    {
      this.setMusic("VS_GO_title_bgm.ogg");
      this.loopSound("music",0.85f); 
      m_musicStarted = true; 
    }

    if (m_inputManager.isSpeedPressed())
    {
      if (m_delayTicks > 60)
        System.exit(0);
    }
  }

  public void buttonSelected(int buttonNum, boolean mainClick)
  {
    if (m_delayTicks < 50) return;

    if (buttonNum == 1)
    {
      this.eraseGame(0);
      this.loadGameDefaults();
      this.setGlobal("m_stage","1"); 
      this.setGlobal("m_level", "1");
      this.loadGame(0);

      
      if(GameOff.DEBUG) {
        MainLayer l = new MainLayer();
        l.loadLevel(4,1);
        this.replaceActiveLayer(l);
        this.cleanUp();
      }else
      {
        MainLayer l = new MainLayer();
        l.loadLevel(1,1);
 
        CutSceneImage scene1 = new CutSceneImage("cut1");
        CutSceneImage scene2 = new CutSceneImage("cut2");
        CutSceneImage scene3 = new CutSceneImage("cut3");
        CutSceneImage scene4 = new CutSceneImage("cut4");
        CutSceneImage scene5 = new CutSceneImage("cut5");
        CutSceneImage scene6 = new CutSceneImage("cut6");
        CutSceneImage scene7 = new CutSceneImage("cut7");

        scene1.setNextScene(scene2);
        scene2.setNextScene(scene3);
        scene3.setNextScene(scene4);
        scene4.setNextScene(scene5);
        scene5.setNextScene(scene6);
        scene6.setNextScene(scene7);
        scene7.setNextScene(l);
        this.replaceActiveLayer(scene1);
        this.cleanUp();
      }
      


    } else if (buttonNum == 2)
    {
      this.loadGameDefaults();
      this.loadGame(0);

      int scene = Integer.parseInt(this.getGlobal("m_stage"));
      int level = Integer.parseInt(this.getGlobal("m_level"));

      MainLayer l = new MainLayer();
      l.loadLevel(scene, level);
      
      this.replaceActiveLayer(l);
      this.cleanUp();

    } 
  }

  @Override
  protected void preCustomDraw()
  {

    m_spriteBatch.setProjectionMatrix(m_defaultMatrix);
    m_spriteBatch.begin();
    m_introSprite.draw(m_spriteBatch);

    m_menu.draw(m_spriteBatch);

    m_spriteBatch.end();
  }

  public void dispose()
  {
    //m_texture.dispose();
    //m_texture = null;
  }

  public void cleanUp()
  {
    Gdx.app.debug("space", "TitleScreenLayer cleanUp called");
  }
}
