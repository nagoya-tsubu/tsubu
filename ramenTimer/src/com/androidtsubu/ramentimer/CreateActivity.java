package com.androidtsubu.ramentimer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidtsubu.ramentimer.quickaction.ActionItem;
import com.androidtsubu.ramentimer.quickaction.QuickAction;

public class CreateActivity extends Activity {

	//QRコードスキャナーのパッケージ名
	private static final String	QRCODE_PKG_NAME = "com.google.zxing.client.android";
	private static final int REQUEST_BARCODE = 0;
	private static final int REQUEST_GALLERY = 1;
	private static final int REQUEST_CAMERA = 2;

	//商品情報(NoodleMaster)のキー
	private static final String	KEY_NOODLE_MASTER = "NOODLE_MASTER";
	
	//JANコード
	private TextView janText = null;
	//商品名
	private EditText nameEdit = null;
	//ゆで時間
	private EditText boilTimeEdit = null;
//	//麺の種類
//	private RadioGroup noodleTypeRadioGroup = null;
	//商品の画像
	private ImageButton noodleImageView = null;
	private Bitmap noodleImage = null;
	//リクエストコードの値（どこから呼び出されたか）
	private int requestCode = 0;
	//カップラーメン情報
	private NoodleMaster noodleMaster = null;
	
	//QuickAction のアイテム カメラ
	ActionItem itemCamera = null;
	//QuickAction のアイテム カメラ
	ActionItem itemGallery = null;

	/**
	 * CreateActivityがインテントで呼び出されたときに呼ばれる 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_activity);
		//リクエストコードを保持
		Intent intent = getIntent();
		requestCode = intent.getIntExtra(RequestCode.KEY_RESUEST_CODE, -1);
		//リクエストコードがセットされてない場合は終了
		if(requestCode==-1)
			finish();
		//カップラーメン情報の取得
		noodleMaster = (NoodleMaster)intent.getParcelableExtra(KEY_NOODLE_MASTER);

		//ボタンとかエディットボックスとかのUIを取ってくる
		janText = (TextView) findViewById(R.id.JanEdit);
		janText.setText(noodleMaster.getJanCode());
		nameEdit= (EditText) findViewById(R.id.NameEdit);
		boilTimeEdit = (EditText) findViewById(R.id.BoilingTimeEdit);
//		noodleTypeRadioGroup	= (RadioGroup) findViewById(R.id.NoodleTypeRadioGroup);
		noodleImageView = (ImageButton) findViewById(R.id.NoodleImageButton);
		
//		//麺の種類をラジオボタンで作成
//		final NoodleType NoodleTypeValues[] = NoodleType.values();
//		for(int i=0;i<NoodleTypeValues.length;i++){
//			RadioButton radioButton=new RadioButton(this);
//			radioButton.setText(NoodleTypeValues[i].getName());
//			radioButton.setId(i);
//			radioButton.setTextColor(R.color.information_form_text2);
//			noodleTypeRadioGroup.addView(radioButton);
//		}
		
		//QuickActionのためのItemを作成 QuickAction自体は onLoadImageClick()で作成
		itemCamera = new ActionItem();
		itemCamera.setTitle("カメラ");
		itemCamera.setIcon(getResources().getDrawable(R.drawable.camera_button));
		itemCamera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				callCamera();	//カメラを起動
			}
		});
		
		itemGallery = new ActionItem();
		itemGallery.setTitle("ギャラリー");
		itemGallery.setIcon(getResources().getDrawable(R.drawable.image_button));
		itemGallery.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				callGallery();	//ギャラリーを起動
			}
		});
	}


	/**
	 * インテントがもどってきた時の動作
	 * @param requestCode
	 * @param resultCode
	 * @param intent
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == RESULT_OK) {
			
			if (requestCode == REQUEST_BARCODE) {//バーコードリーダー
				//EditTextにバーコードリーダーで読み込んだJANコードの値を設定
				final String barcode = intent.getStringExtra("SCAN_RESULT");
				janText.setText(barcode);
				
			}else if(requestCode == REQUEST_GALLERY){//ギャラリー
				try{
					InputStream is = getContentResolver().openInputStream(intent.getData());
					noodleImage = BitmapFactory.decodeStream(is);
					is.close();
					//ビューに画像をセット
					noodleImageView.setImageBitmap(noodleImage);
					noodleImageView.setBackgroundColor(R.color.clear);//背景の削除
				}catch(FileNotFoundException e){
					Toast.makeText(this, "ファイルが見つかりません", 3);
				}catch(IOException e){
					Toast.makeText(this, e.toString(), 3);	
				}
				
	        }else if(requestCode == REQUEST_CAMERA){//カメラ
	        	noodleImage = (Bitmap) intent.getExtras().get("data");
				//ビューに画像をセット
	        	noodleImageView.setImageBitmap(noodleImage);
				noodleImageView.setBackgroundColor(R.color.clear);//背景の削除
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
			startActivityForResult(intent, REQUEST_BARCODE);
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
	
	/**
	 * 画像読み込みボタンが押された時の動作 インテントでギャラリーかカメラを呼び出す
	 * @param v
	 */
	public void onLoadImageClick(View v) {
		//QuickAction
		QuickAction qa = new QuickAction(v);
		
		qa.addActionItem(itemCamera);
		qa.addActionItem(itemGallery);
		qa.show();
	}
	
	/**
	 * ギャラリーをインテントで起動
	 */
	private void callGallery(){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_GALLERY);
	}

	/**
	 * カメラをインテントで起動
	 */
	private void callCamera(){
		Intent intent = new Intent();
		intent.setAction("android.media.action.IMAGE_CAPTURE");
		startActivityForResult(intent, REQUEST_CAMERA);
	}
		
	/**
	 * 登録ボタンが押された時の動作
	 * @param v
	 */
	public void onCreateClick(View v) {
		NoodleMaster noodleMaster = getNoodleMaster();
		EntryAsyncTask entry = new EntryAsyncTask(this); 
		entry.execute(noodleMaster);
	}
	
	/**
	 * logoボタンが押された時の動作
	 * @param v
	 */
	public void onLogoClick(View v) {
		finish();
	}

	/**
	 * UIから登録情報を集めて返す
	 * @return
	 */
	NoodleMaster getNoodleMaster(){
		//EditTextやRadioGroupから状態を取得
		String jancode = janText.getText().toString();
		String name = nameEdit.getText().toString();
		int boilTime = new Integer(boilTimeEdit.getText().toString()).intValue();
		Bitmap image = noodleImage;
//		NoodleType noodleType = NoodleType.values()[noodleTypeRadioGroup.getCheckedRadioButtonId()];
		NoodleMaster noodle = new NoodleMaster(jancode,name,image,boilTime);
		return noodle;
	}
	
	/**
	 * ラーメンの情報を登録する
	 * @author leibun
	 *
	 */
	private class EntryAsyncTask extends AsyncTask<NoodleMaster, Integer, Integer>{
		//表示用にコンテキストを保持
		private Activity activity =null; 
		private NoodleMaster noodleMaster = null;
		NoodleManager nm;
		
		private static final int RESULT_CREATE_OK = 0;	//登録成功
		private static final int RESULT_ERROR_SQLITE = 1;//SQLITEでエラー
		private static final int RESULT_ERROR_GAE = 2;	//Webへの登録でエラー
		
		/**
		 * コンストラクタ
		 * @param context
		 */
		public EntryAsyncTask(Activity activity){
			this.activity = activity;
			this.nm = new NoodleManager();
		}
		
		/**
		 * Web登録があるので別スレッドで実行
		 * @params params
		 */
		@Override
		protected Integer doInBackground(NoodleMaster... params) {
			try{
				//カップラーメンの情報をWebとローカルに登録
				nm.createNoodleMaster(params[0]);
			}catch(GaeException e){
				return RESULT_ERROR_GAE;
			} catch (java.sql.SQLException e) {
				return RESULT_ERROR_SQLITE;
			}
			return RESULT_CREATE_OK;
		}
		/**
		 * 
		 * doInBackgroundが呼ばれた後に呼び出される
		 */
		@Override
		protected void onPostExecute(Integer result){
			switch(result){
				case RESULT_CREATE_OK:
					Toast.makeText(activity, "登録完了", Toast.LENGTH_LONG).show();
					break;
				case RESULT_ERROR_GAE:
					Toast.makeText(activity, "サーバーへの登録に失敗しました", Toast.LENGTH_LONG).show();
					return;
				case RESULT_ERROR_SQLITE:
					Toast.makeText(activity, "ローカルへの登録に失敗しました", Toast.LENGTH_LONG).show();
					return;
			}
			//リーダーから呼び出された場合
			if(requestCode == RequestCode.READER2CREATE.ordinal()){
				//ダイアログでカメラかギャラリーを選択させる
				final CharSequence[] items = {"タイマーを起動", "ダッシュボードに戻る"};
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("画像の選択");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	if(item==0)
							callTimerActivity();
				    	else
							activity.finish();
				    }
				});
				builder.show();
			//タイマーから呼び出された場合
			}else if(requestCode == RequestCode.TIMER2CREATE.ordinal()){
				activity.finish();
			}
		}

		/**
		 * タイマーをインテントで呼び出す
		 */
		private void callTimerActivity(){
			Intent intent = new Intent(this.activity, TimerActivity.class);
			intent.putExtra(RequestCode.KEY_RESUEST_CODE, RequestCode.CREATE2TIMER.ordinal());
			activity.startActivityForResult(intent,RequestCode.CREATE2TIMER.ordinal());			
		}
	}
}
