package com.android.tsubu.oryaaaa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * simejiマッシュルーム (「・ω・)「がおー
 * 
 * @author TAN(@tan1234jp)
 * 
 *         改変版 simejiマッシュルーム （ﾉ≧∇≦）ﾉ ﾐ ┸┸おりゃー
 * @author 弘味(@mrshiromi)
 * 
 */
public class Activity_Oryaaaa extends Activity {

    private static final String     ACTION_INTERCEPT = "com.adamrocker.android.simeji.ACTION_INTERCEPT";    //simejiから起動
    private static final String     REPLACE_KEY = "replace_key";                    //未確定文字列
    private static final String     GAOH_STRING = "（ﾉ≧∇≦）ﾉ ﾐ ┸┸おりゃー";             //（ﾉ≧∇≦）ﾉ ﾐ ┸┸おりゃー

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //自分自身のインテントを取得し、どのアクションで呼び出されたかを
        //取得する
        Intent  it = getIntent();
        String action = it.getAction();
        
        //simejiから呼び出されたかどうかを判定する
        if((null != action) && (true == ACTION_INTERCEPT.equals(action))) {
                //simejiから呼び出されたとき、未確定文字列の後に「 （ﾉ≧∇≦）ﾉ ﾐ ┸┸おりゃー 」を
                //追加する
                StringBuffer    sb = new StringBuffer();
                sb.append(it.getStringExtra(REPLACE_KEY));      //未確定文字列を取得する

                //未確定文字列が存在する場合は、未確定文字列の後に半角スペースを挿入する
                if(0 < sb.length()) {
                        sb.append(" ");
                }
                //(「・ω・)「がおー
                sb.append(GAOH_STRING);
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