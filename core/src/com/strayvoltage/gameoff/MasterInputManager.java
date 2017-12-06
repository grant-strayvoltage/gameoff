package com.strayvoltage.gameoff;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.strayvoltage.gamelib.*;

public class MasterInputManager {

  private GameInputManager2[] m_controllers = new GameInputManager2[2];
  private static MasterInputManager m_instance = null;
  public int m_p1Code, m_p2Code;

  public MasterInputManager()
  {
    this.reInitialize();
  }

  public static MasterInputManager getSharedInstance()
  {
    if (m_instance == null)
    {
      m_instance = new MasterInputManager();
    }

    return m_instance;
  }
  
  
  public void reInitialize()
  {
    m_controllers[0] = null;
    m_controllers[1] = null;
    int controllerNumber = 0;
    for (Controller controllerL : Controllers.getControllers()) {
      Gdx.app.log("MasterInputManager", controllerL.getName());
      if (controllerNumber < 2)
      {
        if(controllerL.getName().equals(Ouya.ID))
        {
          m_controllers[controllerNumber] = new GameInputManager2(controllerL,controllerNumber);
          controllerNumber++;
        } else if ((controllerL.getName().equals("Razer Serval")) || (controllerL.getName().contains("Shield")))
        {
          m_controllers[controllerNumber] = new GameInputManager2(controllerL,controllerNumber);
          controllerNumber++;
        } else if (controllerL.getName().contains("XBOX 360"))
        {
          m_controllers[controllerNumber] = new GameInputManager2(controllerL,controllerNumber);
          controllerNumber++;       
        } else if (controllerL.getName().toLowerCase().contains("xbox one") && controllerL.getName().toLowerCase().contains("windows"))
        {
          m_controllers[controllerNumber] = new GameInputManager2(controllerL,controllerNumber);
          controllerNumber++; 
        } else if ((controllerL.getName().contains("NVIDIA")) || (controllerL.getName().contains("Nvidia")))
        {
          m_controllers[controllerNumber] = new GameInputManager2(controllerL,controllerNumber);
          controllerNumber++; 
        } else if ((controllerL.getName().contains("2-axis")))
        {
          m_controllers[controllerNumber] = new GameInputManager2(controllerL,controllerNumber);
          controllerNumber++;           
        }
        //how to handle STEAM controller
        //other android TV controllers?
      }
    }

    if (m_controllers[0] == null)
    {
      m_controllers[0] = new GameInputManager2(0, true);
      m_controllers[1] = new GameInputManager2(1, true);
    } else if (m_controllers[1] == null)
    {
      //one controller connected...allow player 2 to use the keyboard
      m_controllers[0].setAllowKeyboard(true);
      m_controllers[1] = new GameInputManager2(1, true);
    } else
    {
      //two controllers- let player one use keyboard
      m_controllers[0].setAllowKeyboard(true);
      m_controllers[1].setAllowKeyboard(true);
    }
  }

  public void setControllerCodes(int p1, int p2)
  {
    m_p1Code = p1;
    m_p2Code = p2;
  }

  public int getNumControllers()
  {

    if (m_controllers[1] != null)
      return 2;
    
    return 1;

  }

  public GameInputManager2 getController(int i)
  {
    return m_controllers[i];
  }
}