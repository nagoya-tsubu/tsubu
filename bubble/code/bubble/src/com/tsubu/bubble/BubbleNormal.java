package com.tsubu.bubble;

import twExpress.*;

public class BubbleNormal implements Bubble
{
	private GameListener gameListener;
	
	private TweAnimation2D animeBubble;
	
	private float scale;
	
	class BubbleColor
	{
		public float red , green , blue;
	};
	
	private BubbleColor[] bubbleColor;
	
	private final int COLOR_SET_MAX = 4; // 色は４箇所に設定
	
	private float rotate;
	
	//------
	// しゃぼんだま　の状態
	//------
	enum Status
	{
		//INITAL , 
		NORMAL , // ふつーの状態　少しずつ上にあがってくよ
		DAMAGE , // 指で押した状態。　何度も押すと 割れちゃう
		DESTROY , // たくさん 押したので割れちゃった・・・
	}
	
	// 状態
	private Status status;
	
	// 座標
	private float positionX;
	private float positionY;

	// 画面に登場してからのフレーム数 
	private int frameCount;
	
	// しゃぼんだまの耐久度 ０になると割れちゃう
	private int life;
	
	public BubbleNormal( GameListener gameListener )
	{
		this.gameListener = gameListener;
	
		// 泡画像を取得
		this.animeBubble = gameListener.getResouce().getAnimeBubble();
		
		this.status = Status.NORMAL;
		
		// 座標は画面下から 横ランダムで登場
		this.positionX = gameListener.getRandom().nextInt( (this.gameListener.getActivityInterface().getScreenWidth()-50 ) ) + 25;
		this.positionY = this.gameListener.getActivityInterface().getScreenHeight() + 100;
		
		this.frameCount = 0;
		
		// 耐久度は 1～3
		this.life = gameListener.getRandom().nextInt(3)+1;

		this.scale = 1.0f;
		
		bubbleColor = new BubbleColor[ COLOR_SET_MAX ];
		
		for( int colorId=0 ; colorId<COLOR_SET_MAX ; colorId++ )
		{
			bubbleColor[ colorId ] = new BubbleColor();
			bubbleColor[ colorId ].red   = (float)gameListener.getRandom().nextInt( 100 ) / 100.0f;
			bubbleColor[ colorId ].green = (float)gameListener.getRandom().nextInt( 100 ) / 100.0f;
			bubbleColor[ colorId ].blue  = (float)gameListener.getRandom().nextInt( 100 ) / 100.0f;
		}
		
		
		this.rotate = 0;
	}
	
	
	//----------------
	// 指でタッチしたときの処理
	//----------------
	@Override
	public boolean touch( float touchX , float touchY )
	{
		float width  = touchX - this.positionX;
		float height = touchY - this.positionY;
	
		if( this.status != Status.NORMAL )
			return false;
		
		if( java.lang.Math.sqrt( width*width + height*height) < 50 )
		{
			this.life --;
			
			if( this.life > 0 )
			{
				this.status = Status.DAMAGE;
				this.frameCount = 0;
				this.gameListener.getResouce().getSoundTouch().play();
			}
			else
			{
				this.status = Status.DESTROY;
				this.frameCount = 0;
				this.gameListener.getResouce().getSoundBreak().play();
				
				this.gameListener.getActivityInterface().setVibrate( 60 );
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean action()
	{
		this.frameCount ++;
		
		if( this.status == Status.NORMAL )
			this.positionY -= 3;
		
		this.positionX -= TweMath.sin( (float)this.frameCount/10.0f )*1;
		
		if( this.positionY < -100 )
			return false;
		
		if( this.frameCount>=8 && this.status == Status.DAMAGE )
		{
			this.status = Status.NORMAL;
			this.frameCount = 0;
		}
			
		if( this.frameCount>=8 && this.status == Status.DESTROY )
			return false;
		
		if( this.status == Status.NORMAL )
			this.rotate = (float)(java.lang.Math.sin( (double)frameCount/30.0 )*360.0);
		
		return true;
	}

	@Override
	public void draw()
	{
		
		this.animeBubble.setRotate( this.rotate );
		this.animeBubble.setScale( this.scale );
		this.animeBubble.setBlendMode( TweBlendMode.ADD );
		
		for( int colorId=0 ; colorId<this.COLOR_SET_MAX ; colorId++ )
		{
			TweVertexPoint point = TweVertexPoint.BOTTOM_LEFT;
			
			BubbleColor color = this.bubbleColor[ colorId ];
			
			switch( colorId )
			{
				case 0 : point=TweVertexPoint.BOTTOM_LEFT;  break;
				case 1 : point=TweVertexPoint.BOTTOM_RIGHT; break;
				case 2 : point=TweVertexPoint.TOP_LEFT;     break;
				case 4 : point=TweVertexPoint.TOP_RIGHT;    break;
				default:
			}
			
			this.animeBubble.setColor( point , color.red , color.green , color.red );
		}
		
		switch( this.status )
		{
		case DAMAGE:
			this.animeBubble.setAnimationIndex( this.frameCount % 8+8 );
			this.animeBubble.draw( this.positionX , this.positionY );
			break;
		case DESTROY:
			this.animeBubble.setAnimationIndex( this.frameCount % 8+16 );
			this.animeBubble.draw( this.positionX , this.positionY );
			break;
		default:
			this.animeBubble.setAnimationIndex( this.frameCount/3 % 8 );
			this.animeBubble.draw( this.positionX , this.positionY );
			break;
		}
		
	}

}
