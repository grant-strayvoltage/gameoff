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

public class SplashScreen extends GameLayer {
  Matrix4 m_defaultMatrix = new Matrix4();
  GameSprite m_five;
  Texture m_texture = null;

  private int m_state = 0;
  private float m_stateTime = 0;
  GameAnimateable seq1 = null;
  boolean m_soundPlaying = false;
  AssetManager m_assets = null;

  public SplashScreen() {
    //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

    m_assets = getAssetManager();

    m_texture = new Texture("sv_logo.png");
    m_five = new GameSprite(m_texture);
    m_five.setPosition(170,250);
    this.add(m_five);
    m_five.setOpacity(0);

    AnimateDelay d0 = new AnimateDelay(0.5f);
    AnimateFadeIn in1 = new AnimateFadeIn(0.75f);
    AnimateDelay d1 = new AnimateDelay(1.5f);
    AnimateFadeOut out1 = new AnimateFadeOut(0.5f);
    AnimateDelay d2 = new AnimateDelay(0.5f);

    GameAnimateable[] a1 = {d0,in1,d1,out1,d2};

    seq1 = new GameAnimationSequence(a1,1);

    m_five.runAnimation(seq1);

    this.setCameraPosition(640,360);

    m_assets.load("title.png", Texture.class);
    m_assets.load("g_sprites.txt", TextureAtlas.class);
    m_assets.load("game_sprites.txt", TextureAtlas.class);
    m_assets.load("backgrounds.txt", TextureAtlas.class);
    //m_assets.load("game_select_back.png", Texture.class);

  }

  @Override
  public void update (float deltaTime) {

      //Uncomment the below for proper splash screen
      m_stateTime += deltaTime;
      if (m_state == 0)
      {
        if (!m_soundPlaying)
        {
          if (m_stateTime > 0.1f)
          {
            //this.playSound("splash", 0.7f);
            m_soundPlaying = true;
          }
        }

        if (m_stateTime > 2.0f)
        {
          GameMain.getSingleton().finishSetup();
          m_state = 1;
        }
      } else if (m_state == 1)
      {
        m_assets.update();
        if (seq1.isRunning() == false)
        {
          GameLayer titleLayer = new TitleScreenLayer();
          this.replaceActiveLayer(titleLayer);
          this.cleanUp();
        }
      } 
  }

  public void dispose()
  {
    //m_texture.dispose();
    //m_texture = null;
  }

  public void cleanUp()
  {
    m_texture.dispose();
    m_texture = null;
    //this.removeSound("splash");
  }
}
