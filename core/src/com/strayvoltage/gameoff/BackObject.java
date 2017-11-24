package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapProperties;
import com.strayvoltage.gamelib.*;

public class BackObject extends GameMapObject {

  public void init(MapProperties mp, TextureAtlas textures)
  {
      m_hasPhysics = false;

    int spd = getInt("speed", mp);
    int frames = getInt("frames", mp);
    String sn = getString("sprite",mp);
    int st = getInt("style",mp);


    TextureRegion texture = null;
    texture = textures.findRegion(sn + "_F1");
    this.setRegion(texture);
    this.setSize(texture.getRegionWidth(),texture.getRegionHeight());

    float tm = (1/30f) * spd * frames;

    GameAnimateable animation = null;
    if (frames == 2)
    {
        animation = new AnimateSpriteFrame(textures, new String[] {sn + "_F1", sn + "_F2"},tm,-1);
            this.runAnimation(animation);
    } else if (frames == 3)
    {
        if (st == 1)
        {
            animation = new AnimateSpriteFrame(textures, new String[] {sn + "_F1", sn + "_F2", sn + "_F3", sn + "_F2"},tm*4/3,-1);
        }
        else
            animation = new AnimateSpriteFrame(textures, new String[] {sn + "_F1", sn + "_F2", sn + "_F3"},tm,-1);
        
        this.runAnimation(animation);
    } else if (frames == 4)
    {
        animation = new AnimateSpriteFrame(textures, new String[] {sn + "_F1", sn + "_F2", sn + "_F3", sn + "F4"},tm,-1);
        this.runAnimation(animation);
    }



  }
}