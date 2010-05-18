package com.tsubu.bubble;

import twExpress.*;

public class SceneStartMenu extends Scene
{
	private TweActivityInterface activityInterface;
	
	private TweString2D tempString;
	
	public SceneStartMenu( TweActivityInterface activityInterface , GameManager gameManager )
	{
		this.tempString = new TweString2D( activityInterface , "temp_string" );
		this.tempString.loadString( 20 , "おーぷにんぐ がめん touch me!" );
		
		this.activityInterface = activityInterface;
	}
	
	@Override 
	public void finish()
	{
		
	}
	
	@Override 
	public SceneResult action()
	{
		if( this.activityInterface.getScreenPush() )
		{
			return SceneResult.TO_PLAY;
		}
		
		return SceneResult.CONTINUE;
	}

	@Override 
	public void draw()
	{
		this.tempString.draw( 0 , 0 );
	}

}
