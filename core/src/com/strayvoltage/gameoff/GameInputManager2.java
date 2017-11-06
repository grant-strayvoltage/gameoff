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

public class GameInputManager2 implements InputManager, ControllerListener {

  private boolean testPressed,leftPressed, rightPressed, upPressed, downPressed, firePressed, ouyaControllerConnected, speedPressed, bridgePressed, jumpPressed, triggerPressed;
  private Controller controller;
  public boolean m_touchDevice;
  private TouchDetails[] m_touches = new TouchDetails[4];
  private int moveTouchID, buttonTouchID;
  private Viewport m_viewport;
  private OrthographicCamera m_camera;
  boolean dragStyle = false;
  int dragLastMoveDir = -1;
  public boolean androidTV = false;
  public String m_controllerName = "None";
  public String m_controllerStatus = "None2";
  boolean razer = false;
  boolean razerDesktop = false;
  boolean xbox360 = true;
  boolean controllerConnected =false;

  int buttonA = 96;
  int buttonB = 97;
  int buttonX = 99;
  int buttonY = 100;
  int pauseA = 108;
  int pauseB = 109;
  int triggerL = 102;
  int triggerR = 103;

  int xbox360ButtonA = 0;
  int xbox360ButtonB = 1;
  int xbox360ButtonX = 2;
  int xbox360ButtonY = 3;
  int xbox360ButtonMenu = 7;
  int xbox360triggerL = 4;
  int xbox360triggerR = 5;

  float m_xMoveAxis = 0;
  float m_yMoveAxis = 0;
  float m_thrustAxis = 0;
  float m_xShootAxis = 0;
  float m_yShootAxis = 0;

  boolean m_optionsDialog = false;
  int m_playerNumber = 0;
  boolean m_desktop = false;
  boolean m_android = false;
  boolean m_allowKeyboard = false;

  //left trigger lower is axis 7 and right trigger lower is axis 8
  //left.right button in middle is 109 and 108 (108 right, 109 left)

  //int buttonY = 
  //TODO - depending on size screen - have big or small DPAD
  //my feeling is tablet one is just about 20% too big
  //phone one is just a bit too small

  // Mayu1724
  public GameInputManager2(int playerNumber, boolean allowKeyboard)
  {
    this(null, playerNumber);
    m_allowKeyboard = allowKeyboard;
  }

  public void setAllowKeyboard(boolean b)
  {
    m_allowKeyboard = b;
  }

  public GameInputManager2(Controller controllerIn, int playerNumber)
  {
    m_optionsDialog = false;
    leftPressed = false;
    rightPressed = false;
    upPressed = false;
    downPressed = false;
    firePressed = false;
    testPressed = false;
    ouyaControllerConnected = false;
    speedPressed = false;
    jumpPressed = false;
    bridgePressed = false;
    controller = null;
    m_touchDevice = false;
    triggerPressed = false;
    moveTouchID = -1;
    buttonTouchID = -1;
    m_playerNumber = playerNumber;
    m_desktop = false;
    m_android = false;

    for (int i =0; i < 4; i++)
    {
      m_touches[i] = new TouchDetails();
    }

    m_touchDevice = false;


    switch(Gdx.app.getType()) {
      case Android:
        m_android = true;
        break;
      case Desktop:
        m_desktop = true;
        break;
    }

    this.initialize(controllerIn);
  }

  public String getControllerName()
  {
    return m_controllerName;
  }

  public String getControllerStatus()
  {
    return m_controllerStatus;
  }

  public void setViewport(Viewport v)
  {
    m_viewport = v;
    m_camera = (OrthographicCamera)v.getCamera();
  }

  public void clearTouches()
  {
    for (int i =0; i<4; i++)
    {
      m_touches[i].isDown = false;
      m_touches[i].lastStateDown = false;
      m_touches[i].ticksDown = 0;
      m_touches[i].inDrag = false;
      m_touches[i].tapped = false;
    }
  }

  public void handleInput() {


    leftPressed = false;
    rightPressed = false;
    upPressed = false;
    downPressed = false;
    firePressed = false;
    speedPressed = false;
    jumpPressed = false;
    bridgePressed = false;
    testPressed = false;
    triggerPressed = false;

    if (ouyaControllerConnected) {

      if (!xbox360)
      {
        if (controller.getButton(Ouya.BUTTON_DPAD_LEFT))  {
          leftPressed = true;
        } else if (controller.getButton(Ouya.BUTTON_DPAD_RIGHT)) {
          rightPressed = true;
        }

        if (controller.getButton(Ouya.BUTTON_DPAD_UP))  {
          upPressed = true;
        } else if (controller.getButton(Ouya.BUTTON_DPAD_DOWN))  {
          downPressed = true;
        }
      }

      if ((!razer) && (!xbox360) && (!razerDesktop))
      {

        Gdx.app.debug("GameInputManager2", "Ouya checking.");

        if (controller.getButton(Ouya.BUTTON_O)) {
          jumpPressed = true;
        }

        if ((controller.getButton(Ouya.BUTTON_U)) || (controller.getButton(Ouya.BUTTON_A))) {
          firePressed = true;
        }

        if ((controller.getButton(Ouya.BUTTON_Y)) || (controller.getButton(Ouya.BUTTON_R1))) {
          speedPressed = true;
        }
      } else if (razer)
      {
         Gdx.app.log("GameInputManager2", "razer checking");

        if (controller.getButton(buttonA))
          jumpPressed = true;

        if (controller.getButton(buttonB))
          firePressed = true;

        if (controller.getButton(buttonX))
          bridgePressed = true;

        if ((controller.getButton(pauseA)) || (controller.getButton(pauseB)))
          speedPressed = true;

        if ((controller.getButton(triggerR)) || (controller.getButton(triggerL)))
          triggerPressed = true;

        float v1 = controller.getAxis(8); 
        float v2 = controller.getAxis(0); 
        //m_thrustAxis = controller.getAxis(4); //right trigger
        m_thrustAxis = controller.getAxis(7);
        float thrust2 = controller.getAxis(4);
        if (thrust2 > m_thrustAxis)
          m_thrustAxis = thrust2;


        if ((v1 > 0.75f) || (v2 > 0.5f))
        {
          rightPressed = true;
        } else if ((v1 < -0.75f) || (v2 < -0.5f))
        {
          leftPressed = true;
        }

        m_xMoveAxis = v2;

        v1 = controller.getAxis(9); //dpad up/down
        v2 = controller.getAxis(1);
        if ((v1 > 0.75f) || (v2 > 0.5f))
        {
          downPressed = true;
        } else if ((v1 < -0.75f) || (v2 < -0.5f))
        {
          upPressed = true;
        }

        m_yMoveAxis = v2; 

        m_xShootAxis = controller.getAxis(2);
        m_yShootAxis = -controller.getAxis(3);

      } else if (xbox360)
      {
         Gdx.app.log("GameInputManager2", "xbox360 checking");
        if (controller.getButton(xbox360ButtonA))
          jumpPressed = true;

        if (controller.getButton(xbox360ButtonB))
          firePressed = true;

        if (controller.getButton(xbox360ButtonX))
          bridgePressed = true;

        if (controller.getButton(xbox360ButtonMenu))
          speedPressed = true;

        if ((controller.getButton(xbox360triggerL)) || (controller.getButton(xbox360triggerR)))
          triggerPressed = true;


        //check left
        float v1 = controller.getAxis(0); //up/down on left stick
        float h1 = controller.getAxis(1); //left/right on left stick

        m_thrustAxis = -controller.getAxis(4);
        float thrust2 = controller.getAxis(4);
        if (thrust2 > m_thrustAxis)
          m_thrustAxis = thrust2;


        if (v1 < -0.4f)
          upPressed = true;
        else if (v1 > 0.4f)
          downPressed = true;

        m_yMoveAxis = v1;

        if (h1 < -0.4f)
          leftPressed = true;
        else if (h1 > 0.4f)
          rightPressed = true;

        m_xMoveAxis = h1;

        m_xShootAxis = controller.getAxis(3);
        m_yShootAxis = -controller.getAxis(2);

        PovDirection dirCode = controller.getPov(0);
        if (dirCode == PovDirection.north)
        {
          upPressed = true;
        } else if (dirCode == PovDirection.south)
        {
          downPressed = true;
        } else if (dirCode == PovDirection.east)
        {
          rightPressed = true;
        } else if (dirCode == PovDirection.west)
        {
          leftPressed = true;
        } else if (dirCode == PovDirection.northEast)
        {
          rightPressed = true;
          upPressed = true;
        } else if (dirCode == PovDirection.southEast)
        {
          rightPressed = true;
          downPressed = true;
        } else if (dirCode == PovDirection.southWest)
        {
          leftPressed = true;
          downPressed = true;
        } else if (dirCode == PovDirection.northWest)
        {
          leftPressed = true;
          upPressed = true;
        }
      } else if (razerDesktop)
      {
        Gdx.app.debug("GameInputManager2", "razerDesktop checking");
        if (controller.getButton(xbox360ButtonA))
          jumpPressed = true;

        if (controller.getButton(xbox360ButtonB))
          firePressed = true;

        if (controller.getButton(xbox360ButtonX))
          bridgePressed = true;

        if (controller.getButton(xbox360ButtonMenu))
          speedPressed = true;

        if ((controller.getButton(xbox360triggerL)) || (controller.getButton(xbox360triggerR)))
          triggerPressed = true;


        //check left
        float v1 = controller.getAxis(2); //up/down on left stick
        float h1 = controller.getAxis(3); //left/right on left stick

        m_thrustAxis = -controller.getAxis(4);
        float thrust2 = controller.getAxis(4);
        if (thrust2 > m_thrustAxis)
          m_thrustAxis = thrust2;


        if (v1 < -0.4f)
          upPressed = true;
        else if (v1 > 0.4f)
          downPressed = true;

        m_yMoveAxis = v1;

        if (h1 < -0.4f)
          leftPressed = true;
        else if (h1 > 0.4f)
          rightPressed = true;

        m_xMoveAxis = h1;

        m_xShootAxis = controller.getAxis(1);
        m_yShootAxis = -controller.getAxis(0);

        PovDirection dirCode = controller.getPov(0);
        if (dirCode == PovDirection.north)
        {
          upPressed = true;
        } else if (dirCode == PovDirection.south)
        {
          downPressed = true;
        } else if (dirCode == PovDirection.east)
        {
          rightPressed = true;
        } else if (dirCode == PovDirection.west)
        {
          leftPressed = true;
        } else if (dirCode == PovDirection.northEast)
        {
          rightPressed = true;
          upPressed = true;
        } else if (dirCode == PovDirection.southEast)
        {
          rightPressed = true;
          downPressed = true;
        } else if (dirCode == PovDirection.southWest)
        {
          leftPressed = true;
          downPressed = true;
        } else if (dirCode == PovDirection.northWest)
        {
          leftPressed = true;
          upPressed = true;
        }
      }
    } else if (m_touchDevice)
    {
      for (int i = 0; i < 4; i++)
      {
        m_touches[i].tapped = false;
        m_touches[i].lastStateDown = m_touches[i].isDown;
        m_touches[i].isDown =  Gdx.input.isTouched(i);
        if (m_touches[i].isDown)
        {
          Vector2 loc = new Vector2(Gdx.input.getX(i),Gdx.input.getY(i));
          //m_touches[i].screenX = loc.x;
          //m_touches[i].screenY = m_viewport.getScreenHeight() - loc.y;

          loc = m_viewport.unproject(loc);
          float scaleFactor = m_camera.zoom;

          m_touches[i].currX = (loc.x  - m_viewport.getCamera().position.x) / scaleFactor + 640;
          m_touches[i].currY = (loc.y  - m_viewport.getCamera().position.y) / scaleFactor + 360;

          if (m_touches[i].lastStateDown == false)
          {
            m_touches[i].startX = m_touches[i].currX;
            m_touches[i].startY = m_touches[i].currY;
          }

          m_touches[i].ticksDown++;
        } else
        {
          if (m_touches[i].lastStateDown == true)
          {
            if (m_touches[i].ticksDown < 40)
              m_touches[i].tapped = true;

          }
          m_touches[i].ticksDown = 0;
          m_touches[i].inDrag = false;
        }
      }

      if (moveTouchID >= 0)
      {
        if (m_touches[moveTouchID].isDown == false)
        {
          moveTouchID = -1;
        }
      }

      if (buttonTouchID >= 0)
      {
        if (m_touches[buttonTouchID].isDown == false)
        {
          buttonTouchID = -1;
        }
      }

      if (dragStyle == false)
      {
        //movement touch
        if (moveTouchID >= 0)
        {
          if ((m_touches[moveTouchID].isDown) && (m_touches[moveTouchID].currY  < 400) && (m_touches[moveTouchID].currX < 400))
          {
            /*
            if (m_dpad != null)
            {
              leftPressed = m_dpad.isDirectionPressed(3, m_touches[moveTouchID].currX, m_touches[moveTouchID].currY);
              rightPressed = m_dpad.isDirectionPressed(1, m_touches[moveTouchID].currX, m_touches[moveTouchID].currY);
              upPressed = m_dpad.isDirectionPressed(0, m_touches[moveTouchID].currX, m_touches[moveTouchID].currY);
              downPressed = m_dpad.isDirectionPressed(2, m_touches[moveTouchID].currX, m_touches[moveTouchID].currY);
            }*/
          } else
          {
            moveTouchID = -1;
          }
        } else
        {
          for (int j=0; j < 4; j++)
          {
            if (buttonTouchID != j)
            {
              if (m_touches[j].isDown)
              {
                if (m_touches[j].currX < 400)
                {
                  moveTouchID = j;
                  break;
                }
              }
            }
          }
        }
      } else
      {
        //drag style movement
        if (moveTouchID >= 0)
        {
          float deltaX = m_touches[moveTouchID].currX - m_touches[moveTouchID].startX;
          float deltaY = m_touches[moveTouchID].currY - m_touches[moveTouchID].startY;

          float absDeltaX = Math.abs(deltaX);
          float absDeltaY = Math.abs(deltaY);
          if ((absDeltaX > 2) || (absDeltaY > 2))
          {
            m_touches[moveTouchID].inDrag = true;
            //a new drag
            if (absDeltaX > absDeltaY)
            {
              if (deltaX > 0)
              {
                rightPressed = true;
                dragLastMoveDir = 1;
              } else
              {
                leftPressed = true;
                dragLastMoveDir = 3;
              }
            } else
            {
             if (deltaY > 0)
              {
                upPressed = true;
                dragLastMoveDir = 0;
              } else
              {
                downPressed = true;
                dragLastMoveDir = 2;
              }
            }
          } else
          {
            //no drag...but if still touch down, use last drag direction
            if (m_touches[moveTouchID].inDrag)
            {
              if (dragLastMoveDir == 0)
                upPressed = true;
              else if (dragLastMoveDir == 1)
                rightPressed = true;
              else if (dragLastMoveDir == 2)
                downPressed = true;
              else if (dragLastMoveDir == 3)
                leftPressed = true;

              m_touches[moveTouchID].startX = m_touches[moveTouchID].currX;
              m_touches[moveTouchID].startY = m_touches[moveTouchID].currY;
            }
          }
        } else
        {
          for (int j=0; j < 4; j++)
          {
            if (buttonTouchID != j)
            {
              if (m_touches[j].isDown)
              {
                if (m_touches[j].currX < 500)
                {
                  moveTouchID = j;
                  break;
                }
              }
            }
          }
        }
      }

      //button jump
      /*
      if (m_dpad != null)
      {
        if (buttonTouchID >= 0)
        {
          jumpPressed = m_dpad.isButtonPressed(0, m_touches[buttonTouchID].currX, m_touches[buttonTouchID].currY);
          firePressed = m_dpad.isButtonPressed(1, m_touches[buttonTouchID].currX, m_touches[buttonTouchID].currY);
          speedPressed = m_dpad.isButtonPressed(2, m_touches[buttonTouchID].currX, m_touches[buttonTouchID].currY);

        } else
        {
          for (int j=0; j < 4; j++)
          {
            if (moveTouchID != j)
            {
              if (m_touches[j].isDown)
              {
                if (m_touches[j].currX > 700)
                {
                  buttonTouchID = j;
                  break;
                }
              }
            }
          }
        }
      } */

    }

      if (m_allowKeyboard == false)
      {
        return;
      }


      if (controller == null)
      {
        m_xMoveAxis = 0;
        m_yMoveAxis = 0;
        m_xShootAxis = 0;
        m_yShootAxis = 0;
      }

      if (m_playerNumber == 0)
      {

      //use keyboard
      Input inp = Gdx.input;
      if((inp.isKeyPressed(Keys.LEFT)) || (inp.isKeyPressed(Keys.DPAD_LEFT))) {
        leftPressed = true;
        m_xMoveAxis = -1;
        m_thrustAxis = 1.0f;
      }

      if ((inp.isKeyPressed(Keys.RIGHT)) || (inp.isKeyPressed(Keys.DPAD_RIGHT))) {
        rightPressed = true;
        m_xMoveAxis = 1;
        m_thrustAxis = 1.0f;
      }

      if ((inp.isKeyPressed(Keys.UP)) || (inp.isKeyPressed(Keys.DPAD_UP))) {
        upPressed = true;
        m_yMoveAxis = -1;
        m_thrustAxis = 1.0f;
      }

      if ((inp.isKeyPressed(Keys.DOWN)) || (inp.isKeyPressed(Keys.DPAD_DOWN))) {
        downPressed = true;
        m_yMoveAxis = 1;
        m_thrustAxis = 1.0f;
      }

      if (inp.isKeyPressed(Keys.S))
      {
        m_yShootAxis = -1;
      } else if (inp.isKeyPressed(Keys.W))
      {
        m_yShootAxis = 1;
      }

      if (inp.isKeyPressed(Keys.D))
      {
        m_xShootAxis = 1;
      } else if (inp.isKeyPressed(Keys.A))
      {
        m_xShootAxis = -1;
      }

      if ((inp.isKeyPressed(Keys.ENTER)) || (inp.isKeyPressed(Keys.X)) || (inp.isKeyPressed(Keys.J)) || (inp.isKeyPressed(Keys.DPAD_CENTER)))
      {
        jumpPressed = true;
      }

      if ((inp.isKeyPressed(Keys.Q)) || (inp.isKeyPressed(Keys.ESCAPE)) || (inp.isKeyPressed(Keys.BACK))) 
      {
        speedPressed = true;
      }

      if ((inp.isKeyPressed(Keys.C)) || (inp.isKeyPressed(Keys.SPACE)) || (inp.isKeyPressed(Keys.K))) {
        firePressed = true;
      }

      if (inp.isKeyPressed(Keys.Z))
      {
        triggerPressed = true;
      }

      if (inp.isKeyPressed(Keys.V) || (inp.isKeyPressed(Keys.HOME)) || (inp.isKeyPressed(Keys.PAGE_UP)) || (inp.isKeyPressed(Keys.L)))
      {
        bridgePressed = true;
      }

      if (inp.isKeyPressed(Keys.T))
      {
        testPressed = true;
      }
    } else
    {
      //player 2 keyboard controls
      Input inp = Gdx.input;
      if(inp.isKeyPressed(Keys.A)) {
        leftPressed = true;
        m_xMoveAxis = -1;
        m_thrustAxis = 1.0f;

        m_xShootAxis = -1;
      }

      if (inp.isKeyPressed(Keys.D)) {
        rightPressed = true;
        m_xMoveAxis = 1;
        m_thrustAxis = 1.0f;

        m_xShootAxis = 1;
      }

      if (inp.isKeyPressed(Keys.W)) {
        upPressed = true;
        m_yMoveAxis = -1;
        m_thrustAxis = 1.0f;

         m_yShootAxis = 1;
      }

      if (inp.isKeyPressed(Keys.S)) {
        downPressed = true;
        m_yMoveAxis = 1;
        m_thrustAxis = 1.0f;

        m_yShootAxis = -1;
      }


      if (inp.isKeyPressed(Keys.I))
      {
        jumpPressed = true;
      }

      if (inp.isKeyPressed(Keys.O))
      {
        speedPressed = true;
      }

      if (inp.isKeyPressed(Keys.P)) {
        firePressed = true;
      }

      if (inp.isKeyPressed(Keys.U))
      {
        triggerPressed = true;
      }

      if (inp.isKeyPressed(Keys.Y))
      {
        bridgePressed = true;
      }

    }
    
  }

  public boolean isTriggerPressed()
  {
    return triggerPressed;
  }

  public boolean isBridgePressed()
  {
    return bridgePressed;
  }

  public boolean isTestPressed()
  {
    return testPressed;
  }

  public float getXMoveAxis()
  {
    return m_xMoveAxis;
  }

  public float getYMoveAxis()
  {
    return m_yMoveAxis;
  }

  public float getXShootAxis()
  {
    return m_xShootAxis;
  }

  public float getYShootAxis()
  {
    return m_yShootAxis;
  }

  public float getThrustAxis()
  {
    return m_thrustAxis;
  }

  public boolean isLeftPressed()
  {
    return leftPressed;
  }

  public boolean isRightPressed()
  {
    return rightPressed;
  }

  public boolean isDownPressed()
  {
    return downPressed;
  }

  public boolean isUpPressed()
  {
    return upPressed;
  }

  public boolean isFirePressed()
  {
    return firePressed;
  }

  public boolean isSpeedPressed()
  {
    return speedPressed;
  }

  public boolean isJumpPressed()
  {
    return jumpPressed;
  }

  public boolean nextPressed()
  {
    return (jumpPressed || firePressed || m_touches[0].tapped);
  }

  public boolean anythingPressed()
  {
    return (nextPressed() || upPressed || downPressed || leftPressed || rightPressed || (Math.abs(m_xShootAxis) > 0.5f) || (Math.abs(m_yShootAxis) > 0.5f));
  }

  public boolean buttonTapped(GameButton b)
  {
    if (m_touches[0].tapped)
    {
      //Gdx.app.debug("TEST", "x = " + m_touches[0].currX + "  y = " + m_touches[0].currY);
      //Rectangle box = b.getBoundingRectangle();
      //Gdx.app.debug("TEST", "Button x = " + b.getX() + "  y = " + b.getY());
      return b.getBoundingRectangle().contains(m_touches[0].currX, m_touches[0].currY);
    }

    return false;
  }

  public TouchDetails getTouch(int i)
  {
    return m_touches[i];
  }

   @Override
    public void connected(Controller controller) {
        //hasControllers = true;
    }

    @Override
    public void disconnected(Controller controller) {
        //hasControllers = false;
    }

      @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
      m_controllerStatus = "buttonDown " + buttonCode;
      return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        // This is your analog stick
        // Value will be from -1 to 1 depending how far left/right, up/down the stick is
        // For the Y translation, I use a negative because I like inverted analog stick
        // Like all normal people do! ;)
      m_controllerStatus = "axisMoved " + axisCode + " = " + value;
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        // This is the dpad

      m_controllerStatus = "Controller Status: " + povCode + " = " + value;
        
        return false;
    }

        @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }

    public boolean isValid()
    {
      if (controllerConnected)
      {
        if ((Controllers.getControllers().size == 0) || (controller == null))
          return false;
        else
          return true;
      }

      return true;
    }

    public boolean isControllerConnected()
    {
      if (Controllers.getControllers().size > 0)
        return true;

      return false;

    }

    public void initialize(Controller controllerL)
    {
      if (controllerL == null)
      {
        return;
      }

      Gdx.app.log("GameInputManager2", controllerL.getName());
      m_controllerName = controllerL.getName();
      if(controllerL.getName().equals(Ouya.ID))
      {
        controller = controllerL;
        ouyaControllerConnected = true;
        m_touchDevice = false;
        androidTV = true;
        controllerConnected = true;
      } else if ((controllerL.getName().equals("Razer Serval")) || (controllerL.getName().contains("Shield")))
      {
        if (controllerL.getName().equals("Razer Serval"))
        {
          m_controllerStatus = "Controller Status: Razer Serval";
          Gdx.app.log("GameInputManager2", "Razer Serval Connected");
        } else
          m_controllerStatus = "Controller Status: Shield";

        controller = controllerL;
        ouyaControllerConnected = true;
        m_touchDevice = false;
        //androidTV = true;
        androidTV = false;
        razer = false;
        xbox360 = false;
        razerDesktop = true;
        controllerConnected = true;
        if (m_optionsDialog)
          controller.addListener(this);
      } else if (controllerL.getName().contains("XBOX 360"))
      {
        m_controllerStatus = "Controller Status: Connected XBOX 360";
        controller = controllerL;
        ouyaControllerConnected = true;
        m_touchDevice = false;
        androidTV = false;
        xbox360 = true;
        controllerConnected = true;
        if (m_optionsDialog)
          controller.addListener(this);     
      } else if (controllerL.getName().contains("Xbox One"))
      {
        m_controllerStatus = "Controller Status: Connected XBOX One";
        controller = controllerL;
        ouyaControllerConnected = true;
        m_touchDevice = false;
        androidTV = false;
        xbox360 = true;
        controllerConnected = true;
        if (m_optionsDialog)
          controller.addListener(this);      
      } else if ((controllerL.getName().contains("NVIDIA")) || (controllerL.getName().contains("Nvidia")))
      {
        m_controllerStatus = "Controller Status: Connected Nvidia";
        controller = controllerL;
        ouyaControllerConnected = true;
        m_touchDevice = false;
        androidTV = true;
        razer = true;
        if (m_optionsDialog)
          controller.addListener(this);   
      } else if (m_desktop)
      {
        //for now will assume if Android and a controller is connected, be like Razer
        m_controllerStatus = "Controller Status: Unknown Controller";
        controller = controllerL;
        ouyaControllerConnected = true;
        m_touchDevice = false;
        androidTV = true;
        razer = true;
        controllerConnected = true;
        //keep looping to see if find exact fit.
        if (m_optionsDialog)
          controller.addListener(this);
      } else
      {
        //controller attached, desktop, assume like xbox360
        m_controllerStatus = "Controller Status: Unknown Controller Connected";
        controller = controllerL;
        ouyaControllerConnected = true;
        m_touchDevice = false;
        androidTV = false;
        xbox360 = true;
        controllerConnected = true;
        if (m_optionsDialog)
          controller.addListener(this);
      }
  }

  public void cleanUp()
  {
    if (controller != null)
    {
      controller.removeListener(this);
      controller = null;
    }
  }
}