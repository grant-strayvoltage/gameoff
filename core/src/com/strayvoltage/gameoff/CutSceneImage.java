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
  GameSprite m_nextImage = null;
  GameSprite m_panel = null;
  GameText m_deathsText, m_timeText;
  int m_nextDisplayTicks = 60;

  int m_delayTicks = 0;
  int m_buttonDelay = 0;
  float m_time = 0;
  float m_doneTime = 5000f;
  int m_state = 0;
  boolean m_victory = false;

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

    m_nextImage = new GameSprite(textures.findRegion("next_img"));
    this.add(m_nextImage);
    m_nextImage.setOpacity(0f);
    m_nextImage.setVisible(false);
    m_nextImage.setPosition(950,65);

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
      GameAnimateable an = new AnimateFadeIn(0.5f);
      m_img.runAnimation(an);
      GameAnimateable an2 = new AnimateFadeIn(0.5f);
      m_nextImage.runAnimation(an2);
    }
  }

  public void setVictory()
  {
    AssetManager am = this.getAssetManager();
    m_victory = true;
    TextureAtlas textures = am.get("cut_sprites.txt", TextureAtlas.class);

    m_panel = new GameSprite(textures.findRegion("vic_panel"));
    this.add(m_panel);
    m_panel.setOpacity(0f);
    m_panel.setVisible(false);
    m_panel.setPosition(110,380);
    GameAnimateable an2 = new AnimateFadeIn(0.5f);
    m_panel.runAnimation(an2);
    m_nextDisplayTicks = 180;

    BitmapFont font24 = new BitmapFont(Gdx.files.internal("Font24.fnt"), Gdx.files.internal("Font24.png"), false);
    m_deathsText = new GameText(font24);
    m_timeText = new GameText(font24);

    this.add(m_deathsText);
    this.add(m_timeText);

    m_deathsText.setVisible(false);
    m_timeText.setVisible(false);
    m_deathsText.setOpacity(0);
    m_timeText.setOpacity(0);

    m_deathsText.setPosition(265,523);
    m_timeText.setPosition(265,458);

    GameAnimateable an5 = new AnimateFadeIn(0.5f);
    m_deathsText.runAnimation(an5);


    GameAnimateable an6 = new AnimateFadeIn(0.5f);
    m_timeText.runAnimation(an6);

    String deaths =  GameMain.getSingleton().getDeaths();
    m_deathsText.setText(deaths);

    String timeString =  GameMain.getSingleton().getTime();
    m_timeText.setText(timeString);

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

    if ((m_buttonDelay > 60) && (m_victory))
    {
        m_panel.setVisible(true);
    }

    if ((m_buttonDelay > 85) && (m_victory))
    {
        m_deathsText.setVisible(true);
        m_timeText.setVisible(true);
    }

    if (m_buttonDelay > m_nextDisplayTicks)
    {
      m_nextImage.setVisible(true);
    }

    if (m_buttonDelay > (m_nextDisplayTicks+20))
    {
      if (m_inputManager.nextPressed())
      {
        m_time = m_doneTime + 10f;
      }
    }

    if (m_time > m_doneTime)
    {
        GameAnimateable an = new AnimateFadeOut(0.5f);
        m_img.runAnimation(an);
        GameAnimateable an2 = new AnimateFadeOut(0.5f);

        m_nextImage.stopAllAnimations();
        m_nextImage.runAnimation(an2);
        if (m_panel != null)
        {
          GameAnimateable an3 = new AnimateFadeOut(0.5f);
          m_panel.runAnimation(an3);
          m_deathsText.setVisible(false);
          m_timeText.setVisible(false);
        }
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
