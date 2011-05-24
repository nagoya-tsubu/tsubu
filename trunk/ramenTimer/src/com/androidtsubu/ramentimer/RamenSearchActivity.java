/**
 * 
 */
package com.androidtsubu.ramentimer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * @author hide
 * 商品検索登録Activity
 */
public class RamenSearchActivity extends Activity {
	/**JANコードのEditText*/
	private EditText editTextJancode;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ramensearch);
		editTextJancode = (EditText)findViewById(R.id.SearchBarcodeEdit);
	}
	
	/**
	 * 検索ボタンクリック
	 * @param v
	 */
	public void onSearchClick(View v){
		String jancode = editTextJancode.getText().toString();
		if(jancode == null || jancode.equals("")){
			//バーコードを入力するように促す
			Toast.makeText(this, R.string.ramensearch_jancodealert, Toast.LENGTH_LONG).show();
			return;
		}
		//バーコードをReadeActivityに渡す
		Intent intent = new Intent(this,ReaderActivity.class);
		intent.putExtra(ReaderActivity.KEY_JANCODE, jancode);
		int requestCode = RequestCode.RAMENSEARCH2READER.ordinal();
		intent.putExtra(RequestCode.KEY_RESUEST_CODE, requestCode);
		startActivityForResult(intent, requestCode);
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
	
	/**
	 * HOMEロゴクリック
	 * @param v
	 */
	public void onLogoClick(View v){
		finish();
	}

	
	
	
}
