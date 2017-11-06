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

public class GameImageButton extends GameButton {

  public GameImageButton(TextureAtlas textures, String imageName)
  {
    super(textures.findRegion(imageName + "_notSelected"));
    m_selectedRegion = textures.findRegion(imageName + "_selected");
    m_notSelectedRegion = textures.findRegion(imageName + "_notSelected");

    AnimateScaleTo g = new AnimateScaleTo(0.45f, 1.0f, 1.0f, 1.08f, 1.08f);
    AnimateDelay d = new AnimateDelay(0.1f);
    AnimateScaleTo s = new AnimateScaleTo(0.40f, 1.08f, 1.08f, 1.0f, 1.0f);
    GameAnimateable[] a = {g,d,s};
    m_selectedAnimation = new GameAnimationSequence(a,-1);
  }

  public boolean isSelected()
  {
    return m_selected;
  }

}

