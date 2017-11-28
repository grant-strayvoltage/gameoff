package com.strayvoltage.gameoff;

public class Box2dVars {
	
	public static final float PIXELS_PER_METER = 32.0f;
	
	//USE PowerOfTwo
	public static final short BLOCK = 2;   //blocks to be used for several different ways to solve puzzles
	public static final short PLATFORM = 4;     
	public static final short PLATFORM_STOP = 8; //stops for platforms
	public static final short SWITCH = 16;   //all switches
	public static final short POWER = 32;   //power unit(brain)
	public static final short PLAYER_NORMAL = 64;   //switched on when player is not jumping or falling
	public static final short PLAYER_JUMPING = 128; //switched on when player is jumping
	public static final short HAZARD = 256; //causes death
	public static final short FLOOR = 512;  //should collide with every dynamic body that has gravity
	public static final short OBJECT = 1024; //used for anything non specific where collision type doesnt matter
	public static final short PLAYER_FOOT = 2048;
	public static final short BRAIN_FOOT = 4096;
	public static final short ENEMY = 8192; //enemies that move about and interact with objects
	public static final short FAN = 1;
	
}
