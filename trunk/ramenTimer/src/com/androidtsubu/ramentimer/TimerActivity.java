package com.androidtsubu.ramentimer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TimerActivity extends Activity {
	
	// 分表示部
	private TextView minTextView = null;
	// 秒表示部
	private TextView secTextView = null;
	// 開始ボタン
	private Button startButton = null;
	// 終了ボタン
	private Button endButton = null;
	// 分+ボタン
	private Button minUpButton = null;
	// 分-ボタン
	private Button minDownButton = null;
	// 秒+ボタン
	private Button secUpButton = null;
	// 秒-ボタン
	private Button secDownButton = null;
	// タイマーイメージ
	private ImageView timerImage = null;
	// 情報を表示しないレイアウト
	private LinearLayout blank = null;
	// 未登録レイアウト
	private LinearLayout notExistNoodle = null;
	// 登録済みレイアウト
	private LinearLayout existNoodle = null;
	// 登録確認レイアウト
	private LinearLayout confirmCreation = null;
	
	// Intentに付与している呼び出し元を保持する
	private int requestCode = 0;
	// ラーメン情報
	private NoodleMaster noodleMaster = null;
	// 登録フラグ
//	private boolean registrationFlg = false;
	private boolean registrationFlg = true; //表示テスト用にTrue
	
	// 商品情報(NoodleMaster)のキー
	private static final String KEY_NOODLE_MASTER = "NOODLE_MASTER";
	// 秒の増減間隔
	private static final int SEC_INTERVALS = 10;
	// 分の上限値
	private static final int MIN_UPPEL_LIMIT = 9;
	// 分の下限値
	private static final int MIN_LOWER_LIMIT = 0;
	// タイマーの更新時間間隔(ms)
	private static final int TIMER_UPDATE_INTERVALS = 200;
	
	// 開始時刻を保持
	private long startTime = 0;
	// 待ち時間を保持
	private long waitTime = 0;
		
	
	private class RamenTimerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			long currentTime = System.currentTimeMillis();
			
			// 待ち時間を超えてない場合は、表示を更新し処理を終了する。
			if(waitTime > currentTime ){
				updateTimerTextView((waitTime - currentTime) / 1000 + 1);
				return;
			}
			// サービスを停止する
			ramenTimerService.stop();

			// 0秒TextView、終了ボタンを表示
			updateTimerTextView(0);

			Toast toast = Toast.makeText(getApplicationContext(), "Time over!", Toast.LENGTH_LONG);
			toast.show();
	    	MediaPlayer mp = MediaPlayer.create(TimerActivity.this, R.raw.alarm);
			try {
				mp.start();
			} catch (Exception e) {
				// 例外は発生しない
			}
			// 未登録の商品であれば、登録するか問う
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
		
		// レイアウトを取得
		minTextView = (TextView)findViewById(R.id.MinTextView);
		secTextView = (TextView)findViewById(R.id.SecTextView);
		
		minUpButton = (Button)findViewById(R.id.MinUpButton);
		minDownButton = (Button)findViewById(R.id.MinDownButton);
		secUpButton = (Button)findViewById(R.id.SecUpButton);
		secDownButton = (Button)findViewById(R.id.SecDownButton);
		
		blank = (LinearLayout)findViewById(R.id.BlankLinearLayout);
		notExistNoodle = (LinearLayout)findViewById(R.id.NotExistsNoodleLinearLayout);
		existNoodle = (LinearLayout)findViewById(R.id.ExistsNoodleLinearLayout);
		confirmCreation = (LinearLayout)findViewById(R.id.ConfirmCreationLinearLayout);

		timerImage = (ImageView)findViewById(R.id.TimerImageView);
		
		// 呼び出し元を保持する
		Intent requestIntent = getIntent();
		requestCode = requestIntent.getIntExtra(RequestCode.KEY_RESUEST_CODE, -1);
		// 呼び出し元のラーメン情報を取得する
		noodleMaster =(NoodleMaster)requestIntent.getParcelableExtra(KEY_NOODLE_MASTER);
		// 呼び出し元に応じて表示を切り替える
		displaySetting(requestCode);
		
		// 分+ボタン
		minUpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int min = Integer.valueOf(minTextView.getText().toString())+1;
				if(min > MIN_UPPEL_LIMIT) // 上限値を超える場合は処理しない
					return; 
				minTextView.setText(String.valueOf(min));
			}
		});
		
		// 分-ボタン
		minDownButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				int min = Integer.valueOf(minTextView.getText().toString())-1;
				if(min < MIN_LOWER_LIMIT) // 下限値未満となる場合は処理しない
					return; 
				minTextView.setText(String.valueOf(min));
			}
		});
		
		// 秒+ボタン
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
		startButton = (Button)findViewById(R.id.TimerStartButton);
		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startTimer();
			}
		});

		// 終了ボタン
		endButton = (Button)findViewById(R.id.TimerEndButton);
		endButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		// はいボタン
		Button yesButton = (Button)findViewById(R.id.ConfirmCreationYesButton);
		yesButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// CreateActivityへ移動
			}
		});
		
		// いいえボタン
		Button noButton = (Button)findViewById(R.id.ConfirmCreationNoButton);
		noButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		// 開始前の画面表示
		showStartButton();
		
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
	private String getSecText(long sec){
		if(sec >= 60){
			sec = sec - 60;
		}else if(sec < 0){
			sec = sec + 60;
		}
		String secText = String.valueOf(sec);
		if(sec < 10){ //一桁の場合は前0を表示
			secText = "0" + sec;
		}
		return secText;
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

		hideNoodleInformation();
		if(id == RequestCode.DASHBORAD2TIMER.ordinal()){ //DashboardActivityから呼ばれた場合
			blank.setVisibility(View.VISIBLE);
			
		}else if(id == RequestCode.CREATE2TIMER.ordinal()){ //CreateActivityから呼ばれた場合
			existNoodle.setVisibility(View.VISIBLE);
			// ラーメン情報、時間をセットする
			setNoodleData();
		}else if(id == RequestCode.HISTORY2TIMER.ordinal()){ //HistoryActivityから呼ばれた場合
			existNoodle.setVisibility(View.VISIBLE);
			// ラーメン情報、時間をセットする
			setNoodleData();
		}else if(id == RequestCode.READER2TIMER.ordinal()){ //ReaderActivityから呼ばれた場合
			// ラーメン情報が存在する場合は、ラーメン情報、時間をセットする
			if(noodleMaster != null){
				existNoodle.setVisibility(View.VISIBLE);
				setNoodleData();
			}else{ //ラーメン情報が存在しなければ、登録フラグをたてる
				notExistNoodle.setVisibility(View.VISIBLE);
				registrationFlg = true;
			}
		}else{ //上記以外は、、、
			
		}
	}

	/**
	 * 登録確認レイアウトの表示
	 */
	private void showConfirmCreationLayout(){
		hideNoodleInformation();
		confirmCreation.setVisibility(View.VISIBLE);
	}
	
	/**
	 * タイマーの残り時間を更新する
	 * @param time
	 */
	private void updateTimerTextView(long sec){
		
		minTextView.setText(String.valueOf(sec / 60));
		secTextView.setText(getSecText(sec % 60));
	}
	
	/**
	 * 終了時間をセットし、サービスのタイマーを起動する
	 */
	private void startTimer(){
		int min = Integer.valueOf(minTextView.getText().toString());
		int sec = Integer.valueOf(secTextView.getText().toString());
		
		// 終了時刻を設定する
		startTime = System.currentTimeMillis(); 
		waitTime = startTime + ((min * 60 + sec) * 1000);
		ramenTimerService.schedule(TIMER_UPDATE_INTERVALS);
		// 終了ボタンを表示する
		showEndButton();
		// 時間調整ボタンを非表示にする
		hidePickerButton();
	}
	
	/**
	 * 開始ボタン等を表示する
	 */
	private void showStartButton(){
		startButton.setVisibility(View.VISIBLE);
		timerImage.setVisibility(View.GONE);
		endButton.setVisibility(View.GONE);
	}
	
	/**
	 * 終了ボタン等を表示する
	 */
	private void showEndButton(){
		endButton.setVisibility(View.VISIBLE);
		timerImage.setVisibility(View.VISIBLE);
		startButton.setVisibility(View.GONE);
	}
	
	/**
	 * ラーメン情報のLinearLayoutを非表示にする
	 */
	private void hideNoodleInformation(){
		blank.setVisibility(View.GONE);
		notExistNoodle.setVisibility(View.GONE);
		existNoodle.setVisibility(View.GONE);
		confirmCreation.setVisibility(View.GONE);
	}
	
	/**
	 * 時間調整ボタンを非表示にする
	 */
	private void hidePickerButton(){
		minUpButton.setVisibility(View.GONE);
		minDownButton.setVisibility(View.GONE);
		secUpButton.setVisibility(View.GONE);
		secDownButton.setVisibility(View.GONE);
	}
}