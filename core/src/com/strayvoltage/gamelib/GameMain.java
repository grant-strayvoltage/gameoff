package com.strayvoltage.gamelib;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Ouya;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.*;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Stack;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.*;
import com.badlogic.gdx.files.*;
import com.badlogic.gdx.assets.AssetManager;

public abstract class GameMain extends ApplicationAdapter  {

	public static OrthographicCamera m_camera;
    public static Viewport m_viewport;
	public static SpriteBatch m_spriteBatch;
	private HashMap m_gameSounds = new HashMap(40);
	protected Stack<GameLayer> m_gameLayers = new Stack<GameLayer>();
	private GameLayer m_activeGameLayer = null;
	private static GameMain m_instance = null;
    public static HashMap m_globals = new HashMap(200);
    public static String m_currentMusic = "NONE";
    public static String m_currentMusic2 = "NONE";
    public static String m_currentMusic3 = "NONE";
    public static Vector2 m_distCalc = new Vector2();
    public static SoundManager m_soundManager = new SoundManager();
    public AssetManager m_assets = new AssetManager();

	public abstract void setupGame();
    public abstract void finishSetup();
    public abstract void loadGameDefaults();

	@Override
	public void create () 
	{
		m_spriteBatch = new SpriteBatch();
        m_instance = this;
		this.setupGame();
	}

	@Override
	public void render () 
	{
		m_activeGameLayer.render();
    }

    public void pushGameLayer(GameLayer gl)
    {
    	m_gameLayers.push(gl);
    	m_activeGameLayer = gl;
    }

    public GameLayer popGameLayer()
    {
      GameLayer g = m_gameLayers.pop();
      if (m_gameLayers.size() > 0)
    	 m_activeGameLayer = m_gameLayers.peek();
      return g;
    }

    public void replaceActiveLayer(GameLayer gl)
    {
      m_gameLayers.pop();
      this.pushGameLayer(gl);
    }

    public void setGlobal(String key, String value)
    {
        m_globals.put(key, value);
    }

    public String getGlobal(String key)
    {
        if (m_globals.containsKey(key))
        {
            return (String) m_globals.get(key);
        }

        return null;
    }

    public String getAllGlobalsAsString()
    {
        String r = "";
        for (Object key : m_globals.keySet())
        {
            Object v = m_globals.get(key);
            String ks = (String) key;
            String vs = (String) v;
            r = r + (ks + ":" + vs + "~");
        }

        return r;
    }

    public void loadAllGlobalsFromString(String s)
    {
        String[] entries = s.split("~");
        for (String entry : entries)
        {
            if ((entry != null) && (entry.contains(":")))
            {
                String[] kv = entry.split(":");
                m_globals.put(kv[0], kv[1]);
            }
        }
    }

    public void stopSound(String soundName, long soundId)
    {
        //Gdx.app.debug("GameMain:stopSound", "Trying to stop:" + soundName + " ID: " + soundId);
        Sound s = (Sound) m_gameSounds.get(soundName);
        if (s != null)
        {
            s.stop(soundId);
            m_soundManager.removeSound(soundId);
            //Gdx.app.debug("GameMain:stopSound", "Stopped sound:" + soundName);
        } 
    }

    public void addSound(String key, String filename)
    {
        //Gdx.app.log("GameMain","addSound: " + key);
        if (m_gameSounds.containsKey(key))
        {
            this.stopSound(key);
            if (key.startsWith("music"))
            {
                Music o = (Music) m_gameSounds.remove(key);
                o.dispose(); 
            } else
            {
                Sound o = (Sound) m_gameSounds.remove(key);
                o.dispose();      
            }

        }

        if (key.startsWith("music"))
        {
            Music music = Gdx.audio.newMusic(Gdx.files.internal(filename));
            m_gameSounds.put(key,music);
            //Gdx.app.log("GameMain","put Sound: " + key + "music = " + music);
        } else
        {
    	   Sound s = Gdx.audio.newSound(Gdx.files.internal(filename));
    	   m_gameSounds.put(key,s);
        }
    }

    public void removeSound(String key)
    {
        if (m_gameSounds.containsKey(key))
        {
            this.stopSound(key);
            if (key.startsWith("music"))
            {
                Music o = (Music) m_gameSounds.remove(key);
                o.dispose(); 
            } else
            {
                Sound o = (Sound) m_gameSounds.remove(key);
                o.dispose();      
            }
        }
    }

    public void loadGame(int num)
    {
      if(Gdx.files.local("saveGame_" + num + ".txt").exists())
      {
        FileHandle file = Gdx.files.local("saveGame_" + num + ".txt");
        String data = file.readString();
        this.loadAllGlobalsFromString(data);
      }

      this.setGlobal("GameSaveID", "" + num);
    }

    public int getGameLevel(int num)
    {
      if(Gdx.files.local("saveGame_" + num + ".txt").exists())
      {
            FileHandle file = Gdx.files.local("saveGame_" + num + ".txt");
            String data = file.readString();

            String[] entries = data.split("~");
            for (String entry : entries)
            {
                if ((entry != null) && (entry.contains(":")))
                {
                    String[] kv = entry.split(":");
                    if (kv[0].equals("Level"))
                    {
                        int lv = Integer.parseInt(kv[1]);
                        return lv;
                    }
                }
            }
      }
      return 0;
    }

    public void eraseGame(int num)
    {
        if(Gdx.files.local("saveGame_" + num + ".txt").exists())
        {
            Gdx.files.local("saveGame_" + num + ".txt").delete();
        }
    }

    public boolean doesGameExist(int num)
    {
        if(Gdx.files.local("saveGame_" + num + ".txt").exists())
            return true;

        return false;
    }


    public void saveGame()
    {
      String gsid = this.getGlobal("GameSaveID");
      if (gsid == null)
        gsid = "0";

      FileHandle file = Gdx.files.local("saveGame_" + gsid + ".txt");
      String fs = getAllGlobalsAsString();
      file.writeString(fs,false);
    }

    public long playSound(String soundName, GameSprite player, GameDrawable target, float max, float min)
    {
        //Gdx.app.debug("playSound", "sound " + soundName);
        m_distCalc.set(player.getX(), player.getY());
        Sound s = (Sound) m_gameSounds.get(soundName);
        float volume = m_soundManager.calculateVolume(m_distCalc, target, max, min);
        float pan = m_soundManager.calculatePan(m_distCalc, target);
        return s.play(volume, 1.0f, pan);
    }

    public long playSound(String soundName, float volume)
    {
       if (soundName.startsWith("music"))
       {
        //Gdx.app.debug("playSound****", "music: " + soundName);
        Music m = (Music) m_gameSounds.get(soundName);
        m.setVolume(volume);
        if (m.isPlaying() == false)
        {
            m.setLooping(false);
            m.play();
            return -1;
            //Gdx.app.debug("playSound****", "music: " + soundName + " started playing.");
        } else
        {
             m.setVolume(volume);
        }
       } else 
       {
            //Gdx.app.debug("playSound", "sound " + soundName);
        	Sound s = (Sound) m_gameSounds.get(soundName);
        	return s.play(volume);
       }
       return -1;
    }

    public void stopSound(String soundName)
    {
       // if (soundName.equals("music"))
       //     return;
       if (soundName.startsWith("music"))
       {
        Music m = (Music) m_gameSounds.get(soundName);
        if (m != null)
        {
            if (m.isPlaying())
                m.stop();
        }
       } else {
        Sound s = (Sound) m_gameSounds.get(soundName);
        s.stop();
      }
    }

    public void stopAllSounds()
    {
        Iterator it = m_gameSounds.entrySet().iterator();
        while (it.hasNext()) 
        {
            Map.Entry pair = (Map.Entry)it.next();
            String soundName = (String)pair.getKey();
            if (!soundName.startsWith("music"))
                this.stopSound(soundName);
        }

        m_soundManager.clear();
    }


    public boolean isMusicPlaying()
    {
        Music m = (Music) m_gameSounds.get("music");
        return m.isPlaying();
    }

    public void setMusic(String musicName)
    {
        if (musicName == m_currentMusic)
            return;

        this.addSound("music", musicName);
        m_currentMusic = musicName;
    }

    public void setMusic2(String musicName, float v)
    {
        if (musicName == m_currentMusic2)
            return;

        this.addSound("music2", musicName);
        m_currentMusic2 = musicName;
    }

    public void setMusic3(String musicName, float v)
    {
        if (musicName == m_currentMusic3)
            return;

        this.addSound("music3", musicName);
        m_currentMusic3 = musicName;
    }

    //(Vector2 distCalc, GameSprite target, float max, float min)
    public long loopSoundManageVolume(String soundName, GameSprite sprite, GameSprite player, float max, float min)
    {
        m_distCalc.set(player.getX(), player.getY());
        float v = m_soundManager.calculateVolume(m_distCalc, sprite, max, min);
        Sound s = (Sound) m_gameSounds.get(soundName);
        float pan = m_soundManager.calculatePan(m_distCalc, sprite);
        long soundId = s.loop(v,1,pan);
        m_soundManager.addSound(soundId, s, sprite, max, min);
        return soundId;
    }

    public void setMusic2Volume(float volume)
    {
        Music m = (Music) m_gameSounds.get("music2");
        m.setVolume(volume);
    }

    public void setMusic3Volume(float volume)
    {
        Music m = (Music) m_gameSounds.get("music3");
        m.setVolume(volume);
    }

    public long loopSound(String soundName, float volume)
    {
        //if (soundName.equals("music"))
        //    return 12123232;

        if (soundName.startsWith("music"))
        {
            //Gdx.app.log("loopSound****", "music: " + soundName);
            Music m = (Music) m_gameSounds.get(soundName);
            //Gdx.app.log("loopSound****", "music: " + soundName + " m = " + m);
            m.setVolume(volume);
            if (m.isPlaying() == false)
            {
                m.setLooping(true);
                //Gdx.app.log("loopSound****", "music: " + soundName + " started playing.");
                
                m.play();
                return 0;

            } else
            {
                //Gdx.app.debug("loopSound****", "music: " + soundName + " already playing.");
                return 0;
            }
        }

        Sound s = (Sound) m_gameSounds.get(soundName);
        Long sId = s.loop(volume);
        //Gdx.app.debug("GameMain:loopSound", "Looping:" + soundName + " ID: " + sId);
        return sId;
    }


    public void addDeath()
    {
        int deaths = Integer.parseInt(getGlobal("deaths"));
        deaths++;
        this.setGlobal("deaths","" + deaths);
    }

    public String getDeaths()
    {
        return getGlobal("deaths");
    }

    public String getTime()
    {
        String tm = getGlobal("time");
        float tmf = Float.parseFloat(tm);
        float mins = tmf / 60f;
        String m = String.format("%.2f mins", mins);

        if (tmf < 90)
        {
            m = String.format("%.0f seconds", tmf);
        }

        return m;
    }

    public static GameMain getSingleton()
    {
    	return m_instance;
    }
}