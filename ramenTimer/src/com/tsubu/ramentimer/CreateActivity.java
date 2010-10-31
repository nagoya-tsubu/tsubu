package com.tsubu.ramentimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;

public class CreateActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_activity);

		ImageButton captureButton = (ImageButton) findViewById(R.id.CaptureButton);
		captureButton.setOnClickListener(captureClick);
	}

	/**
	 * キャプチャーボタンが押された時の動作 インテントでバーコードリーダーを呼び出しに行く
	 */
	final OnClickListener captureClick = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			// intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			intent.putExtra("SCAN_MODE", "ONE_D_MODE");
			try {
				startActivityForResult(intent, 0);
			} catch (ActivityNotFoundException e) {
				new AlertDialog.Builder(CreateActivity.this).setTitle(
						"QR Scaner not found.").setMessage(
						"Please install QR code scanner").setPositiveButton(
						"OK", null).show();
			}
		}
	};

	/**
	 * インテントがもどってきた時の動作
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == 0) {
			//EditTextにバーコードリーダーで読み込んだJANコードの値を設定
			EditText jancode = (EditText) findViewById(R.id.JanEdit);
			if (resultCode == RESULT_OK) {
				final String barcode = intent.getStringExtra("SCAN_RESULT");
				jancode.setText(barcode);
			}
		}
	}
}
