/**
 * JANコード読取クラス
 * @author Ikuo Tansho(@tan1234jp)
 * @version 1.0
 */

package com.androidtsubu.ramentimer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class ReaderActivity extends Activity {

	// QRコードスキャナーのパッケージ名
	private static final String QRCODE_PKG_NAME = "com.google.zxing.client.android";
	// ラーメンデータ
	private NoodleMaster _noodleMaster;
	// ラーメンデータベース・マネージャ
	private NoodleManager _noodleManager;
	// JANコード
	private String _janCode;
	// 呼び出し元インテント
	private int _requestCode;

	// ReaderActivityの状態
	private static final int EXECUTE_QR_CODE_SCANNER = 100; // QRコードスキャナの実行
	private static final int DOWNLOAD_QR_CODE_SCANNER = 200; // QRコードスキャナーのダウンロード
	private static final int RECEIVE_NOODLE_DATA = 300; // ラーメン情報受信
	private static final int GOTO_NEXT_INTENT = 400; // 次のインテントへ処理を移す

	// メッセージハンドラーに対する処理
	private final Handler _handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case EXECUTE_QR_CODE_SCANNER:
				// QRコードスキャナーを実行する
				executeQrCodeScanner();
				break;

			case DOWNLOAD_QR_CODE_SCANNER:
				// QRコードスキャナーをAndroid Marketからダウンロードする
				getQrCodeScanner();
				break;

			case RECEIVE_NOODLE_DATA:
				// ラーメン情報を受信する
				receiveNoodleData();
				break;

			case GOTO_NEXT_INTENT:
				// 次のアクティビティへ遷移する
				gotoNextActivity();
				break;

			default:
				// その他・・・
				// switch()内に処理が書かれていないステートが存在するため、
				// 例外を投げておく
				throw new ReaderStateException(msg.toString());
			}
		}
	};

	/**
	 * コンストラクタ
	 */
	public ReaderActivity() {
		// 内部変数の初期化
		_noodleMaster = null;
		_noodleManager = null;
		_janCode = null;
	}

	/**
	 * ReaderActivityがインテント呼び出しされた時に実行する
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// どのボタンからこのインテントが呼び出されたかを取得する
		Intent intent = getIntent();
		_requestCode = intent.getIntExtra(RequestCode.KEY_RESUEST_CODE, -1);

		// QRコードスキャナーを実行する
		_handler.sendEmptyMessage(EXECUTE_QR_CODE_SCANNER);
	}

	/**
	 * QRコードスキャナを実行する
	 */
	private void executeQrCodeScanner() {
		// QRコードスキャナーのインテントを設定する
		final Intent intent = new Intent(QRCODE_PKG_NAME + ".SCAN");
		// JANコードを読み取る
		intent.putExtra("SCAN_MODE", "ONE_D_MODE");

		// QRコードスキャナーを呼び出す
		try {
			startActivityForResult(intent, EXECUTE_QR_CODE_SCANNER);
		} catch (ActivityNotFoundException e) {
			// アクティビティが存在しない(=インテントの開始に失敗した)場合は、
			// Android Marketからダウンロードするか問い合わせる
			_handler.sendEmptyMessage(DOWNLOAD_QR_CODE_SCANNER);
		}
	}

	/**
	 * アクティビティの実行結果処理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		//アクティビティのリクエストコードで処理を分ける
		switch (requestCode) {
		case EXECUTE_QR_CODE_SCANNER:
			//QRコードスキャナー実行後
			if(RESULT_OK == resultCode) {
				//QRコードスキャナーからJANコードをスキャンできた場合は、
				//ラーメン情報を履歴またはGAEから取得してみる
				_janCode = intent.getStringExtra("SCAN_RESULT");
				_handler.sendEmptyMessage(RECEIVE_NOODLE_DATA);
			} else {
				// 「Back」キー等でJANコードをスキャンできなかった場合は、
				// JANコード、ラーメン情報をNULLに設定して、次のインテントへ遷移する
				_janCode = null;
				_noodleMaster = null;
				_handler.sendEmptyMessage(GOTO_NEXT_INTENT);
			}
			break;

		case DOWNLOAD_QR_CODE_SCANNER:
			//Android Market実行後
			if(RESULT_OK == resultCode) {
				//ダウンロードが完了したら、QRコードスキャナーを実行する
				_handler.sendEmptyMessage(EXECUTE_QR_CODE_SCANNER);
			} else {
				//ダウンロード失敗(or しない)場合は、Dashboardに遷移する
				finish();
			}
			break;

		default:
			// 呼び出したインテントが空の場合は、処理を終了する
			if(RESULT_OK ==resultCode){
				setResult(RESULT_OK,intent);
				finish();
			}
				
			break;
		}

	}

	/**
	 * QRコードスキャナーが受信したJANコードを検索キーとして 履歴・GAEから商品情報を取得する
	 */
	private void receiveNoodleData() {

		// NoodleManagerクラスを実体化させる
		if (null == _noodleManager) {
			_noodleManager = new NoodleManager(this);
		}
		// JANコードを検索キーにして、履歴またはGAEから
		// 商品を検索する
		try {
			_noodleMaster = _noodleManager.getNoodleMaster(_janCode);
			// @hideponm
			if (_noodleMaster == null
					&& RequestCode.values()[_requestCode]
							.equals(RequestCode.DASHBORAD2READER)) {
				// 該当商品がないのでJANコードだけ入れたNoodleMasterを作ってあげる
				_noodleMaster = new NoodleMaster(_janCode, "", null, 0);
			}
		} catch (GaeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 次のインテントへ遷移する
		_handler.sendEmptyMessage(GOTO_NEXT_INTENT);
	}

	/**
	 * Android MarketからQRコードスキャナを取得する (直接ダウンロードするのではなく、Android
	 * Marketのダウンロードページへ飛ぶ)
	 */
	private void getQrCodeScanner() {

		// QRコードをAndroid Marketからダウンロードしてよいか
		// ダイアログを表示して問い合わせる
		new AlertDialog.Builder(this)
		.setTitle("QR Code Scanner not found.")
		.setMessage("QRコードスキャナーをAndroid Marketからインストールしますか？")
		.setPositiveButton("いいえ", new DialogInterface.OnClickListener() {
			//「いいえ」押下時は、ダッシュボードに戻る
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		})
		.setNegativeButton("はい", new DialogInterface.OnClickListener() {
			//「はい」押下時は、Android Marketへ飛び、QRコードスキャナーの
			//ダウンロードページを表示する
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				final Intent intent = new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("market://search?q=pname:" + QRCODE_PKG_NAME));
				startActivityForResult(intent, DOWNLOAD_QR_CODE_SCANNER);
			}
		})
		.create().show();
	}

	// 商品情報(NoodleMaster)のキー
	private static final String KEY_NOODLE_MASTER = "NOODLE_MASTER";

	/**
	 * 次の画面へ遷移する
	 */
	private void gotoNextActivity() {
		Intent intent;

		// 次の画面へ遷移する
		// (1)商品情報オブジェクトが空の場合は、タイマー画面へ遷移する
		// (2)JANコードが取得できなかった場合は、タイマー画面へ遷移する
		// ・QRコードスキャナーで「Back」キーを押下した場合
		// ・QRコードスキャナーをインストールしなかった場合
		// 　→「Dashboardからタイマー」遷移と同じ
		// (3)商品の取得ができなかった場合は、商品登録画面へ遷移する
		// ・履歴、GAEから商品情報を取得できなかった場合
		// (4)それ以外は、商品情報と共にタイマー画面へ遷移する
		
		//(1)商品情報オブジェクトが空の場合
		//※通常はありえない
		if (null == _noodleMaster) {
			intent = new Intent(this, TimerActivity.class);
			intent.putExtra(RequestCode.KEY_RESUEST_CODE,
					RequestCode.DASHBORAD2TIMER.ordinal());
		} else {
			//JANコードが取得できなかった場合
			if (null == _noodleMaster.getJanCode()) {
				intent = new Intent(this, TimerActivity.class);
				intent.putExtra(RequestCode.KEY_RESUEST_CODE,
						RequestCode.DASHBORAD2TIMER.ordinal());
			}
			//商品の取得ができなかった場合
			else if (null == _noodleMaster.getName()
					|| "".equals(_noodleMaster.getName())) {
				intent = new Intent(this, CreateActivity.class);
				intent.putExtra(RequestCode.KEY_RESUEST_CODE,
						RequestCode.READER2CREATE.ordinal());
			}
			//上記以外(商品情報が取得できた場合)
			else {
				intent = new Intent(this, TimerActivity.class);
				intent.putExtra(RequestCode.KEY_RESUEST_CODE,
						RequestCode.READER2TIMER.ordinal());
			}
		}
		// NoodleMaster情報もインテントに情報を送る
		intent.putExtra(KEY_NOODLE_MASTER, _noodleMaster);
		// インテントを発行する
		startActivity(intent);

		// 自分自身を終了する
		finish();
	}

	/**
	 * アクティビティ停止時
	 */
	@Override
	protected void onStop() {
		super.onStop();
		// 念のため、ハンドラーに登録されたメッセージを削除しておく
		_handler.removeMessages(EXECUTE_QR_CODE_SCANNER);
		_handler.removeMessages(RECEIVE_NOODLE_DATA);
		_handler.removeMessages(GOTO_NEXT_INTENT);
	}
}
