package com.tsubu.bubble;

import twExpress.*;

public class BubbleActivity extends TweActivity 
{
	private Scene scene;
	
	// ゲーム管理オブジェクトゥ
	private GameManager gameManager;
	
	// -------------------------
	// コンストラクツゥ
	// -------------------------
	public BubbleActivity()
	{
		this.scene = null;
		this.gameManager = null;
	}
	
	/** -
	 * 画面の初期設定
	 * @param config 設定オブジェクト
	 */
	@Override
	protected void initalizeActivity( TweActivityConfig config )
	{
		// 一秒間に３０回 画面を更新する
		config.setFps( 30.0f );
		
		// バイブレータ機能を使う
		config.setUseDeviceVivrate( true );
		
		// 背景色設定
		config.setBackColor( 0.5f , 0.5f , 0.5f );
	}
	

	/** -
	 * 画面の起動時の処理
	 */	
	@Override
	protected void eventCreate()
	{
		this.gameManager = new GameManager( this );
		
		// 最初はスタートメニュー画面
		this.scene = new SceneStartMenu( this , gameManager );
	}
	
	/**
	 * 画面の終了時の処理
	 */	
	@Override
	protected void eventDestroy()
	{
		if( this.gameManager!= null )
			this.gameManager.finish();
		
		if( this.scene!= null )
		{
			this.scene.finish();
			this.scene = null;
		}
	}
	

	/**
	 * フレーム毎の処理
	 */	
	@Override
	protected void eventActionFrame()
	{
		if( this.scene != null )
		{
			SceneResult sceneResult;
			
			sceneResult = this.scene.action();
			
			switch( sceneResult )
			{
				case CONTINUE:
					break;
					
				case TO_PLAY:
					
					this.scene.finish();
					this.scene = new ScenePlay( this , this.gameManager );
					
				default:
					break;
			}
		}
	}
	
	/**
	 * フレーム毎の描画処理
	 */	
	@Override
	protected void eventDrawFrame()
	{
		if( this.scene != null )
		{
			this.scene.draw();
		}
	}
}