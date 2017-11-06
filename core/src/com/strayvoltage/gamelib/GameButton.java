package com.strayvoltage.gamelib;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public class GameButton extends GameSprite{

  GameText m_label = null;
  boolean m_selected = false;
  TextureRegion m_selectedRegion, m_notSelectedRegion;
  GameAnimateable m_selectedAnimation;
  boolean m_disabled = false;
  public String clickSoundName = "click";

  public GameButton(TextureAtlas textures, String text, BitmapFont font)
  {
    super(textures.findRegion("button_notSelected"));
    m_selectedRegion = textures.findRegion("button_selected");
    m_notSelectedRegion = textures.findRegion("button_notSelected");

    m_label = new GameText(font, this.getWidth());
    m_label.setText(text);

    AnimateScaleTo g = new AnimateScaleTo(0.45f, 1.0f, 1.0f, 1.08f, 1.08f);
    AnimateDelay d = new AnimateDelay(0.1f);
    AnimateScaleTo s = new AnimateScaleTo(0.40f, 1.08f, 1.08f, 1.0f, 1.0f);
    GameAnimateable[] a = {g,d,s};
    m_selectedAnimation = new GameAnimationSequence(a,-1);
  }

  public void setClickSoundName(String sn)
  {
    clickSoundName = sn;
  }

  public GameButton(TextureRegion region)
  {
    super(region);
  }

  public boolean isSelected()
  {
    return m_selected;
  }

  public void setSelected(boolean v)
  {
    m_selected = v;
    if (m_selected)
    {
      this.stopAllAnimations();
      this.setScale(1,1);
      this.setRegion(m_selectedRegion);
      this.runAnimation(m_selectedAnimation);
    } else
    {
      this.setRegion(m_notSelectedRegion);
      this.stopAllAnimations();
      this.setScale(1,1);
    }
  }

  public void setPosition(float xx, float yy)
  {
    super.setPosition(xx,yy);
    if (m_label != null)
      m_label.setPosition(xx, yy+58);
  }

  public void setDisabled(boolean b)
  {
    m_disabled = b;
    if (b == true)
    {
      this.setOpacity(0.55f);
    } else
    {
      this.setOpacity(1);
    }
  }

  public boolean isDisabled()
  {
    return m_disabled;
  }

  public void setText(String t)
  {
    if (m_label != null)
      m_label.setText(t);
  }

  @Override
  public void draw(SpriteBatch s)
  {
    //Gdx.app.log("INFO", "Draw GameSprite called. m_opacity = " + m_opacity);
    super.draw(s, m_opacity);
    if (m_label != null)
      m_label.draw(s);
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

