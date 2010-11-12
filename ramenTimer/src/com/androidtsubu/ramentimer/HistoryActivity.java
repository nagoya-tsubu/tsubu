package com.androidtsubu.ramentimer;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class HistoryActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.history_activity);
		
		List<NoodleMaster> list = new ArrayList<NoodleMaster>();

		// リストに入力
		for (int i = 0; i < 10; i++) {
//			NoodleMaster item = new NoodleMaster();
//			list.add(item);
		}

		// RamenListItemAdapterを生成
		RamenListItemAdapter adapter;
		adapter = new RamenListItemAdapter(this, 0, list);
		// ListViewにListItemAdapterをセット
		ListView listView = (ListView) findViewById(R.id.RamenList);
		listView.setAdapter(adapter);

	}

	/**
	 * リストアイテムを扱うためのアダプタークラス
	 * 
	 * @author leibun
	 * 
	 */
	public class RamenListItemAdapter extends ArrayAdapter<NoodleMaster> {
		private LayoutInflater mInflater;

		public RamenListItemAdapter(Context context, int rid,
				List<NoodleMaster> list) {
			super(context, rid, list);
			mInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			// データを取り出す
			NoodleMaster item = (NoodleMaster) getItem(position);
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
			// ゆで時間をセット
			TextView time;
			time = (TextView) view.findViewById(R.id.BoilingTime);
			time.setText("" + item.getTimerLimit());
			// Janコードをセット
			TextView jancode;
			jancode = (TextView) view.findViewById(R.id.JanText);
			jancode.setText("" + item.getJanCode());

			return view;
		}
	}
}
