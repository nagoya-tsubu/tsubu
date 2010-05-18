package com.tsubu.bubble;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import twExpress.*;

// ---------------------------------
//  ゲーム画面の管理 
// ---------------------------------	
public class GameManager implements GameListener 
{
	private TweActivityInterface activityInterface;
	
	private GameResouce resouce;
	
	private Random random;
	
	private int frameCount;
	
	private ArrayList<Bubble> bubbleList;
	
	private TweShapeFillRect shapeBackScreen;
	
	// ---------------------------------
	//  こんすとらくたー 
	// ---------------------------------	
	public GameManager( TweActivityInterface activityInterface )
	{
		this.activityInterface = activityInterface;
		
		// 画像や 音を 読み出す
		this.resouce = new GameResouce( activityInterface );
		
		// 起動したらすぐにリソースを読み出す
		this.resouce.load();
		
		
		// らんだむーん
		this.random = new Random();
		
		this.frameCount = 0;
		
		this.bubbleList = new ArrayList<Bubble>();
		
		
		this.shapeBackScreen = new TweShapeFillRect( this.activityInterface );
		this.shapeBackScreen.setColor( TweVertexPoint.TOP_LEFT     , 0.8f , 0.8f , 1.0f );
		this.shapeBackScreen.setColor( TweVertexPoint.TOP_RIGHT    , 0.8f , 0.8f , 1.0f );
		this.shapeBackScreen.setColor( TweVertexPoint.BOTTOM_LEFT  , 0.0f , 0.0f , 0.5f );
		this.shapeBackScreen.setColor( TweVertexPoint.BOTTOM_RIGHT , 0.0f , 0.0f , 0.5f );
		this.shapeBackScreen.setRect( 0 , 0 , this.activityInterface.getScreenWidth() , this.activityInterface.getScreenHeight() );
		
		
	}

	// ---------------------------------
	//  動作処理  
	// ---------------------------------		
	public void action()
	{
		this.frameCount ++;
		
		if( this.frameCount % 20 == 0 )
		{
			this.bubbleList.add( new BubbleNormal( this ) );
		}
		
		for( Iterator<Bubble> iterator = this.bubbleList.iterator() ; iterator.hasNext(); )
		{
			Bubble bubble = iterator.next();
			
			if( !bubble.action() )
			{
				iterator.remove();
			}
		}
		
		
		if( this.activityInterface.getScreenPush() )
		{
			boolean catched;
			float touchX = this.activityInterface.getTouchPoint().getPointX();
			float touchY = this.activityInterface.getTouchPoint().getPointY();
			
			for( int touchId = 0 ; touchId<this.bubbleList.size() ; touchId++ )
			{
				Bubble bubble = this.bubbleList.get(touchId);
				catched = bubble.touch( touchX , touchY );
				
				if( catched )
				{
					break;
				}
			}
		}
		
	}

	// ---------------------------------
	//  描画処理  
	// ---------------------------------		
	public void draw()
	{
		this.shapeBackScreen.draw();
		
		for( int drawId = 0 ; drawId<this.bubbleList.size() ; drawId++ )
		{
			this.bubbleList.get( drawId ).draw();
		}
		
	}
	
	// ---------------------------------
	//  終了処理 
	// ---------------------------------		
	public void finish()
	{
		// リソースを開放
		this.resouce.unload();
	}
	
	// ---------------------------------
	//  Activityとのインタフェイス 
	// ---------------------------------
	public TweActivityInterface getActivityInterface()
	{
		return this.activityInterface;
	}

	// ---------------------------------
	//  ゲームリソース 
	// ---------------------------------	
	public GameResouce getResouce()
	{
		return this.resouce;
	}

	// ---------------------------------
	//  らんだむーん　をとるーん 
	// ---------------------------------
	@Override
	public Random getRandom()
	{
		return this.random;
	}
}
