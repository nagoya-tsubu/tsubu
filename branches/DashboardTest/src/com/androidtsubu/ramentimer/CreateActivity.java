package com.androidtsubu.ramentimer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class CreateActivity extends Activity {

	//QRコードスキャナーのパッケージ名
	private static final String	QRCODE_PKG_NAME = "com.google.zxing.client.android";
	private static final int REQUEST_BARCODE = 0;
	private static final int REQUEST_GALLERY = 1;
	private static final int REQUEST_CAMERA = 2;
	
	//JANコード
	private EditText janEdit = null;
	//商品名
	private EditText nameEdit = null;
	//商品説明
	private EditText descriptionEdit = null;
	//ゆで時間
	private EditText boilTimeEdit = null;
	//麺の種類
	private RadioGroup noodleTypeRadioGroup = null;
	//商品の画像
	private ImageView noodleImageView = null;
	private Bitmap noodleImage = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_activity);
		//ボタンとかエディットボックスとかのUIを取ってくる
		janEdit = (EditText) findViewById(R.id.JanEdit);
		nameEdit= (EditText) findViewById(R.id.NameEdit);
		descriptionEdit=(EditText) findViewById(R.id.DescriptionEdit);
		boilTimeEdit = (EditText) findViewById(R.id.BoilingTimeEdit);
		noodleTypeRadioGroup	= (RadioGroup) findViewById(R.id.NoodleTypeRadioGroup);
		noodleImageView = (ImageView) findViewById(R.id.NoodleImageView);
		
		//麺の種類をラジオボタンで作成
		final NoodleType NoodleTypeValues[] = NoodleType.values();
		for(int i=0;i<NoodleTypeValues.length;i++){
			RadioButton radioButton=new RadioButton(this);
			radioButton.setText(NoodleTypeValues[i].getName());
			radioButton.setId(i);
			noodleTypeRadioGroup.addView(radioButton);
		}
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
				janEdit.setText(barcode);
				
			}else if(requestCode == REQUEST_GALLERY){//ギャラリー
				try{
					InputStream is = getContentResolver().openInputStream(intent.getData());
					noodleImage = BitmapFactory.decodeStream(is);
					is.close();
					//ビューに画像をセット
					noodleImageView.setImageBitmap(noodleImage);		
				}catch(FileNotFoundException e){
					Toast.makeText(this, "ファイルが見つかりません", 3);
				}catch(IOException e){
					Toast.makeText(this, e.toString(), 3);	
				}
				
	        }else if(requestCode == REQUEST_CAMERA){//カメラ
	        	noodleImage = (Bitmap) intent.getExtras().get("data");
				//ビューに画像をセット
	        	noodleImageView.setImageBitmap(noodleImage);
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
		//ダイアログでカメラかギャラリーを選択させる
		final CharSequence[] items = {"カメラ", "ギャラリー"};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("画像の選択");
		builder.setItems(items, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int item) {
		    	if(item==0)
		    		callCamera();
		    	else
		    		callGallery();
		    }
		});
		builder.show();
	}
	
	/**
	 * ギャラリーをインテントで起動
	 */
	private void callGallery(){
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		try {
			// 第二引数は戻ってきたときの判別用の適当なint
			startActivityForResult(intent, REQUEST_GALLERY);
		} catch (ActivityNotFoundException e) {
			new AlertDialog.Builder(CreateActivity.this).setTitle(
					"Gallery not found.").setMessage(
					"Please install Gallery").setPositiveButton(
					"OK", null).show();
		}		
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
		String jancode = janEdit.getText().toString();
		String name = nameEdit.getText().toString();
		String description = descriptionEdit.getText().toString();
		int boilTime = new Integer(boilTimeEdit.getText().toString()).intValue();
		Bitmap image = noodleImage;
		NoodleType noodleType = NoodleType.values()[noodleTypeRadioGroup.getCheckedRadioButtonId()];
		NoodleMaster noodle = new NoodleMaster(jancode,name,image,boilTime,noodleType);
		return noodle;
	}
	
}