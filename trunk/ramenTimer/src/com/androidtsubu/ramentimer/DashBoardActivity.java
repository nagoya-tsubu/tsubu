package com.androidtsubu.ramentimer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashBoardActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
	}
	
	public void onTimerButtonClick(View view) {
		Intent intent = new Intent(this, TimerActivity.class);
		int requestCode = RequestCode.DASHBORAD2TIMER.ordinal();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}

	public void onCreateButtonClick(View view) {
		Intent intent = new Intent(this, ReaderActivity.class);
		int requestCode = RequestCode.DASHBORAD2READER.ordinal();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}
	
	public void onHistoryButtonClick(View view) {
		Intent intent = new Intent(this, HistoryActivity.class);
		int requestCode = RequestCode.DASHBORAD2HISTORY.ordinal();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}
	
	public void onStarredClick(View view){
		//CreateActivityのデバックにちょっと拝借  by leibun
		final String	KEY_NOODLE_MASTER = "NOODLE_MASTER";
		final String    TEST_JAN_CODE ="4903320241800";
		Intent intent = new Intent(this, CreateActivity.class);
		int requestCode = RequestCode.READER2CREATE.ordinal();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		intent.putExtra(KEY_NOODLE_MASTER, new NoodleMaster(TEST_JAN_CODE, null, null, 0));
		startActivityForResult(intent, requestCode);
		
	}
}

