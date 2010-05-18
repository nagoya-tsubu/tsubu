package com.tsubu.bubble;

import twExpress.*;

public class ScenePlay extends Scene
{
	
	private GameManager gameManager;
	
	private TweString2D tempString;
	
	public ScenePlay( TweActivityInterface activityInterface , GameManager gameManager )
	{
		this.tempString = new TweString2D( activityInterface , "temp_string" );
		this.tempString.loadString( 20 , "ぷれい がめん" );
		
		this.gameManager = gameManager;
	}
	
	@Override 
	public void finish()
	{
		
	}
	
	@Override 
	public SceneResult action()
	{
		
		this.gameManager.action();
		
		return SceneResult.CONTINUE;
	}

	@Override 
	public void draw()
	{
		this.gameManager.draw();
		
		this.tempString.draw( 0 , 0 );
	}

}
