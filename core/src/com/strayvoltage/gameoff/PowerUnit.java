package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapProperties;
import com.strayvoltage.gamelib.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.*;
import com.badlogic.gdx.utils.Array;


public class PowerUnit extends GameMapObject implements Box2dCollisionHandler {

  Player m_owner = null;
  boolean m_pickedUp = true;
  GameAnimateable m_pickedUpAnimation = null;
  GameAnimateable m_launchAnimation = null;
  GameAnimateable m_walkingAnimation = null;
  GameAnimateable m_warnAnimation = null;
  GameAnimateable m_deathAnimation = null;

  int trampoline_count = 0;

  boolean m_onGround = false;
  GameInputManager2 m_controller = null;
  int m_readyToMoveTicks = 0;
  float stillTime = 0;
  float m_hForce = 4;
  Fixture playerSensorFixture = null;
  World m_world;
  int m_liveTicks = 0;
  boolean m_dead = false;
  int trampoline_state;
  float m_jumpDY = 0.3f;
  float m_jumpForce = 0.6f;
  int m_jumpTicks = 0;
  int m_bounceTicks = 0;
  float m_yOff = 12;
  float m_xOff = 3;
  float m_xOff2 = 0;
  float m_yOff2 = 0;
  GameTileMap m_map;
  int m_fanRight = 0;
  int m_fanLeft = 0;
  long m_brainMoveSound = -1;
  GameParticleSystem m_particleSystem1, m_particleSystem2;

  public void init(MapProperties mp, TextureAtlas textures)
  {
    m_colBits = 16;
    trampoline_state = Trampoline.NONE;
    TextureRegion texture = null;
    texture = textures.findRegion("brain_F1");
    this.setRegion(texture);
    this.setSize(texture.getRegionWidth(),texture.getRegionHeight());

    m_pickedUpAnimation = new AnimateSpriteFrame(textures, new String[] {"brain_F1"}, 0.1f, 1);
    m_launchAnimation = new AnimateSpriteFrame(textures, new String[] {"brain_F1", "brain_F2"}, 0.1f, 1);
    m_walkingAnimation = new AnimateSpriteFrame(textures, new String[] {"brain_F1", "brain_F2"}, 0.2f, -1);

    GameAnimateable f1 = new AnimateFadeOut(0.15f);
    GameAnimateable d1 = new AnimateDelay(0.5f);

    GameAnimateable[] ds = {f1,d1};
    m_deathAnimation = new GameAnimationSequence(ds,1);
    
    AnimateColorTo a = new AnimateColorTo(0.46f,1f,1f,1f,1f,0f,0f);
    AnimateDelay d = new AnimateDelay(0.08f);
    AnimateColorTo b = new AnimateColorTo(0.46f,1f,0f,0f,1f,1f,1f);
    GameAnimateable[] a1 = {a,d,b};
    m_warnAnimation = new GameAnimationSequence(a1,3);
    m_warnAnimation.setIgnoreStop(true);

  }

  public void setDeathParticles(MainLayer l, TextureAtlas textures)
  {
    m_particleSystem1 = new GameParticleSystem(l, textures, "brain_particle", 40, 0, 1.5f, 20,0.2f,0.03f);
    m_particleSystem1.setRandom(0.9f,5,12);
    m_particleSystem2 = new GameParticleSystem(l, textures, "brain_particle", 40, 2, 0.25f, 20,0.1f,0.03f);
    m_particleSystem2.setRandom(0.9f,5,10);
  }

  public void startMoveSound()
  {
    if (m_dead) return;
    if (m_brainMoveSound < 0)
    {
      m_brainMoveSound = loopSound("brainMove",0.7f);
    }
  }

  public void stopMoveSound()
  {
    if (m_brainMoveSound >= 0)
    {
      stopSound("brainMove", m_brainMoveSound);
      m_brainMoveSound = -1;
    }
  }

  public void trampJump()
  {
    if (m_bounceTicks > 0) return;

    if (m_jumpTicks < 1)
      m_body.applyLinearImpulse(0, m_jumpDY,0,0, true);
    else
      m_body.applyLinearImpulse(0, m_jumpDY/1.5f,0,0, true);

    m_jumpTicks = 15;
    m_onGround = false;
    this.playSound("brainJump",1f);
  }

  public void jump() {

    if (m_bounceTicks > 0) return;

	  if(trampoline_state != Trampoline.NONE) {

      Vector2 cv = this.m_body.getLinearVelocity();
      this.m_body.setLinearVelocity(cv.x,1);
      float f = 1.0f;

      if (trampoline_count == 2) f = 1.65f;

		  m_body.applyLinearImpulse(0, m_jumpDY*1.5f*f, 0, 0, true);
		  m_jumpTicks = 15;
      m_bounceTicks = 15;
      this.playSound("bounce",0.85f);
	  }else {
      Vector2 cv = this.m_body.getLinearVelocity();
      if (Math.abs(cv.y) > 3)
       this.m_body.setLinearVelocity(cv.x,0);

		  m_body.applyLinearImpulse(0, m_jumpDY, 0, 0, true);
	    m_jumpTicks = 15;
	  }

    this.playSound("brainJump",1f);
    m_onGround = false;
  }

  public void addToWorld(World world)
  {
    m_world = world;
    BodyDef bodyDef = new BodyDef();
    bodyDef.type = BodyType.DynamicBody;
    bodyDef.fixedRotation = true;
    bodyDef.bullet = true;

    m_body = world.createBody(bodyDef);
    m_body.setUserData(this);

    FixtureDef fixtureDef = new FixtureDef();

    PolygonShape rect = null;
    rect = new PolygonShape();
    rect.setAsBox(this.getWidth()/(2*Box2dVars.PIXELS_PER_METER), this.getHeight()/(2*Box2dVars.PIXELS_PER_METER));
    fixtureDef.shape = rect;

    fixtureDef.density = 0.04f; 
    fixtureDef.friction = 0.5f;
    fixtureDef.restitution = 0.0f;

    fixtureDef.filter.categoryBits = Box2dVars.POWER;
    fixtureDef.filter.maskBits = Box2dVars.OBJECT | Box2dVars.FAN | Box2dVars.FLOOR | Box2dVars.BLOCK | Box2dVars.PLATFORM | Box2dVars.HAZARD;

    m_fixture = m_body.createFixture(fixtureDef);
    m_fixture.setUserData(this);
    rect.dispose();

    fixtureDef = new FixtureDef();
    PolygonShape rect2 = null;
    rect2 = new PolygonShape();
    rect2.setAsBox((this.getWidth()-6)/(2*Box2dVars.PIXELS_PER_METER), 0.1f,new Vector2(0,-this.getHeight()/(2*Box2dVars.PIXELS_PER_METER)),0);
    fixtureDef.shape = rect2;

    fixtureDef.density = 0f; 
    fixtureDef.friction = 0f;
    fixtureDef.restitution = 0f;
    fixtureDef.filter.categoryBits = Box2dVars.BRAIN_FOOT; //changed to foot so we can do separate collision testing. 
    fixtureDef.filter.maskBits = Box2dVars.FLOOR | Box2dVars.BLOCK | Box2dVars.PLATFORM | Box2dVars.OBJECT;

	  playerSensorFixture = m_body.createFixture(fixtureDef);
	  playerSensorFixture.setUserData(this);
    playerSensorFixture.setSensor(true);		
		rect2.dispose();		
  }

  public boolean checkTileCollision(float xx, float yy)
  {
    if (m_map.getCollisionBitsAt(xx,yy) > 0) return true;
    return false;
  }

  public boolean checkDir(int dx)
  {
    float xx = this.getX() + dx*(this.getWidth() + 2) - 1;
    float yy = this.getY() + 1;

    if (checkTileCollision(xx,yy)) return true;

    yy += (this.getHeight() - 5);

    return checkTileCollision(xx,yy);

  }

  public void pickUp(Player p, boolean sound)
  {
      if (sound) this.playSound("brainOn",1f);

      m_owner = p;
      m_pickedUp = true;
      m_body.setActive(false);
      m_owner.setPowerUnit(this);
      m_controller = m_owner.m_controller;
      this.stopAllAnimations();
      this.runAnimation(m_pickedUpAnimation);
      if (m_warnAnimation.isRunning()) m_warnAnimation.stop();
      this.setColor(1f,1f,1f,1f);
      trampoline_state = Trampoline.NONE;
      if (p.id == 1)
      {
        m_yOff = 35;
        m_xOff = 3;
        m_xOff2 = 0;
        m_yOff2 = 0;
      } else
      {
        m_yOff = 15;
        m_xOff = 0;
        m_yOff2 = 0;
      }

  }

  public boolean isAlive()
  {
    if (m_dead == false) return true;
    if (m_dead)
    {
      if (m_deathAnimation.isRunning()) return true;
    }
    return false;
  }

  public void throwUnit(float dx, float dy)
  {
    m_pickedUp = false;
    Vector2 ov = m_owner.m_body.getLinearVelocity();
    m_body.setLinearVelocity(ov.x,ov.y+dy);
    m_body.setActive(true);
    m_owner = null;
    setPositionToBody();
    this.runAnimation(m_launchAnimation);
    m_readyToMoveTicks = 10;
    //if (dx > 0)
    //  this.setFlip(true,false);
    //else
    //  this.setFlip(false,false);

    m_liveTicks = 360;
    stillTime = 0;
    m_onGround = false;
    m_fixture.setFriction(0f);
    playerSensorFixture.setFriction(0f);
    trampoline_state = Trampoline.NONE;
    this.playSound("brainOff",1f);

  }

  public boolean canPickUp()
  {
      return !m_pickedUp;
  }

  private boolean isPlayerGrounded(float deltaTime) {

		//groundedPlatform = null;
		Array<Contact> contactList = m_world.getContactList();
		for(int i = 0; i < contactList.size; i++) {
			Contact contact = contactList.get(i);
			if(contact.isTouching() && (contact.getFixtureA() == playerSensorFixture ||
			   contact.getFixtureB() == playerSensorFixture)) {				
				Vector2 pos = m_body.getPosition();
				WorldManifold manifold = contact.getWorldManifold();
				boolean below = true;
				for(int j = 0; j < manifold.getNumberOfContactPoints(); j++) {
					below = below && (manifold.getPoints()[j].y < (pos.y - this.getHeight()/(2*Box2dVars.PIXELS_PER_METER) - 0.1f));
				}
				
				if(below) {
          /*
					if(contact.getFixtureA().getUserData() != null && contact.getFixtureA().getUserData().equals("p")) {
						groundedPlatform = (MovingPlatform)contact.getFixtureA().getBody().getUserData();							
					}
					
					if(contact.getFixtureB().getUserData() != null && contact.getFixtureB().getUserData().equals("p")) {
						groundedPlatform = (MovingPlatform)contact.getFixtureB().getBody().getUserData();
					}	
          */						
			
					return true;			
				}

				return false;
			}
		}
    return false;
  }

  public void die()
  {
    if (m_dead) return;
    this.stopAllAnimations();
    this.runAnimation(m_deathAnimation);
    m_body.setLinearVelocity(0,0);
    m_dead = true;
    GameMain.getSingleton().addDeath();
    stopMoveSound();
    playSound("brainDie",1f);
    m_particleSystem1.start();
    m_particleSystem2.start();
  }

  public void update(float deltaTime)
  {
      if (m_dead)
      {
        
        m_particleSystem1.setLocation(this.getX(), this.getY(), this.getWidth(), this.getHeight());
        m_particleSystem2.setLocation(this.getX(), this.getY(), this.getWidth(), this.getHeight());
 
        m_particleSystem1.update(deltaTime);
        m_particleSystem2.update(deltaTime);


        setPositionToBody();
        return;
      }

      if (m_bounceTicks > 0) m_bounceTicks--;

      if (m_pickedUp == false)
      {
        m_liveTicks--;
        if (m_liveTicks == 180)
        {
          this.runAnimation(m_warnAnimation);
        } else if (m_liveTicks < 1)
        {
          this.die();
          return;
        }

        m_onGround = isPlayerGrounded(Gdx.graphics.getDeltaTime());

        boolean notMoved = true;
        setPositionToBody();
        Vector2 cv = m_body.getLinearVelocity();
        if (m_readyToMoveTicks > 0) m_readyToMoveTicks--;
        if (m_readyToMoveTicks < 1)
        {
          if (m_controller.isRightPressed())
          {
            if (checkDir(1) == false)
            {
              m_body.applyForceToCenter(m_hForce,0,true);
              startMoveSound();
            }
    
            notMoved = false;
            stillTime = 0;
            //this.setFlip(true,false);
          } else if (m_controller.isLeftPressed())
          {
            if (checkDir(0) == false)
            {
              m_body.applyForceToCenter(-m_hForce,0,true);
              startMoveSound();
            }
              
            notMoved = false;
            stillTime = 0;
            //this.setFlip(false,false);
          } else {		
              stillTime += Gdx.graphics.getDeltaTime();
              if ((m_fanRight + m_fanLeft) < 1)
                m_body.setLinearVelocity(cv.x * 0.8f, cv.y);
          }

          float mrx = 5f;
          float mlx = 5f;

          if (m_fanRight > 0) mrx = mrx * 2.5f;
          if (m_fanLeft > 0) mlx = mlx* 2.5f;
      
          if (cv.x > 0)
          {
            if (cv.x > mrx)
            {
              cv.x = mrx;
              m_body.setLinearVelocity(cv);
            }
          } else if (cv.x < 0)
          {
            if (Math.abs(cv.x) > mlx)
            {
              cv.x = - mlx;
              m_body.setLinearVelocity(cv);
            }
          }

          if (m_jumpTicks > 0) m_jumpTicks--;

          if ((m_onGround && m_controller.isJumpPressed()) && (m_jumpTicks < 1))
          {
            this.jump();
            stopMoveSound();
          } else if (m_jumpTicks > 0)
          {
            if (m_controller.isJumpPressed())
            {
              m_body.applyForceToCenter(0,m_jumpForce,true);
            } else
            {
              m_jumpTicks = 0;
            }
          }
        }

        if(!m_onGround) 
        {
          stopMoveSound();
          m_fixture.setFriction(0f);
          playerSensorFixture.setFriction(0f);			
        } else 
        {
          if(notMoved && stillTime > 0.2) {
            if (Math.abs(cv.x) < 0.2f)
            {
              stopMoveSound();
              m_fixture.setFriction(100f);
              playerSensorFixture.setFriction(100f);
            }
          }
          else {
            m_fixture.setFriction(0.2f);
            playerSensorFixture.setFriction(0.2f);
          }
            //if(groundedPlatform != null && groundedPlatform.dist == 0) {
            //  player.applyLinearImpulse(0, -24, pos.x, pos.y);				
            //}
        }	

        //is on ground and moving make sure walking animation is running
        if (m_onGround)
        {
          if (m_readyToMoveTicks > 15) m_readyToMoveTicks = 15;

          if ((cv.x > 0.25f) || (cv.x < -0.25f))
          {
            if (m_walkingAnimation.isRunning() == false)
            {
              this.runAnimation(m_walkingAnimation);
            }
          } else
          {
            if (m_walkingAnimation.isRunning())
            {
              this.stopAllAnimations();
              this.runAnimation(m_pickedUpAnimation);
              stopMoveSound();
            }
          }
        } else
        {
          if (m_walkingAnimation.isRunning() == false)
            this.runAnimation(m_walkingAnimation);

          if (cv.y < 0)
          {
            if (m_launchAnimation.isRunning() == false)
              this.runAnimation(m_launchAnimation);
              stopMoveSound();
          } else
          {
            if (m_pickedUpAnimation.isRunning() == false)
              this.runAnimation(m_pickedUpAnimation);
              stopMoveSound();
          }
        }
      } else
      {
        if (m_owner != null)
        {
          this.setBodyPosition(m_owner.getX() + m_owner.getWidth()/2 - this.getWidth()/2 + m_xOff + m_xOff2, m_owner.getY() + m_owner.getHeight() - m_yOff + m_yOff2);
        }
      }

  }

  public void playLanding()
  {
        playSound("brainLand",0.85f);
  }

  	@Override
	public void handleBegin(Box2dCollision collision) {
		
		if(collision.target_type == Box2dVars.HAZARD) {
		  this.die();	
		}

    if(collision.self_type == Box2dVars.BRAIN_FOOT) {

      if ((collision.target_type == Box2dVars.PLATFORM)
        || (collision.target_type == Box2dVars.FLOOR))
        {
          playLanding();
        }
    }

    if (collision.target_type == Box2dVars.FAN)
    {
      Fan f = (Fan) collision.target;
      if (f.direction == f.RIGHT)
      {
        m_fanRight++;
      } else if (f.direction == f.LEFT)
      {
        m_fanLeft++;
      }
    }
	}

  @Override
	public void handleEnd(Box2dCollision collision) {

    if (collision.target_type == Box2dVars.FAN)
    {
        Fan f = (Fan) collision.target;
        if (f.direction == f.RIGHT)
        {
          m_fanRight--;
        } else if (f.direction == f.LEFT)
        {
          m_fanLeft--;
        }
     }
	}

}