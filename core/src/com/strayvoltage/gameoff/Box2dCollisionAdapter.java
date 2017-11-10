package com.strayvoltage.gameoff;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class Box2dCollisionAdapter implements ContactListener{
	
	Box2dCollision col;
	
	public Box2dCollisionAdapter() {
		col = new Box2dCollision(Box2dVars.OBJECT,Box2dVars.OBJECT, null);
	}

	@Override
	public void beginContact(Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		Box2dCollisionHandler bch = null;
		if(a.getUserData()!=null&&a.getUserData() instanceof Box2dCollisionHandler) {
			bch = (Box2dCollisionHandler) a.getUserData();
			col.self_type = a.getFilterData().categoryBits;
			col.target_type = b.getFilterData().categoryBits;
			if(b.getUserData()!=null&&b.getUserData() instanceof GameMapObject) {
				col.target = (GameMapObject) b.getUserData();
			}
			bch.handleBegin(col);
		}
		if(b.getUserData()!=null&&b.getUserData() instanceof Box2dCollisionHandler) {
			bch = (Box2dCollisionHandler) b.getUserData();
			col.self_type = b.getFilterData().categoryBits;
			col.target_type = a.getFilterData().categoryBits;
			if(a.getUserData()!=null&&a.getUserData() instanceof GameMapObject) {
				col.target = (GameMapObject) a.getUserData();
			}
			bch.handleBegin(col);
		}
		
	}

	@Override
	public void endContact(Contact contact) {
		Fixture a = contact.getFixtureA();
		Fixture b = contact.getFixtureB();
		Box2dCollisionHandler bch = null;
		if(a.getUserData()!=null&&a.getUserData() instanceof Box2dCollisionHandler) {
			bch = (Box2dCollisionHandler) a.getUserData();
			col.self_type = a.getFilterData().categoryBits;
			col.target_type = b.getFilterData().categoryBits;
			if(b.getUserData()!=null&&b.getUserData() instanceof GameMapObject) {
				col.target = (GameMapObject) b.getUserData();
			}
			bch.handleEnd(col);
		}
		if(b.getUserData()!=null&&b.getUserData() instanceof Box2dCollisionHandler) {
			bch = (Box2dCollisionHandler) b.getUserData();
			col.self_type = b.getFilterData().categoryBits;
			col.target_type = a.getFilterData().categoryBits;
			if(a.getUserData()!=null&&a.getUserData() instanceof GameMapObject) {
				col.target = (GameMapObject) a.getUserData();
			}
			bch.handleEnd(col);
		}
	}

	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {
		
	}

	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		
	}

}
