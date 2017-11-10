package com.strayvoltage.gameoff;

public interface Box2dCollisionHandler {
	/**
	 * use it to handle contact begin
	 * @param collision - contains collision category data and target of collision
	 */
	public void handleBegin(Box2dCollision collision);
	/**
	 * same as handleBegin only that it is used to handle 
	 * the end of collisions. 
	 * @param collision
	 */
	public void handleEnd(Box2dCollision collision);
}
