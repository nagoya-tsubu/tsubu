package com.tsubu.bubble;

import android.view.KeyEvent;
import twExpress.TweActivityInterface;

public class ChildLock
{
	private boolean isDownMenu;
	private int pushCountBack;
	private TweActivityInterface activityInterface;
	
	// メニューキーを押しながら 何回バックキーを押せばいいか。
	private static final int BACK_BUTTON_COUNT = 3;
	
	public ChildLock( TweActivityInterface activityInterface )
	{
		this.isDownMenu = false;
		this.pushCountBack = 0;
		this.activityInterface = activityInterface;
	}
	
	public boolean checkShutdownControl()
	{
		
		if( this.activityInterface.getKeyPush( KeyEvent.KEYCODE_MENU) )
		{
			this.isDownMenu = true;
			this.pushCountBack = 0;
			this.activityInterface.getLogManager().logDebug("aaa", "ON");
		}
		
		if( this.activityInterface.getKeyUp( KeyEvent.KEYCODE_MENU) )
		{
			this.isDownMenu = false;
			this.pushCountBack = 0;
			this.activityInterface.getLogManager().logDebug("aaa", "OFF");
		}
		
		if( this.isDownMenu )
		{
			
			if( this.activityInterface.getKeyPush( KeyEvent.KEYCODE_BACK ))
				this.pushCountBack ++;
			
			if( this.pushCountBack >= BACK_BUTTON_COUNT )
				return true;
			
			this.activityInterface.getLogManager().logDebug("aaa", "%d",this.pushCountBack);
			
		}

		return false;
	}
}
