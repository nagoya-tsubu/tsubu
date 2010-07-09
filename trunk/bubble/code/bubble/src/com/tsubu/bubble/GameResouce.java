package com.tsubu.bubble;

import twExpress.*;

public class GameResouce
{
	private TweAnimation2D animeBubble;
	private TweAnimation2D animeNumber;
	private TweImage backgroundImage;
	private TweImage tsubuIcon;
	private TweSound soundBgm;
	private TweSound soundBubbleTouch;
	private TweSound soundBubbleBreak;
	
	public GameResouce( TweActivityInterface activityInterface )
	{
		this.animeBubble = new TweAnimation2D( activityInterface );
		this.animeNumber = new TweAnimation2D( activityInterface );
		this.backgroundImage = new TweImage( activityInterface );
		this.tsubuIcon = new TweImage( activityInterface );
		this.soundBubbleTouch = new TweSound( activityInterface );
		this.soundBubbleBreak = new TweSound( activityInterface );
		this.soundBgm = new TweSound( activityInterface );
		
	}
	
	public void load()
	{
		this.backgroundImage.loadFromFile( "/sdcard/background.jpg" );
		
		this.animeBubble.loadFromFile( "/sdcard/bubble.png" );
		this.animeBubble.setAnimationSize( 64 , 64 );
		this.animeBubble.setHotspot( this.animeBubble.getAnimationWidth()/2.0f , this.animeBubble.getAnimationHeight()/2.0f );
		
		this.soundBubbleTouch.loadFromFile( "/sdcard/buble_touch.ogg" );
		this.soundBubbleBreak.loadFromFile( "/sdcard/buble_break.ogg" );

		this.tsubuIcon.loadFromFile( "/sdcard/tsubu_icon.png" );
		
		this.soundBgm.loadFromFile( "/sdcard/background.ogg" );

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
		this.soundBgm.unload();
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
	
	public TweImage getBackground()
	{
		return this.backgroundImage;
	}

	public TweSound getBgm()
	{
		return this.soundBgm;
	}
	
	public TweImage getTsubuIcon()
	{
		return this.tsubuIcon;
	}
	
	
}
