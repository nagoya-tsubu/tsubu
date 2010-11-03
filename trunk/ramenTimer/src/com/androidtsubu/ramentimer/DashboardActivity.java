package com.androidtsubu.ramentimer;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashboardActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);
	}
	
	public void onTimerButtonClick(View view) {
		startActivity(new Intent(this, TimerActivity.class));
	}

	public void onCreateButtonClick(View view) {
		int requestCode = RequestCode.READER2CREATE.ordinal();
		Intent intent = new Intent(this, CreateActivity.class)
		.putExtra(RequestCode.RESUEST_CODE,requestCode);
		startActivityForResult(intent, requestCode);
	}
	
	public void onHistoryButtonClick(View view) {
		startActivity(new Intent(this, HistoryActivity.class));
	}
}

