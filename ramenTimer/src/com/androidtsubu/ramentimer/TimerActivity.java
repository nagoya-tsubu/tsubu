package com.androidtsubu.ramentimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends Activity {
	
	private static final int REQUEST_CODE =100;
	TextView minTextView;
	TextView secTextView;
	
	private class RamenTimerReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast toast = Toast.makeText(getApplicationContext(), "Time over!", Toast.LENGTH_LONG);
			toast.show();
	    	MediaPlayer mp = MediaPlayer.create(TimerActivity.this, R.raw.alarm);
			try {
				mp.start();
			} catch (Exception e) {
				// 例外は発生しない
			}
		}
	}
	
	private RamenTimerService ramenTimerService;
	private final RamenTimerReceiver receiver = new RamenTimerReceiver();
	
	private ServiceConnection serviceConnection = new ServiceConnection() {
		
		public void onServiceConnected(ComponentName className, IBinder service) {
			ramenTimerService = ((RamenTimerService.RamenTimerBinder)service).getService();
		}
		
		public void onServiceDisconnected(ComponentName className) {
			ramenTimerService = null;
		}
		
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        minTextView = (TextView)findViewById(R.id.MinTextView);
		secTextView = (TextView)findViewById(R.id.SecTextView);
		
		Button minUpButton = (Button)findViewById(R.id.MinUpButton);
		minUpButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				int min = Integer.valueOf(minTextView.getText().toString())+1;
				if(min >= 10) return; // 10分以上は不要と判断
				minTextView.setText(String.valueOf(min));
			}
		});
		
		Button minDownButton = (Button)findViewById(R.id.MinDownButton);
		minDownButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				int min = Integer.valueOf(minTextView.getText().toString())-1;
				if(min <= 0) return; // 1分未満は不要と判断
				minTextView.setText(String.valueOf(min));
				
			}
		});

		Button secUpButton = (Button)findViewById(R.id.SecUpButton);
		secUpButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				int sec = Integer.valueOf(secTextView.getText().toString())+10;
				if(sec >= 51){ // 60秒になったら1分あげて、00秒とする
					int min = Integer.valueOf(minTextView.getText().toString())+1;
					if(min >= 10) return; // 10分以上となる場合は処理しない
					minTextView.setText(String.valueOf(min));
					secTextView.setText("00");
					return;
				}
				secTextView.setText(String.valueOf(sec));
				
			}
		});

		Button secDownButton = (Button)findViewById(R.id.SecDownButton);
		secDownButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				int sec = Integer.valueOf(secTextView.getText().toString())-10;
				if(sec == 0){ // 0秒になったら、00秒と表示する
					secTextView.setText("00");
					return;
				}else if(sec <= 0){ // 0秒時にマイナスボタンを押下した場合は、1分さげて50秒とする
					int min = Integer.valueOf(minTextView.getText().toString())-1;
					if(min <= 0) return; // １分未満となる場合は処理しない
					minTextView.setText(String.valueOf(min));
					secTextView.setText("50");
					return;
				}
				secTextView.setText(String.valueOf(sec));
				
			}
		});
		
		Button startButton = (Button)findViewById(R.id.StartButton);
		startButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				long min = Integer.valueOf(minTextView.getText().toString());
				long sec = Integer.valueOf(secTextView.getText().toString());
				ramenTimerService.schedule((min * 60 + sec ) * 1000 );
				moveTaskToBack(true);
			}
		});
		
		// サービスを開始
		Intent intent = new Intent(this,RamenTimerService.class);
		startService(intent);
		IntentFilter filter = new IntentFilter(RamenTimerService.ACTION);
		registerReceiver(receiver, filter);
		
		// サービスにバインド
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

		// いったんアンバインドしてから再度バインド
		unbindService(serviceConnection);
		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
	}
	
	
	/*
	 * Intentの戻り値を受け取る
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE) {
            TextView textView = (TextView)findViewById(R.id.RamenInfoTextView);
            if (resultCode == RESULT_OK) {
                final String barcode = data.getStringExtra("SCAN_RESULT");
                textView.setText(barcode);
            }
        }
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection); // バインド解除
		unregisterReceiver(receiver); // 登録解除
		ramenTimerService.stopSelf(); // サービスは必要ないので終了させる。
	}
	
	private NoodleMaster getRamen(){

		String janCode = "";

		NoodleMaster noodleMaster = null;
		try{
			NoodleManager noodleManager = new NoodleManager();
			noodleMaster = noodleManager.getNoodleMaster(janCode);
		}catch (NumberFormatException e) {
			new AlertDialog.Builder(TimerActivity.this).setTitle(
			"JANコードを入力してください").setMessage(
			"数字を入力してください。").setPositiveButton(
			"OK", null).show();
			return null;
		}
		
		if(noodleMaster==null){
			// Janコードが存在しない場合の処理
	    	String dialogTitle = "未登録の商品です。";
	    	String dialogMessage = "この商品を登録しますか？";
	    	
	    	new AlertDialog.Builder(TimerActivity.this)
	    	.setTitle(dialogTitle)
	    	.setMessage(dialogMessage) // 
	    	.setPositiveButton("はい",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// 登録機能を呼び出す。Janコード情報を付加する。
					startActivity(new Intent(TimerActivity.this, CreateActivity.class));
				}
			})
			.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					return;
				}
			})
			.show();
		}
		return null;
	}
}