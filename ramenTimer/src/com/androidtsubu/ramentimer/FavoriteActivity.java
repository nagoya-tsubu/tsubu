package com.androidtsubu.ramentimer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class FavoriteActivity extends ListActivity {

	// 検索用EditText
	EditText searchEdit = null;
	// タイトルテキストビュー
	TextView titleText = null;
	//リストビュー
	ListView listView;
	//空っぽ時に表示するTextView
	TextView emptyFavoriteText;
	// 登録情報のリスト
	private List<NoodleMaster> list = new ArrayList<NoodleMaster>();
	private ProgressDialog dialog;
	private NoodleManager manager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorite);
		searchEdit = (EditText)findViewById(R.id.SearchBarcodeEdit);
		// Viewの取得
		//searchEdit = (EditText) findViewById(R.id.title_edit);
		titleText = (TextView) findViewById(R.id.title_text);
		emptyFavoriteText = (TextView)findViewById(R.id.TextViewEmptyFavorite);

		// 登録情報の呼び出し
		manager = new NoodleManager(this);
		SearchTask task = new SearchTask();
		task.execute("");
	}
	
	/**
	 * 描画する
	 */
	private void draw(){
		if (list != null && list.size() > 0) {
			getListView().setVisibility(View.VISIBLE);
			emptyFavoriteText.setVisibility(View.GONE);
			// RamenListItemAdapterを生成
			RamenListItemAdapter adapter;
			adapter = new RamenListItemAdapter(this, 0, list);
			setListAdapter(adapter);
			return;
		}
		getListView().setVisibility(View.GONE);
		emptyFavoriteText.setVisibility(View.VISIBLE);
	}

	/**
	 * リストがクリックされた時の動作
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);

		// 押されたListItemに対応するNoodleMasterを取得
		NoodleMaster nm = list.get(position);
		// タイマーを起動
		Intent intent = new Intent(this, TimerActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.FAVORITE2TIMER.ordinal());
		intent.putExtra(TimerActivity.KEY_NOODLE_MASTER, nm);
		// //履歴も渡す @hideponm
		// intent.putExtra(TimerActivity.KEY_NOODLE_HISTORY, nh);
		startActivityForResult(intent, RequestCode.FAVORITE2TIMER.ordinal());
	}

	/**
	 * アクティビティの実行結果処理
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {

		// アクティビティのリクエストコードで処理を分ける
		if (requestCode == RequestCode.FAVORITE2TIMER.ordinal()) {
			if (RESULT_OK == resultCode) {
				setResult(RESULT_OK, intent);
				// Intentをダッシュボードまで戻す。
				// 呼び出したインテントが空の場合は、処理を終了する
				finish();
			}
		}
	}

	/**
	 * リストアイテムを扱うためのアダプタークラス
	 * 
	 * @author leibun
	 * 
	 */
	public class RamenListItemAdapter extends ArrayAdapter<NoodleMaster> {
		private LayoutInflater mInflater;
		private View mViews[];
		private Bitmap noImage = null;

		/**
		 * コンストラクタ
		 * 
		 * @param context
		 * @param rid
		 * @param list
		 */
		public RamenListItemAdapter(Context context, int rid,
				List<NoodleMaster> list) {
			super(context, rid, list);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// Viewの入れものを作っておく
			mViews = new View[list.size()];
			for (int i = 0; i < list.size(); i++)
				mViews[i] = null;
			// 空のときの画像をロード
			noImage = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.img_ramen_noimage);

		}

		/**
		 * リストのアイテムを表示する部分
		 * 
		 * @param position
		 * @param convertView
		 * @param parent
		 */
		public View getView(int position, View convertView, ViewGroup parent) {
			if (null == mViews[position]) {
				NoodleMaster item = (NoodleMaster) getItem(position);
				mViews[position] = getView(item);
			}
			return mViews[position];
		}

		/**
		 * NoodleMasterからViewを作る関数
		 * 
		 * @param item
		 * @return
		 */
		private View getView(NoodleMaster item) {
			// レイアウトファイルからViewを生成
			View view = mInflater.inflate(R.layout.list_item_ramen, null);
			// 画像をセット
			ImageView image;
			image = (ImageView) view.findViewById(R.id.NoodleImage);
			if (item.getImage() != null)
				image.setImageBitmap(item.getImage());
			else
				// 空のとき
				image.setImageBitmap(noImage);
			// カップラーメンの名前をセット
			TextView name;
			name = (TextView) view.findViewById(R.id.RamenName);
			name.setText(item.getName());
			// Janコードをセット
			TextView jancode;
			jancode = (TextView) view.findViewById(R.id.JanText);
			jancode.setText("" + item.getJanCode());
			// 時間をセット
			TextView boilTime = (TextView) view.findViewById(R.id.BoilingTime);
			boilTime.setText(item.getTimerLimitString(
					getString(R.string.min_unit), getString(R.string.sec_unit)));
			return view;
		}

	}

	/**
	 * logoボタンが押された時の動作
	 * 
	 * @param v
	 */
	public void onLogoClick(View v) {
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
				RequestCode.ACTION＿TIMER.ordinal());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * アクションバーの読込みボタンが押されたとき
	 */
	public void onReaderButtonClick(View v) {
		Intent intent = new Intent();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.ACTION_READER.ordinal());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * アクションバーの検索ボタンが押されたとき
	 * これはleibunが作ったバージョン。
	 * 実際使う時まで置いておく
	 */
	public void onSearchButtonClick(View v) {
		if (searchEdit.getVisibility() == View.GONE) {
			searchEdit.setVisibility(View.VISIBLE);
			titleText.setVisibility(View.GONE);
		} else {
			searchEdit.setVisibility(View.GONE);
			titleText.setVisibility(View.VISIBLE);
			// ソフトウェアキーボードを非表示にする
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}
	}
	
	public void onSearchClick(View v){
		// ソフトウェアキーボードを非表示にする
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
		//検索文字列で検索する
		String key = searchEdit.getText().toString();
		SearchTask task = new SearchTask();
		task.execute(key);
	}
	
	/**
	 * 検索中ダイアログを作成する
	 */
	private void showSearchDialog(){
		dialog = new CustomProgressDialog(this,R.style.CustomDialog);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.progress_icon));
		dialog.setTitle(getResources().getString(R.string.search_searching));
		dialog.show();
	}

	/**
	 * 検索中ダイアログを消去する
	 */
	private void dismissSearchDialog(){
		if(dialog != null){
			dialog.dismiss();
		}
	}
	
	
	/**
	 * 検索用非同期Task
	 * @author morikawa
	 *
	 */
	private class SearchTask extends AsyncTask<String, Void, List<NoodleMaster>>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			showSearchDialog();
		}

		@Override
		protected void onPostExecute(List<NoodleMaster> result) {
			// TODO Auto-generated method stub
			dismissSearchDialog();
			list = result;
			draw();
		}

		@Override
		protected List<NoodleMaster> doInBackground(String... arg0) {
//			try {
//				Thread.sleep(5000);
//			} catch (InterruptedException e1) {
//				// TODO Auto-generated catch block
//				e1.printStackTrace();
//			}
			try {
				if(arg0[0] == null || arg0[0].equals("")){
					//べたでひっぱってくる
					return manager.getNoodleMastersForSqlite();
				}
				//キーで検索
				return manager.searchNoodleMaster(arg0[0]);
			} catch (SQLException e) {
				Toast.makeText(FavoriteActivity.this, R.string.search_alert, Toast.LENGTH_LONG).show();
				return null;
			}
		}
	}


}
