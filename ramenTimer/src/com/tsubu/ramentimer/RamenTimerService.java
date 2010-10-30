package com.tsubu.ramentimer;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class RamenTimerService extends Service {
	
	class RamenTimerBinder extends Binder {
		
		RamenTimerService getService() {
			return RamenTimerService.this;
		}
		
	}
	
	public static final String ACTION = "Ramen Timer Service";
	private Timer timer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		Toast toast = Toast.makeText(getApplicationContext(), "onCreate()", Toast.LENGTH_SHORT);
		toast.show();
System.out.println("####### service onCreate() process:"+ android.os.Process.myPid() + " task:" + android.os.Process.myTid());
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		Toast toast = Toast.makeText(getApplicationContext(), "onStart()", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast toast = Toast.makeText(getApplicationContext(), "onDestroy()", Toast.LENGTH_SHORT);
		toast.show();
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Toast toast = Toast.makeText(getApplicationContext(), "onBind()", Toast.LENGTH_SHORT);
		toast.show();
		return new RamenTimerBinder();
	}
	
	@Override
	public void onRebind(Intent intent) {
		Toast toast = Toast.makeText(getApplicationContext(), "onRebind()", Toast.LENGTH_SHORT);
		toast.show();
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Toast toast = Toast.makeText(getApplicationContext(), "onUnbind()", Toast.LENGTH_SHORT);
		toast.show();
		return true; // 再度クライアントから接続された際に onRebind を呼び出させる場合は true を返す
	}
	
	// クライアントから呼び出されるメソッド
	public void schedule(long delay) {
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		TimerTask timerTask = new TimerTask() {
			
			public void run() {
				sendBroadcast(new Intent(ACTION));
			}
			
		};
		timer.schedule(timerTask, delay);
	}
	
}