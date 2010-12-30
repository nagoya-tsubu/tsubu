package com.androidtsubu.ramentimer;

import java.io.File;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Environment;


/**
 * データの読み書きをするマネージャーです
 * 
 * @author hide
 * 
 */
public class NoodleManager {
	/** SQLite読み書きクラス */
	private NoodleSqlController noodleSqlController;
	/** GAE読み書きクラス */
	private NoodleGaeController noodleGaeController;
	private Context context;
	/** image画像保存ディレクトリ */
	public static File SAVE_IMAGE_DIRECTORY;
	/**NoodleManager*/
	private static NoodleManager noodleManager = null;
	
	
	
	/**
	 * コンストラクタ
	 * 
	 * @param context
	 */
	public NoodleManager(Context context) {
		this.context = context;
		checkExternalStorage();
		noodleGaeController = new NoodleGaeController(context);
		noodleSqlController = new NoodleSqlController(context);
	}

	
	
	/**
	 * JANコードを引数にSQliteやGAEから商品マスタを得ます
	 * 
	 * @param janCode
	 * @return
	 */
	public NoodleMaster getNoodleMaster(String janCode) throws SQLException,
			GaeException {
		// SQliteに商品マスタがあるか問い合わせる
		NoodleMaster master = noodleSqlController.getNoodleMaster(janCode);
		if (master != null) {
			// SQliteの商品マスタを返す
			return master;
		}
		// GAEに問い合わせる
		master = noodleGaeController.getNoodleMaster(janCode);
		if (master != null) {
			// SQliteになくてGAEにある場合はSQliteに登録してあげる
			noodleSqlController.createNoodleMater(master);
		}
		return master;
	}

	/**
	 * SQLiteとGAEに商品を登録する
	 * 
	 * @param noodleMaster
	 * @throws SQLException
	 * @throws DuplexNoodleMasterException
	 * @throws GaeException
	 */
	public void createNoodleMaster(NoodleMaster noodleMaster)
			throws SQLException, DuplexNoodleMasterException, GaeException {
		// SQliteにすでに登録されていないか調べる
		NoodleMaster master = noodleSqlController.getNoodleMaster(noodleMaster
				.getJanCode());
		if (master != null) {
			// 重複エラーを返す
			throw new DuplexNoodleMasterException();
		}
		// SQliteに登録
		noodleSqlController.createNoodleMater(noodleMaster);
		// GAEに登録
		noodleGaeController.create(noodleMaster);
	}

	/**
	 * 商品履歴を登録する
	 * 
	 * @param noodleMaster
	 * @throws SQLException
	 */
	public void createNoodleHistory(NoodleMaster noodleMaster, int measureTime,
			Date measureDate) throws SQLException {
		// 商品マスタは何かしら登録されてから使われているはずなのでGAEにマスタを登録することはしない
		noodleSqlController.createNoodleHistory(noodleMaster, measureTime,
				measureDate);
	}

	/**
	 * 商品履歴を返す
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<NoodleHistory> getNoodleHistories() throws SQLException {
		// とりあえず最新30件を返すようにしておく
		return noodleSqlController.getNoodleHistories(30);
	}

	/**
	 * マスタすべてを返す
	 * @return
	 * @throws SQLException
	 */
	public List<NoodleMaster> getNoodleMastersForSqlite() throws SQLException{
		return noodleSqlController.getNoodleMasters();
	}
	
	/**
	 * 外部ストレージチェック
	 */
	private void checkExternalStorage() {
		if(SAVE_IMAGE_DIRECTORY != null){
			return;
		}
		
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			SAVE_IMAGE_DIRECTORY = new File(Environment.getExternalStorageDirectory(),
					context.getPackageName());
			if (!SAVE_IMAGE_DIRECTORY.exists()) {
				SAVE_IMAGE_DIRECTORY.mkdirs();
			}
		} else {
			new AlertDialog.Builder(context).setMessage("SDカードを挿入してください")
					.setPositiveButton("OK", null).show();
		}
	}

}
