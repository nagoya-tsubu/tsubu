package com.androidtsubu.ramentimer;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.DuplicateFormatFlagsException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Log;

/**
 * @gabuさん作成GAEのAPIを使ってGAEへの読み書きをするクラスです
 * @author hide
 */
public class NoodleGaeController {
	/** Google App Engine のアドレス */
	private static String address = "http://ramentimer.appspot.com/";
	/** 該当JANコードの商品がありません */
	private static final int NOT_FOUND = 404;
	/** すでに該当JANコードの商品があります */
	private static final int DUPLICATE = 400;
	private Context context;

	/**
	 * コンストラクタ
	 * @param context
	 */
	public NoodleGaeController(Context context) {
		this.context = context;
	}

	/**
	 * JANコードを引数にGAEから商品マスタを得る
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
		BufferedReader reader = null;

		StringBuffer request = new StringBuffer(address);
		request.append("api/ramens/show?jan=");
		request.append(janCode);
		try {
			HttpGet httpGet = new HttpGet(request.toString());
			HttpResponse httpResponse = client.execute(httpGet);
			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == NOT_FOUND) {
				// 該当するJANコードの商品はないのでnullを返す
				return null;
			}
			if (statusCode >= 400) {
				// 400以上の場合はエラーなのでExceptionを投げる
				throw new GaeException("Status Error = "
						+ Integer.toString(statusCode));
			}
			// 正常返答レスポンスのContentStreamを読み出すBufferedReaderを生成する
			reader = new BufferedReader(new InputStreamReader(httpResponse
					.getEntity().getContent(), "UTF-8"));
			// 結果文字列を溜め込むStringBuilderを生成する
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			// 正常な結果が返ってきたので商品マスタを生成する
			return createNoodleMaster(builder.toString());
		} catch (ClientProtocolException e) {
			throw new GaeException(e);
		} catch(SocketTimeoutException e){
			throw new GaeException(e);
		} catch (IOException exception) {
			throw new GaeException(exception);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
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

			return new NoodleMaster(janCode, name, image, boilTime);
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
		URL url;
		InputStream input = null;
		try {
			url = new URL(address + imageUrl);
			input = url.openStream();
			return BitmapFactory.decodeStream(input);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}finally{
			if(input != null){
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * GAEに商品マスタを追加作成します
	 * 
	 * @param noodleMaster
	 * @throws DuplexNoodleMasterException
	 * @throws GaeException
	 */
	public void create(NoodleMaster noodleMaster)
			throws DuplexNoodleMasterException, GaeException {
		// HTTPクライアントを生成
		HttpClient client = new DefaultHttpClient();
		// HTTPパラメーターを取得
		HttpParams httpParams = client.getParams();
		// HTTPタイムアウトを設定
		HttpConnectionParams.setSoTimeout(httpParams, 10000);
		BufferedReader reader = null;
		InputStream imageInputStream = null; 
		// GAEへpost
		HttpPost httpPost = new HttpPost(address + "api/ramens/create");

		try {
			MultipartEntity entity = new MultipartEntity();
			// 名称
			entity.addPart("name", new StringBody(noodleMaster.getName()));
			// 商品名
			entity.addPart("jan", new StringBody(noodleMaster.getJanCode()));
			// 茹で時間
			entity.addPart("boilTime",
					new StringBody(noodleMaster.getTimerLimitString()));
			imageInputStream = createImageInputStream(noodleMaster
					.getImage());
			// イメージ画像
			entity.addPart(
					"image",
					new InputStreamBody(imageInputStream, "filename"));

			httpPost.setEntity(entity);

			HttpResponse httpResponse;
			httpResponse = client.execute(httpPost);

			int statusCode = httpResponse.getStatusLine().getStatusCode();
			if (statusCode == DUPLICATE) {
				// 重複エラーを返す
				throw new DuplexNoodleMasterException();
			}
			if (statusCode > 400) {
				// 400以上の場合はエラーなのでExceptionを投げる
				throw new GaeException("Status Error = "
						+ Integer.toString(statusCode));
			}
			// レスポンスのContentStreamを読み出すBufferedReaderを生成する
			reader = new BufferedReader(new InputStreamReader(httpResponse
					.getEntity().getContent(), "UTF-8"));
			// 結果文字列を溜め込むStringBuilderを生成する
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			System.out.println(builder.toString());
		} catch (UnsupportedEncodingException exception) {
			throw new GaeException(exception);
		} catch (ClientProtocolException e) {
			throw new GaeException(e);
		} catch (IOException e) {
			throw new GaeException(e);
		}finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(imageInputStream != null){
				try {
					imageInputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Bitmapからファイルを作成しファイルのInputStreamを返す
	 * 
	 * @param bitmap
	 * @return inputstream
	 */
	private InputStream createImageInputStream(Bitmap bitmap) {
		// jpgファイルを作る
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FileOutputStream fileOutputStream = null;
		try {
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			// ファイルを書き出す
			File file = new File(NoodleManager.SAVE_IMAGE_DIRECTORY,"tmp.jpg");
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(bos.toByteArray());
			fileOutputStream.flush();
			// 作成したファイルのInputStreamを得る
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Log.d("err", e.getMessage(), e);
		} catch (IOException e) {
			Log.d("err", e.getMessage(), e);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.d("err", e.getMessage(), e);
				}
			}
			if(bos != null){
				try {
					bos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return null;
	}

}
