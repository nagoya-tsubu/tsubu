package com.androidtsubu.ramentimer;

import com.androidtsubu.ramentimer.bugreport.AppUncaughtExceptionHandler;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;


public class DashBoardActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //キャッチできない例外エラーが発生した場合に備え、例外ハンドラを設定する
        Resources res = getResources();
        Thread.setDefaultUncaughtExceptionHandler(
        		new AppUncaughtExceptionHandler(this,
	        		res.getString(R.string.bugreport_title),
	        		res.getString(R.string.bugreport_message),
	        		res.getString(R.string.dialog_yes),
	        		res.getString(R.string.dialog_no)
	        	)
        );
	}
	/**
	 * タイマーのボタンが押されたとき
	 * @param view
	 */
	public void onTimerButtonClick(View view) {
		gotoTimerActivity();
	}
	/**
	 * 登録ボタンが押されたとき
	 * @param view
	 */
	public void onCreateButtonClick(View view) {
//		int requestCode = RequestCode.DASHBOARD2CREATE.ordinal();
//		gotoReaderActivity(requestCode);		
		gotoMyListActivity();
	}
	/**
	 * 履歴ボタンが押されたとき
	 * @param view
	 */
	public void onHistoryButtonClick(View view) {
		gotoHistoryActivity();
	}
	/**
	 * 読込ボタンが押されたとき
	 * @param view
	 */
	public void onReaderButtonClick(View view){
		
		int requestCode = RequestCode.DASHBORAD2READER.ordinal();
		gotoReaderActivity(requestCode);
	}
	
	/**
	 * リーダーの起動 requestCodeでその後にTimerActivityかCreateActivityを選択
	 * @param requestCode
	 */
	private void gotoReaderActivity(int requestCode){
		Intent intent = new Intent(this, ReaderActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}
	
	/**
	 * 履歴の起動
	 */
	private void gotoHistoryActivity(){
		Intent intent = new Intent(this, HistoryActivity.class);
		int requestCode = RequestCode.DASHBORAD2HISTORY.ordinal();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}

	/**
	 * マイリストの起動
	 */
	private void gotoMyListActivity(){
		Intent intent = new Intent(this, FavoriteActivity.class);
		int requestCode = RequestCode.DASHBOARD2FAVORITE.ordinal();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}
	
	/**
	 * タイマーの起動
	 */
	private void gotoTimerActivity(){
		Intent intent = new Intent(this, TimerActivity.class);
		int requestCode = RequestCode.DASHBORAD2TIMER.ordinal();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}
	
	/**
	 * インテントがもどってきた時の動作
	 * アクションバーが他のActivityで押さたときは
	 * Dashboardまで戻って、目的のActivityを実行する
	 * @param requestCode
	 * @param resultCode
	 * @param intent
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			if(intent == null){
				return;
			}
			//戻り値のREQUEST_CODEに応じてActivityを選択する
			int rtn_code = intent.getIntExtra(RequestCode.KEY_RESUEST_CODE, -1);
			//エラー処理
			if(rtn_code==-1){
				return;
			}
			//アクションバーが他のActivityで押さたときはDashboardまで戻って、他のActivityを実行する
			switch(RequestCode.values()[rtn_code]){
				case ACTION_HISTORY:	//アクションバーの履歴ボタンが押された場合
					gotoHistoryActivity();
					break;
				case ACTION＿TIMER:	//アクションバーのタイマーボタンが押された場合
					gotoTimerActivity();
					break;
				case ACTION_READER:	//アクションバーの読込ボタンが押された場合
					gotoReaderActivity(RequestCode.DASHBORAD2READER.ordinal());
					break;
			}
		}
	}
	
	/**
	 * アクティビティが表示される直前に呼び出される
	 * ここでは、前回バグで強制終了した場合に、レポート送信を行うかどうか
	 * 問い合わせる
	 */
	@Override
	protected void onStart() {
		super.onStart();
		//前回バグで強制終了した場合は、ダイアログを表示する
		AppUncaughtExceptionHandler.showBugReportDialogIfExist();	
	}
}

