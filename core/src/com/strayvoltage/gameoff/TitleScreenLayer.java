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

public class TitleScreenLayer extends GameLayer{
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

  public TitleScreenLayer() {
    //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

    //this.setMusic("VS_SA_title_bgm.mp3");

    m_assets = getAssetManager();

    if (m_assets.isLoaded("title.png") == false)
    {
      Gdx.app.debug("TitleScreenLayer", "IntroSprite2.png not loaded. Blocking until loaded.");
      m_assets.finishLoading();
    }

    m_introScreenTexture = m_assets.get("title.png", Texture.class);

    m_introSprite = new GameSprite(m_introScreenTexture);
    m_introSprite.setPosition(0,0);

    m_delayTicks = 0;

    m_inputManager = MasterInputManager.getSharedInstance().getController(0);
    m_inputManager.setViewport(GameMain.getSingleton().m_viewport);

    m_defaultMatrix = m_camera.combined.cpy();
    m_defaultMatrix.setToOrtho2D(0, 0, 1280, 720);

    if (m_assets.isLoaded("g_sprites.txt") == false)
    {
      Gdx.app.debug("TitleScreenLayer", "g_sprites.txt not loaded. Blocking until loaded.");
      m_assets.finishLoading();
    }

    m_gameTextures = m_assets.get("g_sprites.txt", TextureAtlas.class);

    m_pressPlay = new GameSprite(m_gameTextures.findRegion("pressplay"));
    m_pressPlay.setPosition(320-m_pressPlay.getWidth()/2, 200);

    GameAnimateable g = AnimateScaleTo.createPulseSequence(0.75f, 1.0f, 1.1f, -1);
    m_pressPlay.runAnimation(g);

    GameAnimateable g2 = AnimateColorTo.createColorSequence(0.1f, 0.03f, 1, 1, 1, 0.3f, 0f, 0.2f, -1);
    m_pressPlay.runAnimation(g2);

    m_musicStarted = false;


    this.setCameraPosition(640,360);
  }


  @Override
  public void update (float deltaTime) {

    m_buttonDelay++;

    m_inputManager.handleInput(); 

    m_delayTicks++;

    m_pressPlay.animate(deltaTime);

    if (!m_musicStarted)
    {
      //this.loopSound("music", 0.95f);  
      m_musicStarted = true; 
    }

    if (m_inputManager.isSpeedPressed())
    {
      if (m_delayTicks > 60)
        System.exit(0);
    }

    if (m_buttonDelay > 20)
    {
      if (m_inputManager.nextPressed())
      {
        MainLayer l = new MainLayer();
        l.loadLevel(1,1);
        this.replaceActiveLayer(l);
        this.cleanUp();
      }
    }
    
  }

  @Override
  protected void preCustomDraw()
  {

    m_spriteBatch.setProjectionMatrix(m_defaultMatrix);
    m_spriteBatch.begin();
    m_introSprite.draw(m_spriteBatch);

    m_pressPlay.draw(m_spriteBatch);

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
