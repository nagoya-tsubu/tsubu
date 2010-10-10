package jp.caldroid.sample.hackathon.onamae;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 「はじめる」画面
 * @author k-matsuda
 *
 */
public class HajimeruActivity extends BaseActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hajimeru);
		startAnimationTaiyoKumo();
	}
	
	
	/**
	 * はじめるクリックイベント
	 * @param v
	 */
	public void onClickHajimeru(View v) {

		Intent intent = new Intent(this, PlayActivity.class);
		startActivity(intent);
		finish();
		
	}
	

	
}
