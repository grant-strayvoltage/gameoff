package com.strayvoltage.gameoff;

import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.strayvoltage.gamelib.GameLayer;
import com.strayvoltage.gamelib.GameMain;

public class GameOff extends GameMain {
  
	public static final boolean DEBUG = false;
	
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

	  this.addSound("toggle", "VS_GO_menu_move.wav");
    this.addSound("click", "VS_GO_menu_select.wav");
    this.addSound("brainJump", "VS_GO_brain_jump.wav");
    this.addSound("brainMove","VS_GO_brain_move.wav");
    this.addSound("brainOff","VS_GO_brain_off.wav");
    this.addSound("brainOn","VS_GO_brain_on.wav");
    this.addSound("brainLand","VS_GO_brain_land.wav");
    this.addSound("brainDie","VS_GO_brain_die.wav");

    this.addSound("1Jump", "VS_GO_p1_jump.wav");
    this.addSound("1Move","VS_GO_p1_move.wav");
    this.addSound("1Land","VS_GO_p1_land.wav");
    this.addSound("cyborgDie","VS_GO_cyborg_die.wav");
    this.addSound("2Jump", "VS_GO_p2_jump.wav");
    this.addSound("2Move","VS_GO_p2_move.wav");
    this.addSound("2Land","VS_GO_p2_land.wav");

    this.addSound("smasherDrop","VS_GO_smasher_drop.wav");
    this.addSound("smasherLoop","VS_GO_smasher_dropping.wav");
    this.addSound("switchToggle","VS_GO_switch_toggle.wav");
    this.addSound("exit","VS_GO_exit.wav");

    this.addSound("boom","VS_GO_boom.wav");
    this.addSound("bounce","VS_GO_bounce.wav");


      loadGameDefaults();
  }

  public void loadGameDefaults()
  {
      this.setGlobal("m_level", "1");
      this.setGlobal("m_stage","1");
      this.setGlobal("deaths","0");
      this.setGlobal("time", "0");
      this.setGlobal("game_complete", "false");
  }

  public void resize(int width, int height) {
    m_viewport.update(width, height, true);
  }

}