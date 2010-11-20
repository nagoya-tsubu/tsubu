package com.androidtsubu.ramentimer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class HistoryActivity extends Activity {
	//履歴情報のリスト
	private List<NoodleHistory> list = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_activity);

		// 履歴の呼び出し
		NoodleManager manager = new NoodleManager(this);
		// list = manager.getNoodleHistories();
		//とりあえずダミーリストいれておく
		list = dummyList();


		if (list != null) {
			// RamenListItemAdapterを生成
			RamenListItemAdapter adapter;
			adapter = new RamenListItemAdapter(this, 0, list);
			// ListViewにListItemAdapterをセット
			ListView listView = (ListView) findViewById(R.id.RamenList);
			listView.setOnItemClickListener(new HistoryItemClick());
			listView.setAdapter(adapter);
		}
	}
	
	/**
	 * リストがクリックされた時の動作
	 */
	class HistoryItemClick implements OnItemClickListener
	{
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			NoodleHistory nm = list.get(position);
			Toast.makeText(HistoryActivity.this,position+"", Toast.LENGTH_SHORT)
			.show();
			Log.i("HistoryActivity","position:"+position);
		}
	};
	
	/**
	 * ダミーデータ
	 * @return
	 */
	private List<NoodleHistory> dummyList(){
		Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
		List<NoodleHistory> list = new ArrayList<NoodleHistory>();
		for(int i=0;i<10;++i){
			NoodleMaster nm = new NoodleMaster("4903085060531", "〇〇ラーメン", img, 180);
			NoodleHistory nh = new NoodleHistory(nm, new Date() );
			list.add(nh);
		}
		return list;
	}

	/**
	 * リストアイテムを扱うためのアダプタークラス
	 * 
	 * @author leibun
	 * 
	 */
	public class RamenListItemAdapter extends ArrayAdapter<NoodleHistory> {
		private LayoutInflater mInflater;

		public RamenListItemAdapter(Context context, int rid,
				List<NoodleHistory> list) {
			super(context, rid, list);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// データを取り出す
			NoodleHistory item = (NoodleHistory) getItem(position);
			// レイアウトファイルからViewを生成
			View view = mInflater.inflate(R.layout.ramen_list_item, null);
			// 画像をセット
			ImageView image;
			image = (ImageView) view.findViewById(R.id.NoodleImage);
			image.setImageBitmap(item.getImage());
			// カップラーメンの名前をセット
			TextView name;
			name = (TextView) view.findViewById(R.id.RamenName);
			name.setText(item.getName());
			// Janコードをセット
			TextView jancode;
			jancode = (TextView) view.findViewById(R.id.JanText);
			jancode.setText("" + item.getJanCode());

			// 日付部分は回数と共有なのでViewStubで実装されてるので置き換える
			ViewStub stub = (ViewStub) view.findViewById(R.id.DateOrNumber);
			stub.setLayoutResource(R.layout.history_date);
			View inflated = stub.inflate();
			TableRow TableRowDate = (TableRow) inflated;

			// 日付をセット
			TextView date;
			date = (TextView) TableRowDate.findViewById(R.id.Date);
			date.setText(item.getMeasureTime().toString());

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
				RequestCode.ACTION_HISTORY.ordinal());
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * バーコードリーダーを起動し、Timerを終了する
	 */
	public void onReaderButtonClick(View v) {
		Intent intent = new Intent(this, ReaderActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE,
				RequestCode.DASHBORAD2READER.ordinal());
		startActivity(intent);
		finish();
	}
}
