package com.tsubu.bubble;


/*
 * 	泡のインタフェイス　
 */
public interface Bubble
{
	public boolean action();
	public void draw();
	public boolean touch( float touchX , float touchY );
	
}
