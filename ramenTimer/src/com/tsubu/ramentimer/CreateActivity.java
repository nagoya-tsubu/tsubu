package com.tsubu.ramentimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
//import android.view.View.OnClickListener;
import android.widget.EditText;
//import android.widget.ImageButton;

public class CreateActivity extends Activity {

	//QRコードスキャナーのパッケージ名
	private static final String	QRCODE_PKG_NAME = "com.google.zxing.client.android";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_activity);
/*
		ImageButton captureButton = (ImageButton) findViewById(R.id.CaptureButton);
		captureButton.setOnClickListener(captureClick);
*/
	}

	/**
	 * キャプチャーボタンが押された時の動作 インテントでバーコードリーダーを呼び出しに行く
	 */
/*
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
*/

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
	
	/**
	 * バーコードをキャプチャーする時に呼び出される
	 * @param view
	 */
	public void onCaptureClick(View view) {

		//QRコードスキャナーのインテントを設定する
		final Intent intent = new Intent(QRCODE_PKG_NAME + ".SCAN");
		//JANコードを読み取る
		intent.putExtra("SCAN_MODE", "ONE_D_MODE");

		//QRコードスキャナーを呼び出す
		try {
			startActivityForResult(intent, 0);
		} catch(ActivityNotFoundException e) {
			//アクティビティが存在しない(=インテントの開始に失敗した)場合は、
			//Android Marketからダウンロードするか問い合わせる
			
			//TODO: メッセージ文字列のリソース化
			
			new AlertDialog.Builder(this)
					.setTitle("QR Code Scanner not found.")
					.setMessage("QRコードスキャナーをAndroid Marketからインストールしますか？")
					.setPositiveButton("はい", new DialogInterface.OnClickListener() {
						//「はい」押下時は、Android Marketへ飛び、QRコードスキャナーの
						//ダウンロードページを表示する
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							final Intent intent = new Intent(
									Intent.ACTION_VIEW,
									Uri.parse("market://search?q=pname:" + QRCODE_PKG_NAME));
							startActivity(intent);
						}
					})
					.setNegativeButton("いいえ", null)
					.create().show();
		}
	}
}
