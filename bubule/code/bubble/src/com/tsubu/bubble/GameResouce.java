package com.tsubu.bubble;

import twExpress.*;

public class GameResouce
{
	private TweAnimation2D animeBubble;
	private TweAnimation2D animeNumber;
	private TweSound soundBubbleTouch;
	private TweSound soundBubbleBreak;
	
	public GameResouce( TweActivityInterface activityInterface )
	{
		this.animeBubble = new TweAnimation2D( activityInterface );
		this.soundBubbleTouch = new TweSound( activityInterface );
		this.soundBubbleBreak = new TweSound( activityInterface );
		this.animeNumber = new TweAnimation2D( activityInterface );
		
	}
	
	public void load()
	{
		this.animeBubble.loadFromFile( "/sdcard/bubble.png" );
		this.animeBubble.setAnimationSize( 64 , 64 );
		this.animeBubble.setHotspot( this.animeBubble.getAnimationWidth()/2.0f , this.animeBubble.getAnimationHeight()/2.0f );
		
		this.soundBubbleTouch.loadFromFile( "/sdcard/buble_touch.ogg" );
		this.soundBubbleBreak.loadFromFile( "/sdcard/buble_break.ogg" );
		
		// 数字画像
		this.animeNumber.loadFromResouce( R.drawable.number );
		this.animeNumber.setAnimationSize( 16 , 32 );
		
	}
	
	
	public void unload()
	{
		this.animeBubble.unload();
		this.soundBubbleBreak.unload();
		this.soundBubbleTouch.unload();
		this.animeNumber.unload();
	}

	public TweAnimation2D getAnimeBubble()
	{
		return this.animeBubble;
	}
	

	public TweSound getSoundTouch()
	{
		return this.soundBubbleTouch;
	}
	
	public TweSound getSoundBreak()
	{
		return this.soundBubbleBreak;
	}
	
	public TweAnimation2D getAnimeNumber()
	{
		return this.animeNumber;
	}
	
}
