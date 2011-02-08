package jp.mrshiromi.net.onamaenaani;

import android.os.Bundle;
import android.view.View;

/**
 * 「ひとやすみ」画面
 * @author k-matsuda
 *
 */
public class HitoyasumiActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_hitoyasumi);
		
		startAnimationTaiyoKumo();
	}
	
	/**
	 * 猫クリックイベント
	 * @param v
	 */
	public void onClickCat(View v ) {

		setResult(RESULT_OK);
		finish();
	}

	/**
	 * 「ひとやすみ」クリックイベント
	 * @param v
	 */
	public void onClickHitoyasumi(View v) {
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void onClickTaiyo(View v) {
		setResult(RESULT_CANCELED);
		super.onClickTaiyo(v);
	}
}
