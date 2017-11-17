package com.strayvoltage.gameoff;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.strayvoltage.gamelib.GameSprite;

public class Box2dCollisionAdapter implements ContactListener{
	
	Box2dCollision col;
	
	public Box2dCollisionAdapter() {
		col = new Box2dCollision(Box2dVars.OBJECT,Box2dVars.OBJECT, null);
	}

	@Override
	public void beginContact(Contact contact) {
		col.target = null;
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		Box2dCollisionHandler bch = null;
		if(a.getUserData()!=null&&a.getUserData() instanceof Box2dCollisionHandler) {
			bch = (Box2dCollisionHandler) a.getUserData();
			col.self_type = a.getFilterData().categoryBits;
			col.target_type = b.getFilterData().categoryBits;
			if(b.getUserData()!=null&&b.getUserData() instanceof GameSprite) {
				col.target =  (GameSprite) b.getUserData();
			}
			bch.handleBegin(col);
		}
		col.target = null;
		if(b.getUserData()!=null&&b.getUserData() instanceof Box2dCollisionHandler) {
			bch = (Box2dCollisionHandler) b.getUserData();
			col.self_type = b.getFilterData().categoryBits;
			col.target_type = a.getFilterData().categoryBits;
			if(a.getUserData()!=null&&a.getUserData() instanceof GameSprite) {
				col.target = (GameSprite) a.getUserData();
			}
			bch.handleBegin(col);
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		col.target = null;
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		Box2dCollisionHandler bch = null;
		if(a.getUserData()!=null&&a.getUserData() instanceof Box2dCollisionHandler) {
			bch = (Box2dCollisionHandler) a.getUserData();
			col.self_type = a.getFilterData().categoryBits;
			col.target_type = b.getFilterData().categoryBits;
			if(b.getUserData()!=null&&b.getUserData() instanceof GameSprite) {
				col.target = (GameSprite) b.getUserData();
			}
			bch.handleEnd(col);
		}
		col.target = null;
		if(b.getUserData()!=null&&b.getUserData() instanceof Box2dCollisionHandler) {
			bch = (Box2dCollisionHandler) b.getUserData();
			col.self_type = b.getFilterData().categoryBits;
			col.target_type = a.getFilterData().categoryBits;
			if(a.getUserData()!=null&&a.getUserData() instanceof GameSprite) {
				col.target =(GameSprite) a.getUserData();
			}
			bch.handleEnd(col);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		Body platform = null;
		Body object = null;
		
		if(contact.getFixtureA().getFilterData().categoryBits == Box2dVars.PLATFORM) {
			platform  = contact.getFixtureA().getBody();
			if(contact.getFixtureB().getFilterData().categoryBits!=Box2dVars.PLATFORM_STOP)
				object = contact.getFixtureB().getBody();
		}else if(contact.getFixtureB().getFilterData().categoryBits == Box2dVars.PLATFORM){
			platform = contact.getFixtureB().getBody();
			if(contact.getFixtureA().getFilterData().categoryBits!=Box2dVars.PLATFORM_STOP)
				object = contact.getFixtureA().getBody();
		}
		
		
		//if this contact contains a platform check the collision
		if(platform!=null && object!=null ) {
			GameMapObject p_object = (GameMapObject) platform.getUserData();
			if(((Platform)p_object).oneWay) {
				if(object.getLinearVelocity().y >= 0 && object.getPosition().y<platform.getPosition().y+(p_object.getHeight()+1)/Box2dVars.PIXELS_PER_METER) {
					contact.setEnabled(false);
				}
			}
			
		}
			
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

}
