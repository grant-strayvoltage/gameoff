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
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.assets.AssetManager;

public class GameOff extends GameMain {
  
  public void setupGame()
  {

	m_camera = new OrthographicCamera(640,360);
	m_camera.update();

	float w = Gdx.graphics.getWidth();
	float h = Gdx.graphics.getHeight();

	m_viewport = new FitViewport(640,360,m_camera);
	m_viewport.update((int)w, (int)h, true);

    //this.addSound("splash", "logo2.wav");

    //Gdx.app.setLogLevel(Gdx.app.LOG_DEBUG);

    // TODO: Uncomment below when done ENEMY ROSTER
     GameLayer splashLayer = new SplashScreen();
     this.pushGameLayer(splashLayer);

  }

  public void finishSetup()
  {

	  this.addSound("toggle", "VS_SA_menu_move.wav");
      this.addSound("click", "VS_SA_menu_select.wav");

      loadGameDefaults();
  }

  public void loadGameDefaults()
  {
      this.setGlobal("Level", "1");
  }

  public void resize(int width, int height) {
    m_viewport.update(width, height, true);
  }

}