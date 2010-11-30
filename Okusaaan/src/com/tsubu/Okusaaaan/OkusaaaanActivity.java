package com.tsubu.Okusaaaan;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * simejiマッシュルーム ＼ｵｸｻｰﾝ／
 * @author miguse(@miguse)
 *
 */
public class OkusaaaanActivity extends Activity {
	
	private static final String	ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";	//simejiから起動
	private static final String	REPLACE_KEY = "replace_key";			//未確定文字列
	private static final String	OKUSAN_STRING = "＼ｵｸｻｰﾝ／";				//＼ｵｸｻｰﾝ／
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //自分自身のインテントを取得し、どのアクションで呼び出されたかを
        //取得する
        Intent	it = getIntent();
        String action = it.getAction();
        
        //simejiから呼び出されたかどうかを判定する
        if((null != action) && (true == ACTION_INTERCEPT.equals(action))) {
        	//simejiから呼び出されたとき、未確定文字列の後に「＼ｵｸｻｰﾝ／ 」を
        	//追加する
        	StringBuffer	sb = new StringBuffer();
        	sb.append(it.getStringExtra(REPLACE_KEY));	//未確定文字列を取得する

        	//未確定文字列が存在する場合は、未確定文字列の後に半角スペースを挿入する
        	if(0 < sb.length()) {
        		sb.append(" ");
        	}
        	//＼ｵｸｻｰﾝ／
        	sb.append(OKUSAN_STRING);
        	//最後に半角スペースを挿入する
        	sb.append(" ");

        	//追加した文字列をsimejiに渡す
        	Intent data = new Intent();
        	data.putExtra(REPLACE_KEY, sb.toString());
        	setResult(RESULT_OK, data);
        }
        //アプリを終了する
        finish();
    }
}
