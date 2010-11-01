package com.tsubu.ramentimer;

import com.tsubu.ramentimer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
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
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

public class TimerActivity extends Activity {
	
	private static final int REQUEST_CODE =100;
	
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

		final TimePicker timePicker = (TimePicker)findViewById(R.id.TimePicker01);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(0);
		timePicker.setCurrentMinute(1);
		
		Button button = (Button)findViewById(R.id.Button01);
		button.setOnClickListener(new View.OnClickListener() {
		
			public void onClick(View view) {
				long hour = timePicker.getCurrentHour();
				long min = timePicker.getCurrentMinute();
				
				ramenTimerService.schedule((hour * 60 + min) * 60 * 1000);
				moveTaskToBack(true);
			}
			
		});
		
		// IntentでJanコードを読むアプリを呼び出す
		Button janReadButton = (Button)findViewById(R.id.JanCodeReadButton);
		janReadButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				Intent intent = new Intent("com.google.zxing.client.android.SCAN");
				intent.putExtra("SCAN_MODE", "ONE_D_MODE");
				try {
					startActivityForResult(intent, REQUEST_CODE);
				} catch (ActivityNotFoundException e) {
					new AlertDialog.Builder(TimerActivity.this).setTitle(
							"アプリケーションが存在しません。").setMessage(
							"アプリケーションをインストールしてください。").setPositiveButton(
							"OK", null).show();
				}
			}
		});
		
		// EditTextのJanコードを検索する
		Button searchButton = (Button)findViewById(R.id.JanCodeSearchButton);
		searchButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {

				EditText janEditText = (EditText)findViewById(R.id.JanCodeEditText);
				//String janCode = janEditText.getText().toString();
				int janCode = 0;
				try{
					janCode = Integer.valueOf(janEditText.getText().toString());
				}catch (NumberFormatException e) {
					new AlertDialog.Builder(TimerActivity.this).setTitle(
					"JANコードを入力してください").setMessage(
					"数字を入力してください。").setPositiveButton(
					"OK", null).show();
					return;
				}
				NoodleManager noodleManager = new NoodleManager();
				NoodleMaster noodleMaster = noodleManager.getNoodleMaster(janCode);
				
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
            EditText textView = (EditText)findViewById(R.id.JanCodeEditText);
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
}