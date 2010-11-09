package com.androidtsubu.ramentimer;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SyncResult;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends Activity {
	
	// 分表示部
	private TextView minTextView = null;
	// 秒表示部
	private TextView secTextView = null;
	// Intentに付与している呼び出し元を保持する
	private int requestCode = 0;
	// ラーメン情報
	private NoodleMaster noodleMaster = null;
	// 登録フラグ
//	private boolean registrationFlg = false;
	private boolean registrationFlg = true; //表示テスト用にTrue
	// 登録表示用のＩＤ
	private int confirmCreateId = 1000;
	
	// 秒の増減間隔
	private static final int SEC_INTERVALS = 10;
	// 分の上限値
	private static final int MIN_UPPEL_LIMIT = 9;
	// 分の下限値
	private static final int MIN_LOWER_LIMIT = 0;
	// タイマーの更新時間間隔(ms)
	private static final int TIMER_UPDATE_INTERVALS = 100;
	
	// 開始時刻を保持
	private int startTime = 0;
	// 待ち時間を保持
	private int waitTime = 0;
		
	
	private class RamenTimerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			Calendar calendar = Calendar.getInstance();
			int currentTime = calendar.get(Calendar.MINUTE)*60 + calendar.get(Calendar.SECOND);
			
			// 待ち時間を超えてない場合は、表示を更新し処理を終了する。
			if(waitTime > currentTime ){
				updateTimerDisplay(waitTime - currentTime);
				return;
			}
			// サービスを停止する
			ramenTimerService.stop();

			Toast toast = Toast.makeText(getApplicationContext(), "Time over!", Toast.LENGTH_LONG);
			toast.show();
	    	MediaPlayer mp = MediaPlayer.create(TimerActivity.this, R.raw.alarm);
			try {
				mp.start();
			} catch (Exception e) {
				// 例外は発生しない
			}
			// 0秒、終了ボタンを表示
			updateTimerDisplay(0);
			
			// 登録可否の表示
			if(registrationFlg){
				showConfirmCreationLayout();
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
		
		// 呼び出し元を保持する
		Intent requestIntent = getIntent();
		requestCode = requestIntent.getIntExtra(RequestCode.KEY_RESUEST_CODE, -1);
		
		// 呼び出し元のラーメン情報を取得する
		//noodleMaster = requestIntent;
		
		// 呼び出し元に応じて表示を切り替える
		displaySetting(requestCode);
		
		
		// 分+ボタン
		Button minUpButton = (Button)findViewById(R.id.MinUpButton);
		minUpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int min = Integer.valueOf(minTextView.getText().toString())+1;
				if(min > MIN_UPPEL_LIMIT) // 上限値を超える場合は処理しない
					return; 
				minTextView.setText(String.valueOf(min));
			}
		});
		
		// 分-ボタン
		Button minDownButton = (Button)findViewById(R.id.MinDownButton);
		minDownButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int min = Integer.valueOf(minTextView.getText().toString())-1;
				if(min < MIN_LOWER_LIMIT) // 下限値未満となる場合は処理しない
					return; 
				minTextView.setText(String.valueOf(min));
			}
		});
		
		// 秒+ボタン
		Button secUpButton = (Button)findViewById(R.id.SecUpButton);
		secUpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int sec = Integer.valueOf(secTextView.getText().toString())+SEC_INTERVALS;
				if(sec >= 60){ // 60秒以上になったら1分あげる
					int min = Integer.valueOf(minTextView.getText().toString())+1;
					if(min > MIN_UPPEL_LIMIT) // 上限値を超える場合は処理しない
						return; 
					minTextView.setText(String.valueOf(min));
				}
				secTextView.setText(getSecText(sec));
			}
		});
		
		// 秒-ボタン
		Button secDownButton = (Button)findViewById(R.id.SecDownButton);
		secDownButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				int sec = Integer.valueOf(secTextView.getText().toString())-SEC_INTERVALS;
				if(sec < 0){ // 0秒時にマイナスボタンを押下した場合は、1分さげる
					int min = Integer.valueOf(minTextView.getText().toString())-1;
					if(min < MIN_LOWER_LIMIT) // 下限値未満となる場合は処理しない
						return; 
					minTextView.setText(String.valueOf(min));
				}
				secTextView.setText(getSecText(sec));
			}
		});
		
		// 開始ボタン
		Button startButton = (Button)findViewById(R.id.TimerStartButton);
		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int min = Integer.valueOf(minTextView.getText().toString());
				int sec = Integer.valueOf(secTextView.getText().toString());
				
				Calendar calendar = Calendar.getInstance();
				startTime = calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.SECOND); 
				waitTime = min * 60 + sec + startTime;

				//ramenTimerService.schedule((min * 60 + sec ) * 1000 );
				ramenTimerService.schedule(TIMER_UPDATE_INTERVALS);
				
			}
		});

		// 終了ボタン
		Button endButton = (Button)findViewById(R.id.TimerEndButton);
		endButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		unbindService(serviceConnection); // バインド解除
		unregisterReceiver(receiver); // 登録解除
		ramenTimerService.stopSelf(); // サービスは必要ないので終了させる。
	}
	
	/**
	 * 引数が有効値でなければ有効値を戻す。
	 * 1桁の場合は前0を付加する。
	 */
	private String getSecText(int sec){
		if(sec >= 60){ //60秒以上の場合
			sec = sec - 60;
		}else if(sec < 0){ //0未満の場合
			sec = sec + 60;
		}
		String secText = String.valueOf(sec);
		if(sec < 10){ //一桁の場合は前0を表示
			secText = "0" + sec;
		}
		return secText;
	}

	/**
	 * ラーメン情報が登録されていない場合の確認メッセージを表示する
	 */
	private void showCreateConfirmDialog(){
		
		// 商品が登録されていない場合の処理
    	String dialogTitle = "この商品はまだ登録されていません。";
    	String dialogMessage = "商品の登録を行いますか？";
    	
    	new AlertDialog.Builder(TimerActivity.this)
    	.setTitle(dialogTitle)
    	.setMessage(dialogMessage)
		.setNegativeButton("いいえ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// 
			}
		})
    	.setPositiveButton("はい",new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// CreateActivityを呼び出す。
				Intent intent = new Intent(TimerActivity.this, CreateActivity.class)
				.putExtra(RequestCode.KEY_RESUEST_CODE, RequestCode.TIMER2CREATE.ordinal())
				.putExtra("", noodleMaster);
				startActivity(intent);
			}
		})
		.show();
	}
	
	/**
	 * ラーメン情報をレイアウトにセットする
	 */
	private void setNoodleData(){
		
	}
	
	/**
	 * リクエストコードで表示を切り替える
	 * @param id
	 */
	private void displaySetting(int id){

		// 上部を非表示にする
		LinearLayout blank = (LinearLayout)findViewById(R.id.BlankLinearLayout);
		blank.setVisibility(View.GONE);
		LinearLayout notExistNoodle = (LinearLayout)findViewById(R.id.NotExistsNoodleLinearLayout);
		notExistNoodle.setVisibility(View.GONE);
		LinearLayout existNoodle = (LinearLayout)findViewById(R.id.ExistsNoodleLinearLayout);
		existNoodle.setVisibility(View.GONE);
		LinearLayout confirmCreation = (LinearLayout)findViewById(R.id.ConfirmCreationLinearLayout);
		confirmCreation.setVisibility(View.GONE);
		
		if(id == RequestCode.DASHBORAD2TIMER.ordinal()){ //DashboardActivityから呼ばれた場合
			blank.setVisibility(View.VISIBLE);
			
		}else if(id == RequestCode.CREATE2TIMER.ordinal()){ //CreateActivityから呼ばれた場合
			existNoodle.setVisibility(View.VISIBLE);
			// ラーメン情報を表示し時間をセットする
			setNoodleData();
		}else if(id == RequestCode.HISTORY2TIMER.ordinal()){ //HistoryActivityから呼ばれた場合
			existNoodle.setVisibility(View.VISIBLE);
			// ラーメン情報を表示し時間をセットする
			setNoodleData();
		}else if(id == RequestCode.READER2TIMER.ordinal()){ //ReaderActivityから呼ばれた場合
			// ラーメン情報が存在する場合は、ラーメン情報を表示し時間をセットする
			if(noodleMaster != null){
				existNoodle.setVisibility(View.VISIBLE);
				setNoodleData();
			}else{ //タイマー終了後、登録可否を問うメッセージを表示するフラグをたてる
				notExistNoodle.setVisibility(View.VISIBLE);
				registrationFlg = true;
			}
		}else if(id == confirmCreateId){ //タイマー終了後に登録確認画面を表示する場合
			confirmCreation.setVisibility(View.VISIBLE);			
		}else{ //上記以外は、、、
			
		}
		
	}
	
	private void showConfirmCreationLayout(){
		// 上部を非表示にする
		LinearLayout blank = (LinearLayout)findViewById(R.id.BlankLinearLayout);
		blank.setVisibility(View.GONE);
		LinearLayout notExistNoodle = (LinearLayout)findViewById(R.id.NotExistsNoodleLinearLayout);
		notExistNoodle.setVisibility(View.GONE);
		LinearLayout existNoodle = (LinearLayout)findViewById(R.id.ExistsNoodleLinearLayout);
		existNoodle.setVisibility(View.GONE);
		LinearLayout confirmCreation = (LinearLayout)findViewById(R.id.ConfirmCreationLinearLayout);
		confirmCreation.setVisibility(View.GONE);

		confirmCreation.setVisibility(View.VISIBLE);
	}
	
	private void updateTimerDisplay(int time){
		minTextView.setText(String.valueOf(time / 60));
		secTextView.setText(getSecText(time % 60));
	}
}
