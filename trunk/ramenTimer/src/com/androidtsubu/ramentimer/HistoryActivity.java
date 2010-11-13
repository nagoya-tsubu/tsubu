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
		
		//履歴の呼び出し
		NoodleManager manager = new NoodleManager(this);
		List<NoodleHistory> list=null;
//		list = new manager.getNoodleHistories();

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
			// 日付をセット
			TextView date;
			date = (TextView) view.findViewById(R.id.Date);
			date.setText(item.getMeasureTime().toString());
			// Janコードをセット
			TextView jancode;
			jancode = (TextView) view.findViewById(R.id.JanText);
			jancode.setText("" + item.getJanCode());

			return view;
		}
	}
}