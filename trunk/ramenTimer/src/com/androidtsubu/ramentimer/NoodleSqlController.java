package com.androidtsubu.ramentimer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.sql.SQLException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

/**
 * SQLiteへの読み書きをするクラスです
 * 
 * @author hide
 */
public class NoodleSqlController {
	private static final int DB_VERSION = 1;
	private static final String NOODLEMASTERTABLENAME = "NoodleMaster";
	private static final String NOODLEHISTORYTABLENAME = "NoodleHistory";

	/** テーブルCreate文 */
	private static final String CREATE_NOODLEMASTER_TABLE = "CREATE TABLE "
			+ NOODLEMASTERTABLENAME + "(" + "jancode TEXT PRIMARY KEY,"
			+ "name TEXT ," + "boilTime INTEGER" + "image BLOB)";
	private static final String CREATE_NOODLEHISTORY_TABLE = "CREATE TABLE "
			+ NOODLEHISTORYTABLENAME + "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, jancode TEXT,"
			+ "name TEXT ," + "measuretime TEXT)";

	/** DB読み書きクラス */
	private SQLiteDatabase database = null;

	/**
	 * コンストラクタ
	 * 
	 * @param context
	 */
	public NoodleSqlController(Context context) {
		if (database == null) {
			DataBaseOpenHelper helper = new DataBaseOpenHelper(context);
			database = helper.getWritableDatabase();
		}
	}

	/**
	 * SQLiteからJANコードで商品マスタを得ます
	 * 
	 * @param janCode
	 * @return
	 * @throws SQLException
	 */
	public NoodleMaster getNoodleMaster(String janCode){
		String[] columns = { "jancode", "name", "boiltime" };
		String where = "jancode = ?";
		String[] args = { janCode };
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(NOODLEMASTERTABLENAME, columns, where,
					args, null, null, null);
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex("name"));
				int boilTime = cursor.getInt(cursor.getColumnIndex("boiltime"));
				byte[] imagebyte = cursor.getBlob(cursor
						.getColumnIndex("boiltime"));
				Bitmap image = BitmapFactory.decodeByteArray(imagebyte, 0,
						imagebyte.length);
				return new NoodleMaster(janCode, name, image, boilTime);
			}
			return null;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	/**
	 * SQLiteからすべての商品マスタを得ます
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<NoodleMaster> getNoodleMasters() {
		List<NoodleMaster> noodleMasters = new ArrayList<NoodleMaster>();
		String[] columns = { "jancode", "name", "boiltime" };
		String orderby = "jancode";
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(NOODLEMASTERTABLENAME, columns, null, null,
					null, null, orderby);
			while (cursor.moveToNext()) {
				String jancode = cursor.getString(cursor
						.getColumnIndex("jancode"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				int boilTime = cursor.getInt(cursor.getColumnIndex("boiltime"));
				byte[] imagebyte = cursor.getBlob(cursor
						.getColumnIndex("image"));
				Bitmap image = BitmapFactory.decodeByteArray(imagebyte, 0,
						imagebyte.length);
				noodleMasters.add(new NoodleMaster(jancode, name, image,
						boilTime));
			}
			return noodleMasters;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

	}

	/**
	 * SQLiteからすべての商品履歴を得ます
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<NoodleHistory> getNoodleHistories() {
		List<NoodleHistory> histories = new ArrayList<NoodleHistory>();
		String[] columns = { "jancode", "name", "boiltime","measuretime" };
		String orderby = "measuretime";
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(NOODLEHISTORYTABLENAME, columns, null, null,
					null, null, orderby);
			while (cursor.moveToNext()) {
				String jancode = cursor.getString(cursor
						.getColumnIndex("jancode"));
				String measuretimeString  = cursor.getString(cursor.getColumnIndex("measuretime"));
				Date measuretime = null;
				try {
					measuretime = NoodleHistory.getSimpleDateFormat().parse(measuretimeString);
				} catch (ParseException e) {
					//絶対にExceptionは出ない
					e.printStackTrace();
				}
				NoodleMaster noodleMaster = getNoodleMaster(jancode);
				
				histories.add(new NoodleHistory(noodleMaster,measuretime));
			}
			return histories;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * SQLiteに商品マスタを追加作成します
	 * 
	 * @param noodleMaster
	 */
	public void createNoodleMater(NoodleMaster noodleMaster){
		ContentValues contentValues = new ContentValues();
		contentValues.put("jancode", noodleMaster.getJanCode());
		contentValues.put("name", noodleMaster.getName());
		contentValues.put("boiltime", noodleMaster.getTimerLimit());
		if(noodleMaster.getImage() != null){
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			noodleMaster.getImage().compress(CompressFormat.JPEG, 100, bos);
			contentValues.put("image", bos.toByteArray());
		}
		database.insert(NOODLEMASTERTABLENAME, null, contentValues);
	}

	/**
	 * 引数の商品マスタと計測時間をもとに履歴を追加します
	 * @param noodleMaster
	 * @param measureTime
	 */
	public void createNoodleHistory(NoodleMaster noodleMaster, Date measureTime) {
		ContentValues contentValues = new ContentValues();
		contentValues.put("jancode", noodleMaster.getJanCode());
		contentValues.put("name", noodleMaster.getName());
		contentValues.put("measuretime", NoodleHistory.getSimpleDateFormat().format(measureTime));
		database.insert(NOODLEMASTERTABLENAME, null, contentValues);
	}

	/**
	 * DataBaseOpenHelper
	 * 
	 * @author hide
	 */
	private class DataBaseOpenHelper extends SQLiteOpenHelper {
		/**
		 * コンストラクタ
		 * 
		 * @param context
		 * @param factory
		 * @param version
		 */
		public DataBaseOpenHelper(Context context) {
			super(context, "RamenTimer.db", null, DB_VERSION);
		}

		/**
		 * データベースが新規に作成された
		 */
		@Override
		public void onCreate(SQLiteDatabase database) {
			// NoodleMasterテーブルを作成する
			database.execSQL(CREATE_NOODLEMASTER_TABLE);
			// NoodleHistoryテーブルを作成する
			database.execSQL(CREATE_NOODLEHISTORY_TABLE);
		}

		/**
		 * 存在するデータベースと定義しているバージョンが異なる
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}
}
