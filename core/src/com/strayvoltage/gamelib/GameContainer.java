package com.strayvoltage.gamelib;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import java.util.Iterator;
import java.util.ArrayList;

public interface GameContainer {
  public void add(GameDrawable obj);
  public void remove(GameDrawable obj);
  public void removeAll();
  public boolean isOnScreen(float xx, float yy);
  public boolean isOnScreen(float xx, float yy, float mx, float my);
  public void setGameState(int s);
  public ArrayList<GameDrawable> getChildren();
  public ShapeRenderer getShapeRenderer();
}