package com.androidtsubu.ramentimer;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class HistoryActivity extends ListActivity {


	// 履歴情報のリスト
	private List<NoodleHistory> list = new ArrayList<NoodleHistory>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);

		// 履歴の呼び出し
		NoodleManager manager = new NoodleManager(this);
		try {
			list = manager.getNoodleHistories();
		} catch (SQLException e) {
			Toast.makeText(this, ExceptionToStringConverter.convert(e),
					Toast.LENGTH_LONG).show();
		}

		if (list != null) {
			// RamenListItemAdapterを生成
			RamenListItemAdapter adapter;
			adapter = new RamenListItemAdapter(this, 0, list);
			setListAdapter(adapter);
		}
	}

	/**
	 * リストがクリックされた時の動作
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
		
		//押されたListItemに対応するNoodleMasterを取得
		NoodleHistory nh = list.get(position);
		NoodleMaster nm = nh.getNoodleMaster();
		//タイマーを起動
		Intent intent = new Intent(HistoryActivity.this, TimerActivity.class);
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, RequestCode.CREATE2TIMER.ordinal());
		intent.putExtra(TimerActivity.KEY_NOODLE_MASTER, nm);
		//履歴も渡す @hideponm
		intent.putExtra(TimerActivity.KEY_NOODLE_HISTORY, nh);
		startActivityForResult(intent, RequestCode.CREATE2TIMER.ordinal());
		HistoryActivity.this.finish();
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
			View view = mInflater.inflate(R.layout.list_item_ramen, null);
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
			// 時間をセット
			TextView boilTime = (TextView) view.findViewById(R.id.BoilingTime);
			boilTime.setText(item.getBoilTimeString());
			// 日付をセット
			TextView date;
			date = (TextView) view.findViewById(R.id.date);
			SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH時mm分");
			date.setText(format.format(item.getMeasureTime()));
			// 初期値は不可視（GONE）なので見えるように変更
			date.setVisibility(TextView.VISIBLE);
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
}
