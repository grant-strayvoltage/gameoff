package com.strayvoltage.gameoff;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import java.util.Iterator;
import java.util.ArrayList;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.Gdx;
import com.strayvoltage.gamelib.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.*;
import com.badlogic.gdx.utils.Array;

public class Player extends GameSprite implements Box2dCollisionHandler{

  GameInputManager2 m_controller;
  float m_dx = 0;
  float m_dy = 0;
  GameTileMap m_map = null;
  boolean m_powered = false;
  boolean m_playerControlled = false;
  Player m_otherPlayer = null;
  int m_controlDelayTicks = 5;
  float m_jumpDY = 11;
  float m_gravity = -0.2f;
  int m_jumpTicks = 0;
  boolean m_onGround = true;
  PowerUnit m_powerUnit = null;
  float m_lastDx = 1;
  boolean m_ownsPowerUnit = false;
  int m_firePressedTicks = 0;
  int m_throwDelayTicks = 60;
  Fixture m_fixture = null;
  Fixture playerSensorFixture = null;
  int m_categoryBits = 0;
  int m_tempTicks = 0;
  long lastGroundTime = 0;
  float stillTime = 0;
  World m_world = null;
  float m_hForce = 100;
  Body platform = null;
  Rectangle m_brainRectangle = new Rectangle(0,0,7,4);
  float m_jumpForce = 40;
  int id = 1;
  float m_maxX = 4;
  int trampoline_state;
  int m_vertTicks = 0;
  float m_yOff = 0;
  GameAnimateable m_standingAnimation;
  GameAnimateable m_runningAnimation;
  GameAnimateable m_jumpingAnimation;
  GameAnimateable m_fallingAnimation;
  GameAnimateable m_landingAnimation;
  GameAnimateable m_exitAnimation;
  float m_bhoff = 0;
  int m_ticks = 0;
  int m_state = 2; //paused
  float m_targetX = 0;
  int m_groundCheckTicks = 5;

  public Player(TextureAtlas textures, int p, GameInputManager2 controller)
  {
    super(textures.findRegion("player" + p + "_stand_F1"));
    m_controller = controller;
    trampoline_state = Trampoline.NONE;

    String pb = "player" + p + "_";

    m_standingAnimation = new AnimateSpriteFrame(textures, new String[] {pb+"stand_F1", pb + "stand_F2"}, 0.6f, -1);
    if (p == 1)
      m_runningAnimation = new AnimateSpriteFrame(textures, new String[] {pb+"run_F1", pb + "run_F2", pb + "run_F3", pb + "run_F4"}, 0.8f, -1);
    else
      m_runningAnimation = new AnimateSpriteFrame(textures, new String[] {pb+"run_F1", pb + "run_F2", pb + "run_F3"}, 0.4f, -1);

    m_jumpingAnimation = new AnimateSpriteFrame(textures, new String[] {pb+"jump"}, 0.5f, -1);
    m_landingAnimation =  new AnimateSpriteFrame(textures, new String[] {pb+"land"}, 0.3f, 1);
    this.runAnimation(m_standingAnimation);
    id = p;

    m_exitAnimation = new AnimateFadeOut(0.5f);
  }

  public void setMap(GameTileMap m, Player p, float jumpDY, float jForce, PowerUnit pu, int pNum)
  {
    m_map = m;
    m_otherPlayer = p;
    m_jumpDY = jumpDY;
    m_powerUnit = pu;
    m_jumpForce = jForce;
    id = pNum;
    if (pNum == 1)
    {
      m_hForce = 150;
      m_maxX = 3;
    } else
    {
      m_hForce = 100;
      m_maxX = 4;
    }
  }

  public void calcBrainRectangle()
  {
    Rectangle b = this.getBoundingRectangle();
    m_brainRectangle.x = (b.x + b.width/2) - 3;
    m_brainRectangle.y = b.y + b.height - m_bhoff;
  }

  // call passing in center of the door roughly
  public void doExit(float xx)
  {
    //start animation to move to 'center of door'
    //then fade out

    m_targetX = xx - (float)Math.random()*12f + 6f;
    m_state = 10;
    this.removeControl();
    m_powered = true;
    m_ticks = 0;
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
    float xf = 0.85f;
    float yf = 0.67f;
    rect = new PolygonShape();
   
    if (id == 2)
    {
      yf = 1f;
      xf = 0.95f;
    }

    m_yOff = (1 - yf) * this.getHeight()/2;
    m_bhoff = (1 - yf) * this.getHeight()/2 + 2;

    rect.setAsBox(this.getWidth()/(2*Box2dVars.PIXELS_PER_METER) * xf, this.getHeight()/(2*Box2dVars.PIXELS_PER_METER) * yf);
    fixtureDef.shape = rect;


    fixtureDef.density = 0.6f; 
    fixtureDef.friction = 0.5f;
    fixtureDef.restitution = -1f;

    fixtureDef.filter.categoryBits = Box2dVars.PLAYER_NORMAL;
    fixtureDef.filter.maskBits = Box2dVars.SWITCH | Box2dVars.OBJECT | Box2dVars.FLOOR | Box2dVars.BLOCK | Box2dVars.PLATFORM | Box2dVars.HAZARD;

    m_fixture = m_body.createFixture(fixtureDef);
    m_fixture.setUserData(this);
    rect.dispose();

    fixtureDef = new FixtureDef();
    PolygonShape rect2 = null;
    rect2 = new PolygonShape();
    rect2.setAsBox((this.getWidth()*0.75f)/(2*Box2dVars.PIXELS_PER_METER) * xf, 0.1f,new Vector2(0,-this.getHeight()/(2*Box2dVars.PIXELS_PER_METER) * yf),0);
    fixtureDef.shape = rect2;

    fixtureDef.density = 0f; 
    fixtureDef.friction = 0f;
    fixtureDef.restitution = 0f;
    fixtureDef.filter.categoryBits = Box2dVars.PLAYER_FOOT; //changed to foot so we can do separate collision testing. 
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

    yy += (this.getHeight() - m_yOff*2)/2;

    if (checkTileCollision(xx,yy)) return true;

    yy += (this.getHeight() - m_yOff*2)/2 - 3;

    return checkTileCollision(xx,yy);

  }

  public GameMapObject checkObjectCollisions(float xx, float yy)
  {
    float oldx = this.getX();
    float oldy = this.getY();
    this.setPosition(xx,yy);
    MainLayer m = (MainLayer) getParent();
    for (GameMapObject o : m.m_gameMapObjects)
    {
      if ((o.colBits() & 3) > 0)
      {
        if (Intersector.overlaps(this.getBoundingRectangle(), o.getBoundingRectangle()))
        {
          this.setPosition(oldx,oldy);
          return o;
        }
      }
    }
    this.setPosition(oldx,oldy);
    return null;
  }

  public float dist(float xx, float yy)
  {
    float px = this.getX() + this.getWidth()/2;
    float py = this.getY() + this.getHeight()/2;
    return (float)Math.sqrt(Math.pow((double)(xx- px), 2) + Math.pow((double)(yy-py), 2));
  }

  private boolean isPlayerGrounded(float deltaTime) {

    if (m_groundCheckTicks > 0) 
    {
      m_groundCheckTicks--;
      return true;
    }
  
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

  public void update(float deltaTime)
  {
    if (m_state == 2)
      return;

    boolean isRightPressed = false;
    boolean isLeftPressed = false;

    if (m_state > 9)
    {
      m_ticks++;
      if (m_ticks > 30)
      {
        //exiting
        float dx = m_targetX - (this.getX() + this.getWidth()/2);
        if (Math.abs(dx) > 3)
        {
          if (dx > 0)
            isRightPressed = true;
          else
            isLeftPressed = true;
        } else
        {
          m_body.setLinearVelocity(0,0);
          if (m_exitAnimation.isRunning() == false)
          {
            this.runAnimation(m_standingAnimation);
            this.runAnimation(m_exitAnimation);
            if (m_ownsPowerUnit == true)
            {
              GameAnimateable fade = new AnimateFadeOut(0.5f);
              m_powerUnit.runAnimation(fade);
            }

            m_state = 11;
          }
          return;
        }
      }
    }

	
    if (m_playerControlled)
    {
      if (m_ownsPowerUnit == false)
      {
        m_powered = false;
        //if (m_powerUnit.canPickUp())
        //{
        //  if ((dist(m_powerUnit.getX(), m_powerUnit.getY())) < 100)
        //  {
        //    m_powered = true;
        //  }
       // }
      } else
      {
        if (m_lastDx > 0)
        {
          m_powerUnit.setFlip(false,false);
          this.setFlip(false,false);
        }
        else if (m_lastDx < 0)
        {
          m_powerUnit.setFlip(true,false);
          this.setFlip(true,false);
        }
      }
    } else
    {
      if (m_state == 10)
      {
        if (m_lastDx > 0)
        {
          this.setFlip(false,false);
        }
        else if (m_lastDx < 0)
        {
          this.setFlip(true,false);
        }
      }

      if (m_state < 11)
      {
        if (!m_onGround)
        {
          m_onGround = isPlayerGrounded(Gdx.graphics.getDeltaTime());
        }

        if (m_onGround)
        {
          if (m_standingAnimation.isRunning() == false)
          {
            this.stopAllAnimations();
            this.runAnimation(m_standingAnimation);
          }
        }
      }
    }

    if ((m_powered) && (m_playerControlled))
    {
      isRightPressed = m_controller.isRightPressed();
      isLeftPressed = m_controller.isLeftPressed();
    }

    if (m_powered)
    {
      Vector2 cv = m_body.getLinearVelocity();
      boolean notMoved = true;

      m_onGround = isPlayerGrounded(Gdx.graphics.getDeltaTime());
      if(m_onGround) {
        lastGroundTime = System.nanoTime();
      } else {
        if(System.nanoTime() - lastGroundTime > 100000000) {
          //m_onGround = true;
        }
      }

      if (isRightPressed)
      {
        if (checkDir(1) == false)
        {
          m_body.applyForceToCenter(m_hForce,0,true);
        }
        m_lastDx = 1;
        notMoved = false;
        stillTime = 0;
      } else if (isLeftPressed)
      {
        if (checkDir(0) == false)
        {
          m_body.applyForceToCenter(-m_hForce,0,true);
        }
        m_lastDx = -1;
        notMoved = false;
        stillTime = 0;
      } else {		
			    stillTime += Gdx.graphics.getDeltaTime();
          m_body.setLinearVelocity(cv.x * 0.9f, cv.y);
      }


      
      if (Math.abs(cv.x) > m_maxX)
      {
        if (cv.x > 0) cv.x = m_maxX;
        else cv.x = -m_maxX;
        m_body.setLinearVelocity(cv);
      }



      if(!m_onGround) {			
        m_fixture.setFriction(0f);
        playerSensorFixture.setFriction(0f);			
        if (m_jumpingAnimation.isRunning() == false)
        {
          this.stopAllAnimations();
          this.runAnimation(m_jumpingAnimation);
        }
      } else {

        if (Math.abs(cv.x) < 0.5f)
        {
          if (m_jumpingAnimation.isRunning())
          {
            if ((m_standingAnimation.isRunning() == false) && (m_landingAnimation.isRunning() == false))
            {
              this.stopAllAnimations();
              this.chainAnimations(m_landingAnimation,m_standingAnimation);
            }
          } else
          {
            if ((m_standingAnimation.isRunning() == false) && (m_landingAnimation.isRunning() == false))
            {
              this.stopAllAnimations();
              this.runAnimation(m_standingAnimation);
            }
          }

        } else
        {
          if ((m_runningAnimation.isRunning() == false) && (m_jumpTicks < 1))
          {
            this.stopAllAnimations();
            this.runAnimation(m_runningAnimation);
          }
        }

        if(notMoved && stillTime > 0.2) {
          m_fixture.setFriction(100f);
          playerSensorFixture.setFriction(100f);
          if(platform!=null) {
        	  m_body.setLinearVelocity(platform.getLinearVelocity().x,m_body.getLinearVelocity().y);
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
    }
    if ((m_powered) && (m_playerControlled))
    {

      if (m_controller.isFirePressed())
      {
        if (m_ownsPowerUnit)
        {
          m_firePressedTicks++;
        }
      } else
      {
        if (m_firePressedTicks > 0)
        {
          throwUnit();
          m_firePressedTicks = 0;
        }
      }
    } else
    {
      Vector2 cv = m_body.getLinearVelocity();
      m_body.setLinearVelocity(cv.x * 0.9f, cv.y);
      if(platform!=null) {
    	  m_body.setLinearVelocity(platform.getLinearVelocity().x,m_body.getLinearVelocity().y);
      }
    }

    if (m_controlDelayTicks > 0)
    {
      m_controlDelayTicks--;
    } else if (m_playerControlled)
    {
      //if (m_controller.isTriggerPressed())
      //{
      //  m_playerControlled = false;
      //  m_otherPlayer.playerTakeControl();
      //}
    }

    //now do vertical
    if ((m_powered) && (m_playerControlled))
    {
      if (m_onGround)
      {
        if (m_controller.isJumpPressed())
        {
          if (m_jumpTicks > 0)
          {
            m_jumpTicks--;
          } else
          {
        	  jump();
            m_jumpTicks = 15;
          }
        }
      } else
      {
        if (m_jumpTicks > 0) m_jumpTicks--;
        if ((m_jumpTicks > 0) && (m_controller.isJumpPressed()))
        {
          m_body.applyForceToCenter(0, m_jumpForce,true);
        } else
        {
          m_jumpTicks = 0;
        }
      }
    }

    this.setPositionToBody();
    calcBrainRectangle();

    if (m_throwDelayTicks > 0) 
    {
      m_throwDelayTicks--;
    }
    else
    {
      if (m_state < 10)
      {
        if (m_powerUnit.canPickUp())
        {
          if (Intersector.overlaps(m_brainRectangle, m_powerUnit.getBoundingRectangle()))
          {
            float dly = m_powerUnit.m_body.getLinearVelocity().y - m_body.getLinearVelocity().y;
            if (dly < 0.25f)
            {
              m_powerUnit.pickUp(this);
              this.playerTakeControl();
            }
            //float dly = m_powerUnit.m_body.getLinearVelocity().y - m_body.getLinearVelocity().y;
            //if (dly < 0.5f)
            //{
            //  m_powerUnit.pickUp(this);
            //  this.playerTakeControl();
            //}
          }
        }
      }
    }
  }
  
  public void jump() {
	 
	  if(trampoline_state != Trampoline.NONE) {
		  m_body.applyLinearImpulse(0, m_jumpDY*Trampoline.Multiplier, 0, 0, true);
		  m_jumpTicks = 15;
	  }else {
		  m_body.applyLinearImpulse(0, m_jumpDY, 0, 0, true);
	      m_jumpTicks = 15;
	  }
      m_onGround = false;
  }

  public void setPositionToBody()
  {
    float cx = m_body.getPosition().x*Box2dVars.PIXELS_PER_METER;
    float cy = m_body.getPosition().y*Box2dVars.PIXELS_PER_METER;
    this.setPosition(cx - this.getWidth()/2, cy - this.getHeight()/2 + m_yOff);
  }

  public void powerOn()
  {
    m_powered = true;
  }

  public void setPowerUnit(PowerUnit pu)
  {
    m_powerUnit = pu;
    m_powered = true;
    m_ownsPowerUnit = true;
  }

  public void startLevel()
  {
    m_state = 0; //resume
    m_groundCheckTicks = 4;
  }

  public void powerOff()
  {
    m_powered = false;
  }

  public void throwUnit()
  {
    if (m_firePressedTicks > 30)
    {
      return;
    }

    if (m_ownsPowerUnit == false) return;
    if (m_powerUnit == null) return;
    if (m_powered == false) return;

    m_ownsPowerUnit = false;
    m_powered = false;

    float r = 2f;
    float ry = 4f;

    if (m_lastDx > 0)
    {
      m_powerUnit.throwUnit(r,ry);
       m_throwDelayTicks = 40;
       m_playerControlled = false;
    } else
    {
      m_powerUnit.throwUnit(-r,ry);
      m_throwDelayTicks = 40;
      m_playerControlled = false;
    }
  }

  public void playerTakeControl()
  {
    m_playerControlled = true;
    m_controlDelayTicks = 30;
    stillTime = 0;
  }

  public void removeControl()
  {
    m_playerControlled = false;
  }

  public void setBodyPosition(float xx, float yy)
  {
    this.setPosition(xx,yy);
    m_body.setTransform((xx + this.getWidth()/2)/Box2dVars.PIXELS_PER_METER , (yy + this.getHeight()/2)/Box2dVars.PIXELS_PER_METER, this.getRotation()/180f * 3.14f);
  }

  public void setActive(boolean b)
  {
    this.setVisible(b);
  }

  public boolean isAlive()
  {
    return true;
  }

  
  /////COLLISION HANDLING EXAMPLE -------------------------------
  	//NOTE: in order to make any changes to the physics world or any physics related object
    //		be sure to call Gdx.app.postRunnable() or you will stall the thread. 
	@Override
	public void handleBegin(Box2dCollision collision) {
		//only check the collision of the player foot not the body. 
		if(collision.self_type == Box2dVars.PLAYER_FOOT) {
			//the collision target type is the type(categoryBits) of the thing we are colliding with. 
			
//			Gdx.app.log("PlayerContactTest:","Target Collision Category="+collision.target_type);
//			
//			if(collision.target_type == Box2dVars.FLOOR) {
//				Gdx.app.log("PlayerContactTest:", "we touched a FLOOR!");
//			}
//			
			//if the collision target is another gameMapObject we can access it from here
			//i thought of removing this and just implementing it as Object and letting the programmer
			//cast it themselves whenever they need to but this is simpler for now. 
			if(collision.target!=null) {
				//do stuff with the collision target
				GameMapObject obj = (GameMapObject) collision.target;
			}
		}
		
		if(collision.target_type == Box2dVars.PLATFORM) {
			platform = collision.target.m_body;
		}
		
		if(collision.target_type == Box2dVars.HAZARD) {
			Gdx.app.log("PlayerContactTest:", "we touched a HAZARD! YOU ARE DED!");
			Gdx.app.postRunnable(new Runnable() {
				
				@Override
				public void run() {

          if (m_state < 9)
          {
            //PLAYER DEATH LOGIC HERE --------------------------------------
            int stage = Integer.parseInt(GameMain.getSingleton().getGlobal("m_stage"));
            int level = Integer.parseInt(GameMain.getSingleton().getGlobal("m_level"));
            MainLayer ml = new MainLayer();
                ml.loadLevel(stage,level);
                GameMain.getSingleton().replaceActiveLayer(ml);
          }
					
				}
			});	
		}
	}
	
	
	
	@Override
	public void handleEnd(Box2dCollision collision) {
		if(collision.target_type == Box2dVars.PLATFORM) {
			platform = null;
		}
		
	}
  
}

