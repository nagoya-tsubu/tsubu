package com.androidtsubu.ramentimer;

import java.sql.SQLException;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageButton;
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
	// ラーメン画像
	private ImageView noodleImage = null;
	// Janコード
	private TextView janCode = null;
	// 商品名
	private TextView name = null;
	// 待ち時間
	private TextView timerLimit = null;
	// 切り替え
	private ViewStub timerInfoViewStub = null;

	private View timerInfoInView = null;
	//
	private LinearLayout timerInfoFrame = null;

	// Intentに付与している呼び出し元を保持する
	private int requestCode = 0;
	// ラーメン情報
	private NoodleMaster noodleMaster = null;
	// 履歴
	private NoodleHistory noodleHistory = null;
	// 登録フラグ
	private boolean registrationFlg = false;

	// 商品情報(NoodleMaster)のキー
	public static final String KEY_NOODLE_MASTER = "NOODLE_MASTER";
	//履歴のキー
	public static final String KEY_NOODLE_HISTORY = "NOODLE_HISTORY";
	// 秒の増減間隔
	private static final int SEC_INTERVALS = 10;
	// 分の上限値
	private static final int MIN_UPPEL_LIMIT = 9;
	// 分の下限値
	private static final int MIN_LOWER_LIMIT = 0;
	// タイマーの更新時間間隔(ms)
	private static final int TIMER_UPDATE_INTERVALS = 200;

	private RamenTimerService ramenTimerService;
	private final RamenTimerReceiver receiver = new RamenTimerReceiver();

	// 開始時刻を保持
	private long startTime = 0;
	// 待ち時間を保持
	private long waitTime = 0;
	/** 茹で時間（履歴で使用する）@hideponm */
	private int boilTime;
	/**カウントダウンフラグ*/
	private boolean countdown = false;
	/**音*/
	private MediaPlayer mediaPlayer = null;
	/**振動*/
	private Vibrator vibrator = null;
	/**振動パターン*/
	private static long[] vibePattern = {500,500,500,500,500,500,500,500};

	private Context getThis() {
		return this;
	}

	private class RamenTimerReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			long currentTime = System.currentTimeMillis();

			// 待ち時間を超えてない場合は、表示を更新し処理を終了する。
			if (waitTime > currentTime) {
				updateTimerTextView((waitTime - currentTime) / 1000 + 1);
				return;
			}
			// サービスを停止する
			ramenTimerService.stop();
			// 0秒TextView、終了ボタンを表示
			updateTimerTextView(0);
			setTimerEndLayout();
//			Toast toast = Toast.makeText(getApplicationContext(), "Time over!",
//					Toast.LENGTH_LONG);
//			toast.show();

			//振動させる
			new Thread(new Runnable() {
				public void run() {
					vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
					vibrator.vibrate(vibePattern, -1);
				}
			}).start();
			// Mainスレッドでアラーム再生すると遅延が発生するため、スレッドで実行する
			new Thread(new Runnable() {
				public void run() {
					mediaPlayer = MediaPlayer.create(TimerActivity.this,
							R.raw.alarm);
					try {
						mediaPlayer.start();
					} catch (Exception e) {
						// 例外は発生しない
					}
				}
			}).start();
			

			// GAEに情報が存在した場合、履歴を登録する
			if (noodleMaster != null && noodleMaster.isCompleteData()) {
				new Thread(new Runnable() {
					public void run() {
						NoodleManager noodleManager = new NoodleManager(
								TimerActivity.this);
						try {
							// 履歴を作成する（茹で時間も入れる）@hideponm
							noodleManager.createNoodleHistory(noodleMaster,
									boilTime, new Date());
						} catch (SQLException e) {
							Toast.makeText(getThis(),
									ExceptionToStringConverter.convert(e),
									Toast.LENGTH_LONG).show();
						}
					}
				}).start();
			}
			setCountdown(false);		
		}
	}

	private ServiceConnection serviceConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			ramenTimerService = ((RamenTimerService.RamenTimerBinder) service)
					.getService();
		}

		public void onServiceDisconnected(ComponentName className) {
			ramenTimerService = null;
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//ハードウェアキーによる音量設定を有効にする
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		setContentView(R.layout.activity_timer);

		// レイアウトを取得
		minTextView = (TextView) findViewById(R.id.MinTextView);
		secTextView = (TextView) findViewById(R.id.SecTextView);

		minUpButton = (Button) findViewById(R.id.MinUpButton);
		minDownButton = (Button) findViewById(R.id.MinDownButton);
		secUpButton = (Button) findViewById(R.id.SecUpButton);
		secDownButton = (Button) findViewById(R.id.SecDownButton);

		timerInfoViewStub = (ViewStub) findViewById(R.id.NoodleInfoViewStub);

		timerInfoFrame = (LinearLayout) findViewById(R.id.TimerInfoFrame);

		timerImage = (ImageView) findViewById(R.id.TimerImageView);

		// 呼び出し元を保持する
		Intent requestIntent = getIntent();
		requestCode = requestIntent.getIntExtra(RequestCode.KEY_RESUEST_CODE,
				-1);
		// 呼び出し元のラーメン情報を取得する
		noodleMaster = (NoodleMaster) requestIntent
				.getParcelableExtra(KEY_NOODLE_MASTER);
		// 呼び出し元のラーメン履歴を取得する
		noodleHistory = (NoodleHistory) requestIntent
				.getParcelableExtra(KEY_NOODLE_HISTORY);

		// 呼び出し元に応じて表示を切り替える
		displaySetting(requestCode);

		// 分+ボタン
		minUpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addTimerCount(60);
			}
		});
		// 分-ボタン
		minDownButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addTimerCount(-60);
			}
		});
		// 秒+ボタン
		secUpButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addTimerCount(SEC_INTERVALS);
			}
		});
		// 秒-ボタン
		secDownButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addTimerCount(SEC_INTERVALS * -1);
			}
		});
		// 開始ボタン
		startButton = (Button) findViewById(R.id.TimerStartButton);
		startButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				startTimer();
				// 未登録の商品であれば、登録確認レイアウトを表示する
				if (registrationFlg) {
					showConfirmCreation();
				}

			}
		});
		// 終了ボタン
		endButton = (Button) findViewById(R.id.TimerEndButton);
		endButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//音がなってたら消す
				if(mediaPlayer != null &&  mediaPlayer.isPlaying()){
					mediaPlayer.stop();
				}
				//振動してたら消す
				if(vibrator != null){
					vibrator.cancel();					
				}
				setResult(RESULT_OK);
				finish();
			}
		});

		// 開始前の画面表示
		setTimerNotRunningLayout();

		// サービスを開始
		Intent intent = new Intent(this, RamenTimerService.class);
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
		ramenTimerService = null;
	}

	/**
	 * ラーメン情報をレイアウトにセット、表示する
	 */
	private void setNoodleData() {
		if (noodleMaster == null)
			return;

		// 設定されている項目を表示する
		if (noodleImage != null && noodleMaster.getImage() != null) {
			noodleImage.setImageBitmap(noodleMaster.getImage());
			noodleImage.setVisibility(View.VISIBLE);
		}
		if (janCode != null && !noodleMaster.getJanCode().equals("")) {
			janCode.setText(noodleMaster.getJanCode());
			LinearLayout janCodeTableRow = (LinearLayout) findViewById(R.id.JanCodeTableRow);
			janCodeTableRow.setVisibility(View.VISIBLE);
		}
		if (name != null && !noodleMaster.getName().equals("")) {
			name.setText(noodleMaster.getName());
			name.setVisibility(View.VISIBLE);
		}
		if(noodleHistory != null && noodleHistory.getBoilTime() != 0){
			timerLimit.setText("" + noodleHistory.getNoodleMaster().getTimerLimitString());
			// タイマーの時間をセットする
			//履歴がある場合は履歴のゆで時間を利用する @hideponm
			updateTimerTextView(Long.valueOf(noodleHistory.getBoilTime()));
			
		}else if (noodleMaster != null && noodleMaster.getTimerLimit() != 0) {
			timerLimit.setText("" + noodleMaster.getTimerLimitString());
			// タイマーの時間をセットする
			updateTimerTextView(Long.valueOf(noodleMaster.getTimerLimit()));
		}
	}

	/**
	 * リクエストコードで表示を切り替える
	 * 
	 * @param id
	 */
	private void displaySetting(int id) {
		if(id == -1){
			return;
		}
		switch (RequestCode.values()[id]) {
		case DASHBORAD2TIMER:
			timerInfoViewStub
					.setLayoutResource(R.layout.activity_timer_only_jancode);
			timerInfoInView = timerInfoViewStub.inflate();
			timerInfoFrame.setVisibility(View.INVISIBLE);
			break;
		case CREATE2TIMER:
		case HISTORY2TIMER:
		case FAVORITE2TIMER:
		case READER2TIMER:
			if (noodleMaster.isCompleteData()) {
				timerInfoViewStub
						.setLayoutResource(R.layout.activity_timer_full_information);
				timerInfoInView = timerInfoViewStub.inflate();

				noodleImage = (ImageView) timerInfoInView
						.findViewById(R.id.NoodleImageView);
				janCode = (TextView) timerInfoInView
						.findViewById(R.id.JanCodeTextView);
				name = (TextView) timerInfoInView
						.findViewById(R.id.NameTextView);
				timerLimit = (TextView) timerInfoInView
						.findViewById(R.id.TimerLimitTextView);
			} else {
				// 登録フラグをたてる
				registrationFlg = true;
				// ActionBarのバーコードを登録と置き換える
				timerInfoViewStub
						.setLayoutResource(R.layout.activity_timer_only_jancode);
				timerInfoInView = timerInfoViewStub.inflate();

				janCode = (TextView) timerInfoInView
						.findViewById(R.id.JanCodeTextView);

				// はいボタン
				Button yesButton = (Button) timerInfoInView
						.findViewById(R.id.ConfirmCreationYesButton);
				yesButton.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						onCreateButtonClick(v);
					}
				});
			}
			break;
		}
		setNoodleData();
	}

	/**
	 * 登録確認メッセージの表示
	 */
	private void showConfirmCreation() {
		LinearLayout confirmCreation = (LinearLayout) findViewById(R.id.ConfirmCreationLinearLayout);
		confirmCreation.setVisibility(View.VISIBLE);
	}

	/**
	 * タイマーに時間を足す
	 * 
	 * @param sec
	 */
	private void addTimerCount(int sec) {
		int setTime = (Integer.valueOf(minTextView.getText().toString()) * 60)
				+ Integer.valueOf(secTextView.getText().toString());
		setTime = setTime + sec;
		// 上限値または下限値を超える場合は処理しない
		if (setTime > MIN_UPPEL_LIMIT * 60 || setTime < MIN_LOWER_LIMIT) {
			return;
		}
		updateTimerTextView(setTime);
	}

	/**
	 * タイマーの残り時間を更新する
	 * 
	 * @param time
	 */
	private void updateTimerTextView(long sec) {
		minTextView.setText(String.valueOf(sec / 60));
		secTextView.setText(getSecText(sec % 60));
	}

	/**
	 * 引数が有効値でなければ有効値を戻す。 1桁の場合は前0を付加する。
	 */
	private String getSecText(long sec) {
		if (sec >= 60) {
			sec = sec - 60;
		} else if (sec < 0) {
			sec = sec + 60;
		}
		String secText = String.valueOf(sec);
		if (sec < 10) { // 一桁の場合は前0を表示
			secText = "0" + sec;
		}
		return secText;
	}

	/**
	 * 終了時間をセットし、サービスのタイマーを起動する
	 */
	private void startTimer() {
		setCountdown(true);
		int min = Integer.valueOf(minTextView.getText().toString());
		int sec = Integer.valueOf(secTextView.getText().toString());

		PendingIntent pi = PendingIntent.getActivity(this, 0, new Intent(this,
				TimerActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
		boilTime = min * 60 + sec;
		Date date = new Date();
		date.setSeconds(date.getSeconds() + boilTime);
		AlarmManager amng = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		amng.set(AlarmManager.RTC_WAKEUP, date.getTime(), pi);

		// 終了時刻を設定する
		startTime = System.currentTimeMillis();
		waitTime = startTime + (boilTime * 1000);
		ramenTimerService.schedule(TIMER_UPDATE_INTERVALS, waitTime);
		// カウント中のレイアウトを表示する
		setTimerRunningLayout();
		// 時間調整ボタンを非表示にする
		hidePickerButton();
	}

	/**
	 * 非カウント中のレイアウトを設定する
	 */
	private void setTimerNotRunningLayout() {
		startButton.setVisibility(View.VISIBLE);
		endButton.setVisibility(View.GONE);
		// ボタンを有効化（押せるようになる）
		setOnClickEnable(true);
	}

	/**
	 * カウント中のレイアウトを設定する
	 */
	private void setTimerRunningLayout() {
		startButton.setVisibility(View.GONE);
		// タイマー画像を差し替える(黄色)
		timerImage.setImageResource(R.drawable.img_alarm_start);
		endButton.setVisibility(View.GONE);
		// ボタンを無効化（押せないようになる）
		setOnClickEnable(false);

	}

	/**
	 * タイマー終了後のレイアウトを設定する
	 */
	private void setTimerEndLayout() {
		endButton.setVisibility(View.VISIBLE);
		// タイマー画像を差し替える(赤)
		timerImage.setImageResource(R.drawable.img_alarm_end);
		Animation timerAnim = AnimationUtils.loadAnimation(this, R.anim.timer_icon_action_start);
		timerAnim.setAnimationListener(mAnimLeft2Right);
		timerImage.startAnimation(timerAnim);
		
		startButton.setVisibility(View.GONE);
		// ボタンを有効化（押せるようになる）
		setOnClickEnable(true);

	}
	
	/**
	 * ボタンを無効化/有効化　するメソッド
	 * @param enabled
	 */
	private void setOnClickEnable(boolean enabled){
		ImageButton homeButton= (ImageButton) findViewById(R.id.TitleHomeButton);
		ImageButton readerButton= (ImageButton) findViewById(R.id.TitleReaderButton);
		ImageButton historyButton= (ImageButton) findViewById(R.id.TitleHistoryButton);
		homeButton.setEnabled(enabled);
		readerButton.setEnabled(enabled);
		historyButton.setEnabled(enabled);
	}


	AnimationListener mAnimLeft2Right = new AnimationListener() {
		public void onAnimationStart(Animation animation) {
		}
		public void onAnimationRepeat(Animation animation) {
		}
		public void onAnimationEnd(Animation animation) {
			Animation timerAnim = AnimationUtils.loadAnimation(TimerActivity.this, R.anim.timer_icon_action);
			timerAnim.setAnimationListener(mAnimRight2Center);
			timerImage.startAnimation(timerAnim);
		}
	};
	
	AnimationListener mAnimRight2Center = new AnimationListener() {
		public void onAnimationStart(Animation animation) {
		}		
		public void onAnimationRepeat(Animation animation) {
		}
		public void onAnimationEnd(Animation animation) {
			Animation timerAnim = AnimationUtils.loadAnimation(TimerActivity.this, R.anim.timer_icon_action_end);
			timerImage.startAnimation(timerAnim);
		}
	};
	
	/**
	 * 時間調整ボタンを非表示にする
	 */
	private void hidePickerButton() {
		minUpButton.setVisibility(View.INVISIBLE);
		minDownButton.setVisibility(View.INVISIBLE);
		secUpButton.setVisibility(View.INVISIBLE);
		secDownButton.setVisibility(View.INVISIBLE);
	}

	/**
	 * 登録機能を起動し、Timerを終了する
	 * 
	 */
	public void onCreateButtonClick(View v) {
		if(countdown){
			//カウントダウン中は操作できなようにする
			return;
		}
		Intent intent = new Intent(TimerActivity.this, CreateActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.READER2CREATE.ordinal());
		intent.putExtra(KEY_NOODLE_MASTER, noodleMaster);
		startActivity(intent);
		finish();
	}

	public void onLogoClick(View v) {
		if(countdown){
			//カウントダウン中は操作できなようにする
			return;
		}		
		setResult(RESULT_OK);
		finish();
	}

	/**
	 * バーコードリーダーを起動し、Timerを終了する
	 */
	public void onReaderButtonClick(View v) {
		if(countdown){
			//カウントダウン中は操作できなようにする
			return;
		}		
		Intent intent = new Intent();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.ACTION_READER.ordinal());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 履歴を起動し、Timerを終了する
	 */
	public void onHistoryButtonClick(View v) {
		if(countdown){
			//カウントダウン中は操作できなようにする
			return;
		}		
		Intent intent = new Intent();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.ACTION_HISTORY.ordinal());
		setResult(RESULT_OK, intent);
		finish();
	}
	
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (!countdown) {
			return super.dispatchKeyEvent(event);
		}

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// カウントダウン中は戻るキーを無効にする
			switch (event.getKeyCode()) {
			case KeyEvent.KEYCODE_BACK:
				return true;
			}
		}
		return super.dispatchKeyEvent(event);
	}
	
	/**
	 * カウントダウン中フラグを設定する
	 * @param countdown
	 */
	private void setCountdown(boolean countdown){
		this.countdown = countdown;
		if(countdown){
			//スリープを無効化する
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}else{
			//スリープを有効化する
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}
	}

}
