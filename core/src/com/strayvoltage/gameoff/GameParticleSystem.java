package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.strayvoltage.gamelib.GameLayer;

public class GameParticleSystem {

    float m_x = 0;
    float m_y = 0;
    float m_width = 4;
    float m_height = 4;
    int m_dir = 0;
    float m_frequency = 0.25f;
    float m_spd = 0;
    int m_life = 60;
    boolean m_shouldSpawn = false;
    float m_grav = 0;

    Array<GameParticle> m_particles;

    public GameParticleSystem(GameLayer layer, TextureAtlas textures, String imgName, int numParticles, int dir, int spd, int life, float freq, float grav)
    {
    	m_particles = new Array<GameParticle>(numParticles);
        for (int i = 0; i < numParticles; i++)
        {
            GameParticle p =new GameParticle(textures,imgName);
            m_particles.add(p);
            layer.add(p);
        }

        m_dir = dir;
        m_spd = spd;
        m_life = life;
        m_frequency = freq;
        m_grav = grav;
    }

    public void setLocation(float xx, float yy, float w, float h)
    {
        m_x = xx;
        m_y = yy;
        m_width = w;
        m_height = h;
    }

    public void setDirection(int dir)
    {
        m_dir = dir;
    }

    //% chance per tick
    public void setFrequency(float f)
    {
        m_frequency = f;
    }

    public void update(float deltaTime)
    {
        m_shouldSpawn = false;
        if (Math.random() < m_frequency) 
        {
            m_shouldSpawn = true;
        }

        for (GameParticle p : m_particles)
        {
            if (p.m_state > 0)
            {
                p.update(deltaTime);
            } else if (m_shouldSpawn)
            {
                p.setParticle(m_x, m_y, m_width, m_height, m_dir, m_spd, m_life,m_grav);
                m_shouldSpawn = false;
            }
        }
    }
}