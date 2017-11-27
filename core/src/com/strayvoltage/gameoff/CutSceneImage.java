package com.strayvoltage.gameoff;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
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
import com.badlogic.gdx.assets.AssetManager;
import com.strayvoltage.gamelib.*;
import com.badlogic.gdx.math.*;

public class CutSceneImage extends GameLayer {

  GameInputManager2 m_inputManager;
  Matrix4 m_defaultMatrix = new Matrix4();
  GameLayer m_nextScene = null;

  int m_delayTicks = 0;
  int m_buttonDelay = 0;
  float m_time = 0;
  float m_doneTime = 15.0f;
  int m_state = 0;

  boolean m_scene1Started = false;

  GameSprite m_img;

  public CutSceneImage(String imgName) {

    AssetManager am = this.getAssetManager();

    if (am.isLoaded("cut_sprites.txt") == false)
    {
      am.load("cut_sprites.txt", TextureAtlas.class);
      am.finishLoading();
    }


    TextureAtlas textures = am.get("cut_sprites.txt", TextureAtlas.class);

    m_delayTicks = 0;

    m_inputManager = MasterInputManager.getSharedInstance().getController(0);
    m_inputManager.setViewport(GameMain.getSingleton().m_viewport);

    m_defaultMatrix = m_camera.combined.cpy();
    m_defaultMatrix.setToOrtho2D(0, 0, 1280, 720);

    m_img = new GameSprite(textures.findRegion(imgName));
    m_img.setPosition((1280-m_img.getWidth())/2,(720-m_img.getHeight())/2);
    this.add(m_img);
    m_img.setOpacity(0);


    this.setCameraPosition(1280/2,720/2);

  }

  public void setNextScene(GameLayer gl)
  {
    m_nextScene = gl;
  }

  public void runScene1()
  {
    if (m_scene1Started == false)
    {
      m_scene1Started = true;
      m_time = 0;
      GameAnimateable an = new AnimateFadeIn(1.0f);
      m_img.runAnimation(an);
    }
  }


  @Override
  public void update (float deltaTime) {


    m_time += deltaTime;

    if (m_state == 10)
    {
        if (m_time > 0.95f)
            this.replaceActiveLayer(m_nextScene);

        return;
    }

    m_buttonDelay++;
    m_inputManager.handleInput(); 

    if (m_buttonDelay == 2)
    {
      runScene1();
    }

    if (m_buttonDelay > 64)
    {
      if (m_inputManager.nextPressed())
      {
        m_time = 100;
      }
    }

    if (m_time > m_doneTime)
    {
        GameAnimateable an = new AnimateFadeOut(1.0f);
        m_img.runAnimation(an);
        m_state = 10;
        m_time = 0;
    }
  }

  public void dispose()
  {

  }


  public void cleanUp()
  {

  }
}
