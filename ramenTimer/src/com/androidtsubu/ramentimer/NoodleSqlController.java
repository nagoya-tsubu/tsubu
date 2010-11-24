package com.androidtsubu.ramentimer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import java.sql.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * SQLiteへの読み書きをするクラスです
 * 
 * @author hide
 */
public class NoodleSqlController {
	private static final int DB_VERSION = 8;
	private static final String NOODLEMASTERTABLENAME = "NoodleMaster";
	private static final String NOODLEHISTORYTABLENAME = "NoodleHistory";

	/** テーブルCreate文 */
	private static final String CREATE_NOODLEMASTER_TABLE = "CREATE TABLE "
			+ NOODLEMASTERTABLENAME + "(" + "jancode TEXT PRIMARY KEY,"
			+ "name TEXT ,boiltime INTEGER ,image TEXT )";

	private static final String CREATE_NOODLEHISTORY_TABLE = "CREATE TABLE "
			+ NOODLEHISTORYTABLENAME + "("
			+ "_id INTEGER PRIMARY KEY AUTOINCREMENT, jancode TEXT,"
			+ "name TEXT ,boiltime INTEGER ,measuretime TEXT)";

	/** DB読み書きクラス */
	private SQLiteDatabase database = null;
	private Context context;
	/** imageディレクトリ */
	private File directory;

	/**
	 * コンストラクタ
	 * 
	 * @param context
	 * @param directory
	 */
	public NoodleSqlController(Context context, File directory) {
		this.context = context;
		this.directory = directory;
		if (database == null) {
			DataBaseOpenHelper helper = new DataBaseOpenHelper(context);
			database = helper.getWritableDatabase();
		}
	}

	/**
	 * DBのcursorから商品マスタを生成します
	 * 
	 * @param cursor
	 * @return
	 */
	private NoodleMaster createNoodleMaster(Cursor cursor) {
		String jancode = cursor.getString(cursor.getColumnIndex("jancode"));
		String name = cursor.getString(cursor.getColumnIndex("name"));
		int boilTime = cursor.getInt(cursor.getColumnIndex("boiltime"));
		// byte[] imagebyte = cursor.getBlob(cursor
		// .getColumnIndex("image"));
		// Bitmap image = BitmapFactory.decodeByteArray(imagebyte, 0,
		// imagebyte.length);
		// 画像パス名を得る
		String filename = cursor.getString(cursor.getColumnIndex("image"));
		Bitmap image = null;
		try {
			// パス名からファイルのInputStreamを生成しBitmapにする。
			// ファイルが見つからなかった場合はそのままnullが入る
			File file = new File(directory, filename);
			image = BitmapFactory.decodeStream(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			// ファイルが見つからなかった
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return new NoodleMaster(jancode, name, image, boilTime);
	}

	/**
	 * SQLiteからJANコードで商品マスタを得ます
	 * 
	 * @param janCode
	 * @return
	 * @throws SQLException
	 */
	public NoodleMaster getNoodleMaster(String janCode) throws SQLException {
		String[] columns = { "jancode", "name", "boiltime", "image" };
		String where = "jancode = ?";
		String[] args = { janCode };
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(NOODLEMASTERTABLENAME, columns, where,
					args, null, null, null);
			if (cursor == null) {
				throw new SQLException("cursor is null");
			}
			while (cursor.moveToNext()) {
				// カーソルから商品マスタを生成する
				return createNoodleMaster(cursor);
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
	public List<NoodleMaster> getNoodleMasters() throws SQLException {
		List<NoodleMaster> noodleMasters = new ArrayList<NoodleMaster>();
		String[] columns = { "jancode", "name", "boiltime", "image" };
		String orderby = "jancode";
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(NOODLEMASTERTABLENAME, columns, null, null,
					null, null, orderby);
			if (cursor == null) {
				throw new SQLException("cursor is null");
			}
			while (cursor.moveToNext()) {
				// カーソルから商品マスタを生成してlistに追加する
				noodleMasters.add(createNoodleMaster(cursor));
			}
			return noodleMasters;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * SQLiteから最新の引数件数の商品履歴を得ます
	 * 
	 * @param rows
	 * @return
	 * @throws SQLException
	 */
	public List<NoodleHistory> getNoodleHistories(int rows) throws SQLException {
		List<NoodleHistory> histories = new ArrayList<NoodleHistory>();
		String[] columns = { "jancode", "name", "boiltime", "measuretime" };
		String orderby = "measuretime desc";
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(NOODLEHISTORYTABLENAME, columns, null,
					null, null, null, orderby, Integer.toString(rows));
			if (cursor == null) {
				throw new SQLException("cursor is null");
			}
			while (cursor.moveToNext()) {
				histories.add(createNoodleHistory(cursor));
			}
			return histories;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * SQLiteから指定日付から指定日付までの最新の商品履歴を得ます
	 * 
	 * @param start
	 *            end
	 * @return
	 * @throws SQLiteException
	 */
	public List<NoodleHistory> getNoodleHistories(Date start, Date end)
			throws SQLException {
		List<NoodleHistory> histories = new ArrayList<NoodleHistory>();
		String[] columns = { "jancode", "name", "boiltime", "measuretime" };
		String where = "measuretime >= ? and measuretime < ?";
		String[] whereArgs = {
				NoodleHistory.getSimpleDateFormat().format(start),
				NoodleHistory.getSimpleDateFormat().format(end) };
		String orderby = "measuretime desc";
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(NOODLEHISTORYTABLENAME, columns, where,
					whereArgs, null, null, orderby);
			if (cursor == null) {
				throw new SQLException("cursor is null");
			}
			while (cursor.moveToNext()) {
				histories.add(createNoodleHistory(cursor));
			}
			return histories;
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
	 * @throws SQLiteException
	 */
	public List<NoodleHistory> getNoodleHistories() throws SQLException {
		List<NoodleHistory> histories = new ArrayList<NoodleHistory>();
		String[] columns = { "jancode", "name", "boiltime", "measuretime" };
		String orderby = "measuretime desc";
		Cursor cursor = null;
		try {
			// 検索
			cursor = database.query(NOODLEHISTORYTABLENAME, columns, null,
					null, null, null, orderby);
			if (cursor == null) {
				throw new SQLException("cursor is null");
			}
			while (cursor.moveToNext()) {
				histories.add(createNoodleHistory(cursor));
			}
			return histories;
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
	}

	/**
	 * カーソルから履歴を作成する
	 * 
	 * @param cursor
	 * @return
	 */
	private NoodleHistory createNoodleHistory(Cursor cursor)
			throws SQLException {
		try {
			String jancode = cursor.getString(cursor.getColumnIndex("jancode"));
			String measuretimeString = cursor.getString(cursor
					.getColumnIndex("measuretime"));
			Date measuretime = null;
			try {
				measuretime = NoodleHistory.getSimpleDateFormat().parse(
						measuretimeString);
			} catch (ParseException e) {
				// 絶対にExceptionは出ない
				e.printStackTrace();
			}
			int boiltime = cursor.getInt(cursor.getColumnIndex("boiltime"));
			NoodleMaster noodleMaster = getNoodleMaster(jancode);
			return new NoodleHistory(noodleMaster, boiltime, measuretime);
		} catch (SQLiteException ex) {
			throw new SQLException(ExceptionToStringConverter.convert(ex));
		}
	}

	/**
	 * SQLiteに商品マスタを追加します
	 * 
	 * @param noodleMaster
	 * @throws SQLiteException
	 */
	public void createNoodleMater(NoodleMaster noodleMaster)
			throws SQLException {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put("jancode", noodleMaster.getJanCode());
			contentValues.put("name", noodleMaster.getName());
			contentValues.put("boiltime", noodleMaster.getTimerLimit());
			if (noodleMaster.getImage() != null) {
				// バーコードをファイル名としてファイルを作成する
				String filename = noodleMaster.getJanCode() + ".jpg";
				createImageFile(filename, noodleMaster.getImage());
				contentValues.put("image", filename);
			}
			long ret = database.insert(NOODLEMASTERTABLENAME, null,
					contentValues);
			if (ret < 0) {
				throw new SQLException("insert return value = " + ret);
			}
		} catch (SQLiteException ex) {
			throw new SQLException(ExceptionToStringConverter.convert(ex));
		}
	}

	/**
	 * Imageをfileにします
	 * 
	 * @param filename
	 * @param bitmap
	 */
	private void createImageFile(String filename, Bitmap bitmap) {
		// jpgファイルを作る
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		FileOutputStream fileOutputStream = null;
		try {
			bitmap.compress(CompressFormat.JPEG, 100, bos);
			// ファイルを書き出す
			File file = new File(directory, filename);
			fileOutputStream = new FileOutputStream(file);
			fileOutputStream.write(bos.toByteArray());
			fileOutputStream.flush();
		} catch (FileNotFoundException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
		} catch (IOException e) {
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
		} finally {
			if (fileOutputStream != null) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * 引数の商品マスタと計測時間、計測日時をもとに履歴を作成します
	 * 
	 * @param noodleMaster
	 * @param boilTime
	 * @param measureTime
	 * @throws SQLException
	 */
	public void createNoodleHistory(NoodleMaster noodleMaster, int boilTime,
			Date measureTime) throws SQLException {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put("jancode", noodleMaster.getJanCode());
			contentValues.put("name", noodleMaster.getName());
			// 実際に計測した時間を入力します
			contentValues.put("boiltime", boilTime);
			contentValues.put("measuretime", NoodleHistory
					.getSimpleDateFormat().format(measureTime));
			long ret = database.insert(NOODLEHISTORYTABLENAME, null,
					contentValues);
			if (ret < 0) {
				throw new SQLException("insert return value = " + ret);
			}
		} catch (SQLiteException ex) {
			throw new SQLException(ExceptionToStringConverter.convert(ex));
		}
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
		public void onCreate(SQLiteDatabase db) {
			// NoodleMasterテーブルを作成する
			db.execSQL(CREATE_NOODLEMASTER_TABLE);
			// NoodleHistoryテーブルを作成する
			db.execSQL(CREATE_NOODLEHISTORY_TABLE);
		}

		/**
		 * 存在するデータベースと定義しているバージョンが異なる
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// テーブルを削除する
			StringBuilder builder = new StringBuilder("DROP TABLE ");
			builder.append(NOODLEMASTERTABLENAME);
			db.execSQL(builder.toString());
			builder = new StringBuilder("DROP TABLE ");
			builder.append(NOODLEHISTORYTABLENAME);
			db.execSQL(builder.toString());
			// テーブルを定義しなおす
			onCreate(db);
		}

	}
}
