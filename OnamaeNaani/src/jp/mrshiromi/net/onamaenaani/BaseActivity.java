package jp.mrshiromi.net.onamaenaani;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
//import android.widget.Toast;

/**
 * 共通
 * 
 * @author Twitter@kouichi_matsuda
 * 
 */
public class BaseActivity extends Activity {

	/**
	 * 太陽クリックイベント
	 * 
	 * @param v
	 */
	public void onClickTaiyo(View v) {

		Intent intent = new Intent(this, MataneActivity.class);
		startActivity(intent);
		finish();

	}

	/**
	 * ハードウェアキーイベント
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		// バックボタン無効
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (this.getClass() != MataneActivity.class) {
//				Toast.makeText(this, "太陽をタッチしてね", Toast.LENGTH_LONG).show();
				return true;
			} else {
				finish();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 太陽と雲のアニメーションを開始する
	 */
	protected void startAnimationTaiyoKumo() {
		View taiyo = findViewById(R.id.ImageViewTaiyo);
		if (taiyo != null)
			taiyo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.taiyo));
		View kumo = findViewById(R.id.ImageViewKumo);
		if (kumo != null)
			kumo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.kumo));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		//ハードウェアキーによる音量設定を有効にする
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

}
