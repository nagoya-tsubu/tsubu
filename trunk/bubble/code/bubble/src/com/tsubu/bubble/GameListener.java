package com.tsubu.bubble;

import java.util.Random;

import twExpress.TweActivityInterface;

public interface GameListener
{
	public TweActivityInterface getActivityInterface();
	
	public GameResouce getResouce();
	
	public Random getRandom();
	
}
