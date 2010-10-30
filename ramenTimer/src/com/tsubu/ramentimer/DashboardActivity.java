package com.tsubu.ramentimer;

import com.tsubu.ramentimer.R;

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
		startActivity(new Intent(this, CreateActivity.class));
	}
	
	public void onHistoryButtonClick(View view) {
		startActivity(new Intent(this, HistoryActivity.class));
	}
}

