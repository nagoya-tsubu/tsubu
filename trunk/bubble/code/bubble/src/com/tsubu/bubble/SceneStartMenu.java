package com.tsubu.bubble;

import twExpress.*;

public class SceneStartMenu extends Scene
{
	private TweActivityInterface activityInterface;
	
	private TweString2D tempString;
	
	private GameManager gameManager;
	
	private TweImage imageTsubuIcon;
	
	private TweImage imageBackground;
	
	private int fadeInRemitTimer = 0;
	
	private final int fadeInRemitMax = 120;
	
	private int fadeOutRemitTimer = 0;
	
	private final int fadeOutRemitMax = 30;
	
	private boolean isExit;
	
	public SceneStartMenu( TweActivityInterface activityInterface , GameManager gameManager )
	{
		this.tempString = new TweString2D( activityInterface , "temp_string" );
		this.tempString.loadString( 20 , "つ部 PRESENTS" );
		this.tempString.setHotspot( this.tempString.getStringWidth()/2.0f , this.tempString.getStringHeight()/2.0f );
		
		this.gameManager = gameManager;
		
		this.activityInterface = activityInterface;
		
		this.imageBackground = new TweImage( activityInterface , "backgroundImage" );
		this.imageBackground.loadFromFile( "/sdcard/background.jpg" );
		
		this.imageTsubuIcon = new TweImage( activityInterface , "backgroundImage" );
		this.imageTsubuIcon.loadFromFile( "/sdcard/tsubu_icon.png" );
		this.imageTsubuIcon.setHotspot( this.imageTsubuIcon.getImageWidth()/2.0f ,  this.imageTsubuIcon.getImageHeight()/2.0f  );
		
		this.fadeInRemitTimer = this.fadeInRemitMax;
		this.fadeOutRemitTimer = this.fadeOutRemitMax;
		
		this.isExit = false;
	}
	
	@Override 
	public void finish()
	{
		this.imageBackground.unload();
		this.imageTsubuIcon.unload();
		
	}
	
	@Override 
	public SceneResult action()
	{
		if( this.fadeInRemitTimer > 0 )
			this.fadeInRemitTimer --;
		
		if( this.fadeOutRemitTimer > 0 && this.isExit )
			this.fadeOutRemitTimer --;
		
		if( this.activityInterface.getScreenPush() )
		{
			this.isExit = true;
		}

		if( this.isExit && this.fadeOutRemitTimer == 0)
		{
			return SceneResult.TO_PLAY;
		}
		return SceneResult.CONTINUE;
	}

	@Override 
	public void draw()
	{
		float ratio = 1.0f - (float)this.fadeInRemitTimer / (float)this.fadeInRemitMax;
		
		this.imageBackground.setAlpha( ratio );
		this.imageBackground.draw( 0 , 0 );
		

		if( this.isExit )
			 ratio = (float)this.fadeOutRemitTimer / (float)this.fadeOutRemitMax;

		this.tempString.setAlpha( ratio );
		this.tempString.draw( this.activityInterface.getScreenWidth()/2.0f , 350 );

		if( this.isExit )
			this.imageTsubuIcon.setAlpha( ratio );
		else
			this.imageTsubuIcon.setColor( ratio , ratio , ratio );
		
		this.imageTsubuIcon.draw( this.activityInterface.getScreenWidth()/2.0f , this.activityInterface.getScreenHeight()/2.0f );
	}

}
