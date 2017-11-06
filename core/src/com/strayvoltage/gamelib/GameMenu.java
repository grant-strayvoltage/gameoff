package com.strayvoltage.gamelib;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameMenu extends GamePanel {

  int m_selectedButton = -1;
  int m_lastSelectedButton = -1;
  int m_numButtons = 2;

  boolean m_align = false;
  float m_spacer = 10;
  int m_ignoreTicks = 30;
  int m_moveIgnoreTicks = 30;

  boolean m_allDirections = false;

  InputManager m_inputManager;
  GameMenuListener m_listener;


  public  GameMenu (GameButton button1, float spacer, boolean verticalAlign, InputManager inputManager, GameMenuListener listener) {
      super();
      this.add(button1);
      button1.setSelected(true);

      m_selectedButton = 1;

      m_align = verticalAlign;
      m_spacer = spacer;

      m_inputManager = inputManager;
      m_numButtons = 1;

      m_listener = listener;
      m_ignoreTicks = 35;
      m_moveIgnoreTicks = 0;
  }

  public  GameMenu (GameButton button1, GameButton button2, int defaultButton, float spacer, boolean verticalAlign, InputManager inputManager, GameMenuListener listener) {
      super();
      this.add(button1);
      this.add(button2);
      if (defaultButton == 1)
        button1.setSelected(true);
      else
        button2.setSelected(true);

      m_selectedButton = defaultButton;

      m_align = verticalAlign;
      m_spacer = spacer;

      m_inputManager = inputManager;
      m_numButtons = 2;

      m_listener = listener;
      m_ignoreTicks = 35;
      m_moveIgnoreTicks = 0;
  }

  public  GameMenu (GameButton button1, GameButton button2, GameButton button3, int defaultButton, float spacer, boolean verticalAlign, InputManager inputManager, GameMenuListener listener) {
      super();
      this.add(button1);
      this.add(button2);
      this.add(button3);

      if (defaultButton == 1)
        button1.setSelected(true);
      else if (defaultButton == 2)
        button2.setSelected(true);
      else
        button3.setSelected(true);

      m_selectedButton = defaultButton;

      m_align = verticalAlign;
      m_spacer = spacer;

      m_inputManager = inputManager;
      m_numButtons = 3;

      m_listener = listener;

      m_ignoreTicks = 35;
      m_moveIgnoreTicks = 0;
  }

  public  GameMenu (GameButton button1, GameButton button2, GameButton button3, GameButton button4, int defaultButton, float spacer, boolean verticalAlign, InputManager inputManager, GameMenuListener listener) {
      super();
      this.add(button1);
      this.add(button2);
      this.add(button3);
      this.add(button4);

      if (defaultButton == 1)
        button1.setSelected(true);
      else if (defaultButton == 2)
        button2.setSelected(true);
      else if (defaultButton == 3)
        button3.setSelected(true);
      else
        button4.setSelected(true);

      m_selectedButton = defaultButton;

      m_align = verticalAlign;
      m_spacer = spacer;

      m_inputManager = inputManager;
      m_numButtons = 4;

      m_listener = listener;

      m_ignoreTicks = 35;
      m_moveIgnoreTicks = 0;
  }

  public void addButton(GameButton b)
  {
    this.add(b);
    b.setSelected(false);
    m_numButtons++;
  }

  public void setAllDirections()
  {
    m_allDirections = true;
  }

  @Override
  public void update(float deltaTime)
  {
    if (m_visible) 
    {
      for (GameDrawable d : m_children)
      {
        if (d.isVisible())
         d.update(deltaTime);
      }
    }

    if (m_moveIgnoreTicks > 0)
    {
      if (m_inputManager.isLeftPressed() || m_inputManager.isRightPressed() || m_inputManager.isUpPressed() || m_inputManager.isDownPressed())
        m_moveIgnoreTicks--;
      else
        m_moveIgnoreTicks = 0;
    }
    else
    {
      if ((m_align == false) || (m_allDirections))
      {
        if (m_inputManager.isLeftPressed())
        {
          m_selectedButton--;
          if (m_selectedButton < 1)
            m_selectedButton = m_numButtons;
          m_moveIgnoreTicks = 30;
          this.playSound("toggle", 0.95f);
          while (this.isButtonDisabled(m_selectedButton))
          {
            m_selectedButton--;
            if (m_selectedButton < 1)
              m_selectedButton = m_numButtons;
          }
        } else if (m_inputManager.isRightPressed())
        {
          m_selectedButton++;
          if (m_selectedButton > m_numButtons)
            m_selectedButton = 1;
          m_moveIgnoreTicks = 30;
          this.playSound("toggle", 0.95f);
          while (this.isButtonDisabled(m_selectedButton))
          {
            m_selectedButton++;
            if (m_selectedButton > m_numButtons)
              m_selectedButton = m_numButtons;
          }
        }
      }

      if ((m_align) || (m_allDirections))
      {
        if (m_inputManager.isUpPressed())
        {
          m_selectedButton--;
          if (m_selectedButton < 1)
            m_selectedButton = m_numButtons;
          m_moveIgnoreTicks = 30;
          this.playSound("toggle", 0.95f);
          while (this.isButtonDisabled(m_selectedButton))
          {
            m_selectedButton--;
            if (m_selectedButton < 1)
              m_selectedButton = m_numButtons;
          }
        } else if (m_inputManager.isDownPressed())
        {
          m_selectedButton++;
          if (m_selectedButton > m_numButtons)
            m_selectedButton = 1;
          m_moveIgnoreTicks = 30;
          this.playSound("toggle", 0.95f);
          while (this.isButtonDisabled(m_selectedButton))
          {
            m_selectedButton++;
            if (m_selectedButton > m_numButtons)
              m_selectedButton = m_numButtons;
          }
        }
      }

      if (m_selectedButton != m_lastSelectedButton)
      {
        m_lastSelectedButton = m_selectedButton;
        int bNum = 1;
        for (GameDrawable d : m_children)
        {
          GameButton b = (GameButton) d;
          if (bNum == m_selectedButton)
            b.setSelected(true);
          else
            b.setSelected(false);
          bNum++;
        }
      }
    }

    if (m_ignoreTicks > 0)
    {

      if (m_inputManager.isJumpPressed() || m_inputManager.isFirePressed())
      {
        m_ignoreTicks--;
      } else
      {
        m_ignoreTicks = 0;
      }

    } else
    {
      if (m_inputManager.isJumpPressed() || m_inputManager.isFirePressed())
      {
        m_listener.buttonSelected(m_selectedButton, m_inputManager.isJumpPressed());
        m_ignoreTicks = 35;
        GameButton button = (GameButton)(m_children.get(m_selectedButton-1));
        this.playSound(button.clickSoundName,0.6f);
        //this.playSound("click", 0.65f);
      } else
      {
        int bNum = 1;
        for (GameDrawable d : m_children)
        {
          GameButton b = (GameButton) d;
          if (m_inputManager.buttonTapped(b))
            m_listener.buttonSelected(bNum, m_inputManager.isJumpPressed());

          bNum++;
        }
      }
    }
  }

  public boolean isButtonDisabled(int n)
  {
    GameButton buttonA = (GameButton)(m_children.get(n-1));
    return buttonA.isDisabled();
  }


  public void setPosition(float x, float y) { 
    m_x = x;
    m_y = y;
    float xx = m_x;
    float yy = m_y;

    for (GameDrawable dd : m_children)
    { 
      GameButton d = (GameButton)dd;
      d.setPosition(xx,yy);

      if (m_align == false)
      {
        xx += d.getWidth() + m_spacer;

      } else
      {
        yy -= (d.getHeight() + m_spacer);
      }
    }
  }

  public void nextButton()
  {
    m_selectedButton++;
    if (m_selectedButton > m_numButtons)
      m_selectedButton = 1;

    while (this.isButtonDisabled(m_selectedButton))
    {
      m_selectedButton++;
      if (m_selectedButton > m_numButtons)
        m_selectedButton = m_numButtons;
    }
  }

  public void setValidButton()
  {
    while (this.isButtonDisabled(m_selectedButton))
    {
      m_selectedButton++;
      if (m_selectedButton > m_numButtons)
        m_selectedButton = m_numButtons;
    }
  }

  public void pause()
  {
    m_pause = false;
  }

  public void resume()
  {
    m_pause = false;
  }


}