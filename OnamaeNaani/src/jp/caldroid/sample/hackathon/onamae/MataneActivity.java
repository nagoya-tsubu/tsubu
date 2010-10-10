package jp.caldroid.sample.hackathon.onamae;

import android.os.Bundle;
import android.view.View;

/**
 * 「またね」画面
 * @author k-matsuda
 *
 */
public class MataneActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_matane);
		
		startAnimationTaiyoKumo();
	}
	
	/**
	 * 猫クリックイベント
	 * @param v
	 */
	public void onClickCat(View v ) {
		//猫をクリックしたら、アプリケーションを終了する
		finish();
	}
	
	/**
	 * 「またね」クリックイベント
	 * @param v
	 */
	public void onClickMatane(View v) {
		//「またね」をクリックしたら、アプケーションを終了する
		finish();
	}
}
