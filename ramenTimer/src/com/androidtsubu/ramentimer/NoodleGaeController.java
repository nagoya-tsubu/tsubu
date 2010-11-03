package com.androidtsubu.ramentimer;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @gabuさん作成GAEのAPIを使ってGAEへの読み書きをするクラスです
 * @author hide
 */
public class NoodleGaeController {
	/** Google App Engine のアドレス */
	private static String address = " http://ramentimer.appspot.com/api/ramens";

	/**
	 * JANコードを引数にGAEから商品マスタ形を得る
	 * 
	 * @param janCode
	 * @return
	 * @throws GaeException
	 */
	public NoodleMaster getNoodleMaster(String janCode) throws GaeException {
		// HTTPクライアントを生成
		HttpClient client = new DefaultHttpClient();
		// HTTPパラメーターを取得
		HttpParams httpParams = client.getParams();
		// HTTPタイムアウトを設定
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(httpParams, "Shift_Jis");
		HttpProtocolParams.setHttpElementCharset(httpParams, "Shift_JIS");
		BufferedReader reader = null;

		StringBuffer getString = new StringBuffer();
		StringBuffer request = new StringBuffer(address);
		request.append("/show?jan=");
		request.append(janCode);
		try {
			HttpGet httpGet = new HttpGet(getString.toString());
			HttpResponse httpResponse = client.execute(httpGet);
			if (httpResponse.getStatusLine().getStatusCode() < 400) {
				// レスポンスのContentStreamを読み出すBufferedReaderを生成する
				reader = new BufferedReader(new InputStreamReader(httpResponse
						.getEntity().getContent(), "Shift_Jis"));
				// 結果文字列を溜め込むStringBuilderを生成する
				StringBuilder builder = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
				// 正常な結果が返ってきたので商品マスタを生成する
				return createNoodleMaster(builder.toString());
			}
		} catch (ClientProtocolException e) {
			throw new GaeException(e);
		} catch (IOException exception) {
			throw new GaeException(exception);
		}
		return null;
	}

	/**
	 * 文字列から商品マスタを生成する
	 * 
	 * @param string
	 * @return
	 */
	private NoodleMaster createNoodleMaster(String string) {
		try {
			JSONObject jsonObject = new JSONObject(string);
			int boilTime = jsonObject.getInt("boilTime");
			String name = jsonObject.getString("name");
			String janCode = jsonObject.getString("jan");
			String imageUrl = jsonObject.getString("imageUrl");
			Bitmap image = getBitmap(imageUrl);

			return new NoodleMaster(janCode, name, image, boilTime,
					NoodleType.UNKNOWN);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * URLから画像を取得する
	 * 
	 * @param imageUrl
	 * @return
	 * @throws IOException
	 */
	private Bitmap getBitmap(String imageUrl) {
		try {
			URL url = new URL(address + imageUrl);
			InputStream input = url.openStream();
			try {
				byte[] buf = new byte[102400];
				int len = 0;
				len = input.read(buf);
				return BitmapFactory.decodeByteArray(buf,0,len);
			} finally {
				input.close();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * GAEに商品マスタを追加作成します
	 * 
	 * @param noodleMaster
	 * @throws GaeException
	 */
	public void create(NoodleMaster noodleMaster) throws GaeException {
		return;
	}

}
