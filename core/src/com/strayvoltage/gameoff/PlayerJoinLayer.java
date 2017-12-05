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

public class PlayerJoinLayer extends GameLayer {

  GameInputManager2 m_inputManager1, m_inputManager2;
  Matrix4 m_defaultMatrix = new Matrix4();

  int m_delayTicks = 0;
  int m_buttonDelay = 0;
  int m_buttonDelay2 = 0;
  float m_time = 0;
  int m_state = 0;

  GameSprite m_img;

  GameSprite m_p1Controls, m_p2Controls;
  int m_p1 = 0;
  int m_p2 = 0;
  TextureAtlas m_textures;

  public PlayerJoinLayer() {

    AssetManager am = this.getAssetManager();

    if (am.isLoaded("player_join_back.png") == false)
    {
      am.finishLoading();
    }

    m_delayTicks = 0;

    m_inputManager1 = MasterInputManager.getSharedInstance().getController(0);
    m_inputManager1.setViewport(GameMain.getSingleton().m_viewport);

    m_inputManager2 = MasterInputManager.getSharedInstance().getController(1);
    m_inputManager2.setViewport(GameMain.getSingleton().m_viewport);

    m_defaultMatrix = m_camera.combined.cpy();
    m_defaultMatrix.setToOrtho2D(0, 0, 1280, 720);

    m_img = new GameSprite(am.get("player_join_back.png", Texture.class));
    m_img.setPosition((1280-m_img.getWidth())/2,(720-m_img.getHeight())/2);
    this.add(m_img);
    m_img.setOpacity(0);

    m_textures = am.get("game_sprites.txt", TextureAtlas.class);

    m_p1Controls = new GameSprite(m_textures.findRegion("press_to_join"));
    this.add(m_p1Controls);
    m_p1Controls.setOpacity(0f);
    m_p1Controls.setPosition(177,166);

    m_p2Controls = new GameSprite(m_textures.findRegion("press_to_join"));
    this.add(m_p2Controls);
    m_p2Controls.setOpacity(0f);
    m_p2Controls.setPosition(782,166);

    GameAnimateable an = new AnimateFadeIn(0.5f);
    m_img.runAnimation(an);
    GameAnimateable an2 = new AnimateFadeIn(0.5f);
    m_p1Controls.runAnimation(an2);
    GameAnimateable an3 = new AnimateFadeIn(0.5f);
    m_p2Controls.runAnimation(an3);

    this.setCameraPosition(1280/2,720/2);

  }


    public void setPlayerController(int p, int c)
    {
        TextureRegion r = m_textures.findRegion("press_to_join");
        if (c == 1)
            r = m_textures.findRegion("keyboard1");
        else if (c == 2)
            r = m_textures.findRegion("keyboard2");
        else if (c == 3)
            r = m_textures.findRegion("controller1");
        else if (c == 4)
            r = m_textures.findRegion("controller2");
        
        if (p == 1)
        {
            m_p1Controls.setRegion(r);
            m_p1 = c;
        }
        else
        {
            m_p2Controls.setRegion(r);
            m_p2 = c;
        }
    }
  

  @Override
  public void update (float deltaTime) {

    m_time += deltaTime;

    m_buttonDelay++;
    m_buttonDelay2++;
    m_inputManager1.handleInput(); 
    m_inputManager2.handleInput(); 

    if (m_state < 5)
    {
        if (m_inputManager1.isJumpPressed() && (m_buttonDelay > 15))
        {
            m_buttonDelay = 0;
            //keybooad 1 = 1
            //keyboard 2 = 2
            //controller 1 = 3
            //controler 2 = 4
            int cd = 3;

            this.playSound("click",0.6f);

            if (m_inputManager1.m_keyBoardUsed)
                cd = 1;

            if ((m_p1 == cd) || (m_p2 == cd))
            {
                if (m_p1 == cd)
                {
                    setPlayerController(1,0);
                }

                if (m_p2 == cd)
                {
                    setPlayerController(2,0);
                }
            } else
            {
                if (m_p1 == 0)
                {
                    setPlayerController(1,cd);
                }
                if ((m_p2 == 0) || (m_p1 == m_p2))
                {
                    setPlayerController(2,cd);
                }
            }
        }

        if (m_inputManager2.isJumpPressed() && (m_buttonDelay2 > 15))
        {
            m_buttonDelay2 = 0;
            //keybooad 1 = 1
            //keyboard 2 = 2
            //controller 1 = 3
            //controler 2 = 4
            int cd = 4;
            this.playSound("click",0.6f);

            if (m_inputManager2.m_keyBoardUsed)
                cd = 2;

            if ((m_p1 == cd) || (m_p2 == cd))
            {
                if (m_p1 == cd)
                {
                    setPlayerController(1,0);
                }

                if (m_p2 == cd)
                {
                    setPlayerController(2,0);
                }
            } else
            {
                if (m_p1 == 0)
                {
                    setPlayerController(1,cd);
                }
                if ((m_p2 == 0) || (m_p1 == m_p2))
                {
                    setPlayerController(2,cd);
                }
            }
        }

        if ((m_inputManager1.isFirePressed()) || (m_inputManager2.isFirePressed()))
        {
            if (m_buttonDelay > 15)
            {
                if ((m_p1 > 0) && (m_p2 > 0))
                {
                    this.playSound("click",0.6f);
                     m_state = 5;
                }
                   
            }
        }
    }

    if (m_state == 5)
    {
        GameAnimateable an = new AnimateFadeOut(0.5f);
        m_img.runAnimation(an);
        GameAnimateable an2 = new AnimateFadeOut(0.5f);
        GameAnimateable an3 = new AnimateFadeOut(0.5f);

        m_p1Controls.stopAllAnimations();
        m_p2Controls.stopAllAnimations();
        m_p1Controls.runAnimation(an2);
        m_p2Controls.runAnimation(an3);

        m_state = 10;
        m_time = 0;
    } else if (m_state == 10)
    {
        if (m_time > 0.45f)
        {
            int scene = Integer.parseInt(this.getGlobal("m_stage"));
            int level = Integer.parseInt(this.getGlobal("m_level"));
            MasterInputManager.getSharedInstance().setControllerCodes(m_p1,m_p2);


            MainLayer l = new MainLayer();
            l.loadLevel(scene, level);
            
            //todo: set controllers up right based on config

            this.replaceActiveLayer(l);
            this.cleanUp();
        }
    }
  }

  public void dispose()
  {

  }


  public void cleanUp()
  {

  }
}
