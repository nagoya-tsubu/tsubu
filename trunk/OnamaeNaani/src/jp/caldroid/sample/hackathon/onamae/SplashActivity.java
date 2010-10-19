package jp.caldroid.sample.hackathon.onamae;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

public class SplashActivity extends Activity {
	
	/** タイトルログアニメーション開始 */
	private static final int MSG_TITLE_LOGO_START_ANIMATION = 1;
	/** 「はじめる」画面に遷移 */
	private static final int MSG_GO_MAIN = 2;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			switch (msg.what) {
			case MSG_TITLE_LOGO_START_ANIMATION:
				
				if(mTitleLogo != null) {
					try {
//						mTitleLogo.startAnimation(OnamaeAnimUtil.createSplashLogoAnimation());
						mTitleLogo.startAnimation(AnimationUtils.loadAnimation(SplashActivity.this, R.anim.splash_logo));
					} catch (Exception e) {
						// 何もしない
					}
				}				
				break;
				
			case MSG_GO_MAIN:
				
				try {
					Intent intent = new Intent(getApplicationContext(), HajimeruActivity.class);
//					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				} catch (Exception e) {
					Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
				}
				finish();
				
				break;

			default:
				break;
			}
			
		};
	};
	
	/** タイトルロゴ View */
	private View mTitleLogo = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        // タイトルロゴ view を取得
        mTitleLogo = findViewById(R.id.TitleLogo);
        
        mHandler.sendEmptyMessageDelayed(MSG_TITLE_LOGO_START_ANIMATION, 1500);
        
        // 自動的に次の画面に遷移する
        mHandler.sendEmptyMessageDelayed(MSG_GO_MAIN, 6500);
        
        
    }
    
    @Override
    protected void onStop() {
    	super.onStop();
    	if(mHandler != null)
    		mHandler.removeMessages(MSG_GO_MAIN);
    }
    
    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if(mHandler != null)
    		mHandler.removeMessages(MSG_GO_MAIN);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
        	if(mHandler != null) {
        		mHandler.removeMessages(MSG_GO_MAIN);
        	}
        	return true;
    	}
    	
    	return super.onKeyDown(keyCode, event);
    }
    
    /**
     * フレームクリックイベント
     * 画面をクリックしたら次の画面に遷移する
     * @param v
     */
    public void onClickFrame(View v) {
    	mHandler.removeMessages(MSG_GO_MAIN);
    	mHandler.sendEmptyMessage(MSG_GO_MAIN);
    }
    
}