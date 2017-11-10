package com.strayvoltage.gameoff;

public class Box2dCollision {
	/**
	 * the category bits of self.(or A)
	 */
	public short self_type;
	/**
	 * the box2dVars category bit of the target(or B)
	 * used to identify the type of collision. 
	 */
	public short target_type;
	/**
	 * the target object which this object has made contact with.
	 * It can be null for such things as walls which do not have 
	 * a gameobject. in that case use 'type' to identify.
	 */
	public GameMapObject target;
	
	public Box2dCollision(short self_type,short target_type,GameMapObject target) {
		this.self_type = self_type;
		this.target_type = target_type;
		this.target = target;
	}
}
