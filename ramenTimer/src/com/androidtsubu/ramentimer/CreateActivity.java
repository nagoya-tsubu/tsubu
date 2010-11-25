package com.androidtsubu.ramentimer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidtsubu.ramentimer.quickaction.ActionItem;
import com.androidtsubu.ramentimer.quickaction.QuickAction;

public class CreateActivity extends Activity {
	// RequestCode
	private static final int REQUEST_GALLERY = 1;
	private static final int REQUEST_CAMERA = 2;
	
	// 秒の増減間隔
	private static final int SEC_INTERVALS = 10;
	// 分の上限値
	private static final int MIN_UPPEL_LIMIT = 9;
	// 分の下限値
	private static final int MIN_LOWER_LIMIT = 0;


	// 商品情報(NoodleMaster)のキー
	private static final String KEY_NOODLE_MASTER = "NOODLE_MASTER";

	// JANコード
	private TextView janText = null;
	// 商品名
	private EditText nameEdit = null;
	// ゆで時間(分）	
	private TextView minTextView = null;
	// ゆで時間(秒)
	private TextView secTextView = null;
	// //麺の種類
	// private RadioGroup noodleTypeRadioGroup = null;
	// 商品の画像
	private ImageButton noodleImageView = null;
	private Bitmap noodleImage = null;
	// リクエストコードの値（どこから呼び出されたか）
	private int requestCode = 0;
	// カップラーメン情報
	private NoodleMaster noodleMaster = null;
	// カメラ撮影用
	private String mPicturePath;

	// 確認ダイアログ
	AlertDialog verificationDialog =null;
	
	
	// QuickAction のアイテム カメラ
	ActionItem itemCamera = null;
	// QuickAction のアイテム ギャラリー
	ActionItem itemGallery = null;
	// QuickAction のアイテム ホーム
	ActionItem itemHome = null;
	// QuickAction のアイテム タイマー
	ActionItem itemTimer = null;

	// WEB登録用スレッド
	EntryAsyncTask entry = null;

	/**
	 * CreateActivityがインテントで呼び出されたときに呼ばれる
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create);
		// リクエストコードを保持
		Intent intent = getIntent();
		requestCode = intent.getIntExtra(RequestCode.KEY_RESUEST_CODE, -1);
		// リクエストコードがセットされてない場合は終了
		if (requestCode == -1)
			finish();
		// カップラーメン情報の取得
		noodleMaster = (NoodleMaster) intent
				.getParcelableExtra(KEY_NOODLE_MASTER);

		// ボタンとかエディットボックスとかのUIを取ってくる
		janText = (TextView) findViewById(R.id.JanEdit);
		nameEdit = (EditText) findViewById(R.id.NameEdit);
		minTextView = (TextView) findViewById(R.id.MinTextView);
		secTextView = (TextView) findViewById(R.id.SecTextView);
		// noodleTypeRadioGroup = (RadioGroup)
		// findViewById(R.id.NoodleTypeRadioGroup);
		noodleImageView = (ImageButton) findViewById(R.id.NoodleImageButton);
		// NoodleMasterから情報を取り出す
		String nmJancode = noodleMaster.getJanCode();
		String nmName = noodleMaster.getName();
		String nmTimerLimitString = noodleMaster.getTimerLimitString();
		int nmTimerLimitInt=noodleMaster.getTimerLimit();
		Bitmap nmImage = noodleMaster.getImage();

		if (nmJancode != null)
			janText.setText(nmJancode);
		if (nmName != null)
			nameEdit.setText(nmName);
		if (nmTimerLimitString != null){
			updateTimerTextView(nmTimerLimitInt);
		}
		if (nmImage != null)
			noodleImageView.setImageBitmap(nmImage);

		// 全部埋まっている場合は、既に登録されている
		if (nmJancode != null && nmName != null && nmTimerLimitString != null
				&& nmImage != null) {
			Toast.makeText(this, "既に登録されています", Toast.LENGTH_LONG).show();
			finish();
		}

		// //麺の種類をラジオボタンで作成
		// final NoodleType NoodleTypeValues[] = NoodleType.values();
		// for(int i=0;i<NoodleTypeValues.length;i++){
		// RadioButton radioButton=new RadioButton(this);
		// radioButton.setText(NoodleTypeValues[i].getName());
		// radioButton.setId(i);
		// radioButton.setTextColor(R.color.information_form_text2);
		// noodleTypeRadioGroup.addView(radioButton);
		// }

		// QuickActionのためのItemを作成 QuickAction自体は onLoadImageClick()で作成
		itemCamera = new ActionItem();
		itemCamera.setTitle("カメラ");
		itemCamera.setIcon(getResources().getDrawable(
				R.drawable.ic_quickaction_camera));
		itemCamera.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				callCamera(); // カメラを起動
			}
		});

		itemGallery = new ActionItem();
		itemGallery.setTitle("ギャラリー");
		itemGallery.setIcon(getResources().getDrawable(
				R.drawable.ic_quickaction_gallery));
		itemGallery.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				callGallery(); // ギャラリーを起動
			}
		});
		itemHome = new ActionItem();
		itemHome.setTitle("ダッシュボード");
		itemHome.setIcon(getResources().getDrawable(
				R.drawable.ic_quickaction_home));
		itemHome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				entry.execute(noodleMaster);
				finish();
			}
		});
		itemTimer = new ActionItem();
		itemTimer.setTitle("タイマー");
		itemTimer.setIcon(getResources().getDrawable(
				R.drawable.ic_quickaction_timer));
		itemTimer.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				entry.execute(noodleMaster);
				callTimerActivity();
				finish();
			}
		});
	}
	
	
	/**
	 * 画面が回転時に呼び出される
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}



	/**
	 * インテントがもどってきた時の動作
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param intent
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// dimens.xmlから値を取得 リサイズのパラメータ
		int resizeLength = (int) getResources().getDimension(
				R.dimen.image_longer_length);
		if (resultCode == RESULT_OK) {
			Uri uri = null;
			if (requestCode == REQUEST_CAMERA || requestCode == REQUEST_GALLERY) {
				if (requestCode == REQUEST_GALLERY) {// ギャラリー
					uri = intent.getData();
				} else {
					/*
					 * カメラの動作
					 * GalaxyS対策：uriをPath(String)から生成　
					 * callCamera側でセットしたUriはnullになってしまうらしい。
					 * Xperia対策：セットしたファイル名の通りに画像が作られないので、getData()からUriを取得
					 */					
					File file = new File(mPicturePath);
					uri = Uri.fromFile(file);
					// Experia 2.1対策
					if (intent != null){
						Uri _uri = intent.getData();
						if(_uri != null)
							uri = intent.getData();
					}
				}
				try {
					// 画像の取得
					// URI -> image size -> small bitmap
					noodleImage = getImageFromUriUsingBitmapFactoryOptions(uri,
							resizeLength);
					// // URI -> bitmap -> small bitmap
					// noodleImage = getImageFromUriUsingResizeImage(mPictureUri,resizeLength);
					// ビューに画像をセット
					noodleImageView.setImageBitmap(noodleImage);

				} catch (IOException e) {
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
							.show();
				} catch (NullPointerException e) {
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
							.show();
				} catch (OutOfMemoryError e) {
					Toast.makeText(this, e.toString(), Toast.LENGTH_LONG)
							.show();
				}
			}
		}
	}

	/**
	 * UriからBitmapを取得する
	 * 
	 * @param uri
	 * @param resizeLength
	 *            　リサイズパラメータ
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Bitmap getImageFromUriUsingBitmapFactoryOptions(Uri uri,
			int resizeLength) throws FileNotFoundException, IOException {
		if (uri == null)
			throw new NullPointerException();
		// UriからBitmapクラスを取得
		InputStream is = getContentResolver().openInputStream(uri);
		// オプション
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 画像サイズだけを取得するように設定　デコードはされない
		opts.inJustDecodeBounds = true;
		Bitmap image = BitmapFactory.decodeStream(is, null, opts);
		is.close();
		// デコードするように設定
		opts.inJustDecodeBounds = false;
		// 縦横比を固定したままリサイズ
		resizeOptions(opts, resizeLength);

		if (uri == null)
			Toast.makeText(this, "uri == null", Toast.LENGTH_SHORT).show();
		is = getContentResolver().openInputStream(uri);
		if (is == null)
			Toast.makeText(this, "is == null", Toast.LENGTH_SHORT).show();
		image = BitmapFactory.decodeStream(is, null, opts);
		is.close();
		return image;
	}

	/**
	 * UriからBitmapを取得する
	 * 
	 * @param uri
	 * @param resizeLength
	 *            　リサイズパラメータ
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Bitmap getImageFromUriUsingResizeImage(Uri uri, int resizeLength)
			throws FileNotFoundException, IOException {
		// UriからBitmapクラスを取得
		InputStream is = getContentResolver().openInputStream(uri);
		// メモリを大量に使うのでガベコレ これしておかないと、何回か呼び出されるとエラーで止まる
		System.gc();
		Bitmap tmp = BitmapFactory.decodeStream(is);
		is.close();
		// 縦横比を固定したままリサイズ
		Bitmap image = resizeImage(tmp, resizeLength);
		return image;
	}

	/**
	 * 縦横比を維持したまま画像をリサイズするメソッド 長い方の辺が引数のlengthの長さなる
	 * 
	 * @param img
	 * @param length
	 * @return
	 */
	public Bitmap resizeImage(Bitmap img, int length) {
		int height = img.getHeight();
		int width = img.getWidth();
		// 縦、横の長い方
		float longer = height < width ? (float) width : (float) height;
		// 伸縮するスケール
		float scale = length / longer;
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		// リサイズした画像を作成
		Bitmap dst = Bitmap
				.createBitmap(img, 0, 0, width, height, matrix, true);
		return dst;
	}

	/**
	 * 縦横比を維持したまま画像をリサイズするメソッド 長い方の辺が引数のlengthの長さなる
	 * 
	 * @param opts
	 * @param length
	 */
	public void resizeOptions(BitmapFactory.Options opts, int length) {
		int height = opts.outWidth;
		int width = opts.outHeight;
		// 縦、横の長い方
		float longer = height < width ? (float) width : (float) height;
		// 伸縮するスケール
		float scale = length / longer;
		// 置き換え
		opts.outHeight = Math.round(height * scale);
		opts.outWidth = Math.round(width * scale);
	}

	/**
	 * アクションバーの履歴ボタンが押されたとき インテントに（RequestCode）をセットしてfinish()
	 * 
	 * @param v
	 */
	public void onHistoryButtonClick(View v) {
		Intent intent = new Intent();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, RequestCode.ACTION＿TIMER
				.ordinal());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * アクションバーのタイマーボタンが押されたとき インテントに（）をセットしてFinish()
	 * 
	 * @param v
	 */
	public void onTimerButtonClick(View v) {
		Intent intent = new Intent();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.ACTION_HISTORY.ordinal());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 画像読み込みボタンが押された時の動作 インテントでギャラリーかカメラを呼び出す
	 * 
	 * @param v
	 */
	public void onLoadImageClick(View v) {
		// QuickAction
		QuickAction qa = new QuickAction(v);

		qa.addActionItem(itemCamera);
		qa.addActionItem(itemGallery);
		qa.show();
	}

	/**
	 * 分の＋ボタンが押されたとき
	 * @param v
	 */
	public void onMinUpClick(View v){
		addTimerCount(60);
	}
	
	/**
	 * 分のーボタンが押されたとき
	 * @param v
	 */
	public void onMinDownClick(View v){
		addTimerCount(-60);
	}
	
	/**
	 * 秒の＋ボタンが押されたとき 
	 * ※分も変わる場合がある
	 * @param v
	 */
	public void onSecUpClick(View v){
		addTimerCount(SEC_INTERVALS);
	}
	
	/**
	 * 秒のーボタンが押されたとき
	 * ※分も変わる場合がある
	 * @param v
	 */
	public void onSecDownClick(View v){
		addTimerCount(-SEC_INTERVALS);
	}
	
	/**
	 * タイマーに時間を足す
	 * @param sec
	 */
	private void addTimerCount(int sec){
		int setTime = (Integer.valueOf(minTextView.getText().toString()) * 60 )+ Integer.valueOf(secTextView.getText().toString());
		setTime = setTime + sec ;
		// 上限値または下限値を超える場合は処理しない
		if(setTime > MIN_UPPEL_LIMIT * 60 || setTime < MIN_LOWER_LIMIT){
			return;
		}
		updateTimerTextView(setTime);
	}

	/**
	 * タイマーの残り時間を更新する
	 * @param time
	 */
	private void updateTimerTextView(long sec) {
		minTextView.setText(String.valueOf(sec / 60));
		secTextView.setText(getSecText(sec % 60));
	}
	
	/**
	 * 引数が有効値でなければ有効値を戻す。 1桁の場合は前0を付加する。
	 */
	private String getSecText(long sec) {
		if (sec >= 60) {
			sec = sec - 60;
		} else if (sec < 0) {
			sec = sec + 60;
		}
		String secText = String.valueOf(sec);
		if (sec < 10) { // 一桁の場合は前0を表示
			secText = "0" + sec;
		}
		return secText;
	}

	/**
	 * ギャラリーをインテントで起動
	 */
	private void callGallery() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_GALLERY);
	}

	/**
	 * カメラをインテントで起動
	 */
	private void callCamera() {
		String filename = "RamenTimer_" + System.currentTimeMillis() + ".jpg";
		File file = new File(Environment.getExternalStorageDirectory(),filename);
		mPicturePath = file.getPath();
		
		Intent intent = new Intent();
		intent.setAction("android.media.action.IMAGE_CAPTURE");
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		startActivityForResult(intent, REQUEST_CAMERA);
	}

	/**
	 * タイマーをインテントで呼び出す
	 */
	private void callTimerActivity() {
		Intent intent = new Intent(this, TimerActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, RequestCode.CREATE2TIMER
				.ordinal());
		intent.putExtra(KEY_NOODLE_MASTER, noodleMaster);
		startActivityForResult(intent, RequestCode.CREATE2TIMER.ordinal());
	}

	/**
	 * 登録ボタンが押された時の動作
	 * 
	 * @param v
	 */
	public void onCreateClick(View v) {
		try {
			noodleMaster = getNoodleMaster();
		} catch (Exception e) {
			Toast.makeText(this, "入力項目を埋めてください", Toast.LENGTH_LONG).show();
			return;
		}
		entry = new EntryAsyncTask(this);
		// 登録ボタンを無効化
		v.setEnabled(false);
		// 確認ダイアログの作成
		AlertDialog.Builder builder;

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(this.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.dialog_create_verification,
				(ViewGroup) findViewById(R.id.layout_verification_root));
		TextView jan_text = (TextView) layout.findViewById(R.id.JanText);
		jan_text.setText(noodleMaster.getJanCode());
		TextView name_text = (TextView) layout.findViewById(R.id.RamenName);
		name_text.setText(noodleMaster.getName());
		TextView time_text = (TextView) layout.findViewById(R.id.BoilingTime);
		time_text.setText(noodleMaster.getTimerLimitString());
		ImageView image = (ImageView) layout.findViewById(R.id.NoodleImage);
		image.setImageBitmap(noodleMaster.getImage());
		Button okButton = (Button) layout.findViewById(R.id.CreateDialogCancelButton);
		okButton.setOnClickListener(dialogOkClick);
		Button cancelButton = (Button) layout.findViewById(R.id.CreateDialogCancelButton);
		cancelButton.setOnClickListener(dialogCancelClick);

		builder = new AlertDialog.Builder(this);
		builder.setMessage("これで登録しますか？").setView(layout).setCancelable(false);
		verificationDialog = builder.create();
		verificationDialog.show();

		// //リーダーアクティビティから起動された場合
		// if(requestCode == RequestCode.READER2CREATE.ordinal()){
		// entry.execute(noodleMaster);
		// // QuickAction qa = new QuickAction(v);
		// // qa.addActionItem(itemHome);
		// // qa.addActionItem(itemTimer);
		// // qa.show();
		// //タイマーから起動された場合
		// }else if(requestCode == RequestCode.TIMER2CREATE.ordinal()){
		// entry.execute(noodleMaster);
		// }
	}
	
	/**
	 * ダイアログでOKがクリックされたとき
	 */
	OnClickListener dialogOkClick = new OnClickListener() {
		public void onClick(View v) {
			if(entry!=null && verificationDialog!=null)
				entry.execute(noodleMaster);			
		}
	}; 

	/**
	 * ダイアログでキャンセルがクリックされたとき
	 */
	OnClickListener dialogCancelClick = new OnClickListener() {
		public void onClick(View v) {
			if(verificationDialog!=null)
				verificationDialog.cancel();
		}
	}; 

	/**
	 * logoボタンが押された時の動作
	 * 
	 * @param v
	 */
	public void onLogoClick(View v) {
		finish();
	}

	/**
	 * UIから登録情報を集めて返す
	 * 
	 * @return
	 */
	NoodleMaster getNoodleMaster() {
		// EditTextやRadioGroupから状態を取得
		String jancode = janText.getText().toString();
		String name = nameEdit.getText().toString();
		// 分と秒を取得
		int min = Integer.parseInt(minTextView.getText().toString());
		int sec = Integer.parseInt(secTextView.getText().toString());
		// 秒に変換
		int boilTime = min*60 + sec;
		// 画像の取得
		Bitmap image;
		if(noodleImage==null) // セットされていない場合なダミー画像を入れる
			image = BitmapFactory.decodeResource(getResources(), R.drawable.img_ramen_noimage);
		else
			image = noodleImage;
		// NoodleType noodleType =
		// NoodleType.values()[noodleTypeRadioGroup.getCheckedRadioButtonId()];
		NoodleMaster noodle = new NoodleMaster(jancode, name, image, boilTime);
		return noodle;
	}

	/**
	 * ラーメンの情報を登録する
	 * 
	 * @author leibun
	 * 
	 */
	private class EntryAsyncTask extends
			AsyncTask<NoodleMaster, Integer, Integer> {
		// 表示用にコンテキストを保持
		private Activity activity = null;
		NoodleManager nm;

		private static final int RESULT_CREATE_OK = 0; // 登録成功
		private static final int RESULT_ERROR_SQLITE = 1;// SQLITEでエラー
		private static final int RESULT_ERROR_GAE = 2; // Webへの登録でエラー

		/**
		 * コンストラクタ
		 * 
		 * @param context
		 */
		public EntryAsyncTask(Activity activity) {
			this.activity = activity;
			this.nm = new NoodleManager(activity);
		}

		/**
		 * Web登録があるので別スレッドで実行
		 * 
		 * @params params
		 */
		@Override
		protected Integer doInBackground(NoodleMaster... params) {
			try {
				// カップラーメンの情報をWebとローカルに登録
				nm.createNoodleMaster(params[0]);
			} catch (GaeException e) {
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
		protected void onPostExecute(Integer result) {
			switch (result) {
			case RESULT_CREATE_OK:
				Toast.makeText(activity, "登録完了", Toast.LENGTH_LONG).show();
				break;
			case RESULT_ERROR_GAE:
				Toast.makeText(activity, "サーバーへの登録に失敗しました", Toast.LENGTH_LONG)
						.show();
				return;
			case RESULT_ERROR_SQLITE:
				Toast.makeText(activity, "ローカルへの登録に失敗しました", Toast.LENGTH_LONG)
						.show();
				return;
			}
			// リーダーから呼び出された場合
			if (requestCode == RequestCode.READER2CREATE.ordinal()) {
				// ダイアログで選択させる
				final CharSequence[] items = { "タイマーを起動", "ダッシュボードに戻る" };
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setTitle("タイマーを動かしますか？");
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						if (item == 0)
							callTimerActivity();
						activity.finish();
					}
				});
				builder.show();

				// タイマーから呼び出された場合
			} else if (requestCode == RequestCode.TIMER2CREATE.ordinal()) {
				activity.finish();
			}
		}
	}

}
