package com.androidtsubu.ramentimer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class DashBoardActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
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
		int requestCode = RequestCode.DASHBOARD2CREATE.ordinal();
		gotoReaderActivity(requestCode);		
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
}

