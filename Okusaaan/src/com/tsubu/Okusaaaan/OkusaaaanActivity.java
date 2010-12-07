package com.tsubu.Okusaaaan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * simejiマッシュルーム ＼ｵｸｻｰﾝ／ & ＼ﾀﾞﾝﾅｻｰﾝ／ 
 * @author miguse(@miguse)
 *
 */
public class OkusaaaanActivity extends Activity {
	
	private static final String	REPLACE_KEY = "replace_key";			//未確定文字列
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

	//＼ｵｸｻｰﾝ／
    public void onOkusanClick(View v) {
        String result = getString(R.string.button_okusan) + " ";
        replace(result);
    }
    //＼ｵｸｻｰﾝ／
    public void onDannasanClick(View v) {
    	String result = getString(R.string.button_dannasan) + " ";
        replace(result);
    }
    
    private void replace(String result) {
        Intent data = new Intent();
        data.putExtra(REPLACE_KEY, result);
        setResult(RESULT_OK, data);
        finish();
    }
}
