package com.strayvoltage.gameoff;

import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.strayvoltage.gamelib.GameLayer;
import com.strayvoltage.gamelib.GameMain;

public class GameOff extends GameMain {
  
  public void setupGame()
  {

	m_camera = new OrthographicCamera(1280,720);
	m_camera.update();

	float w = Gdx.graphics.getWidth();
	float h = Gdx.graphics.getHeight();

	m_viewport = new FitViewport(1280,720,m_camera);
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
      this.setGlobal("game_complete", "false");
  }

  public void resize(int width, int height) {
    m_viewport.update(width, height, true);
  }

}