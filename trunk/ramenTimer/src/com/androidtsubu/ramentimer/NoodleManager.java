package com.androidtsubu.ramentimer;

import java.sql.SQLException;
import java.util.Date;

import android.content.Context;

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

	/**
	 * コンストラクタ
	 * 
	 * @param context
	 */
	public NoodleManager(Context context) {
		noodleGaeController = new NoodleGaeController();
		noodleSqlController = new NoodleSqlController(context);
	}

	/**
	 * JANコードを引数にSQliteやGAEから商品マスタを得ます
	 * 
	 * @param janCode
	 * @return
	 */
	public NoodleMaster getNoodleMaster(String janCode) throws SQLException, GaeException {
		//SQliteに商品マスタがあるか問い合わせる
		NoodleMaster master = noodleSqlController.getNoodleMaster(janCode);
		if(master != null){
			//SQliteの商品マスタを返す
			return master;
		}
		//GAEに問い合わせる
		master = noodleGaeController.getNoodleMaster(janCode);
		if(master != null){
			//SQliteになくてGAEにある場合はSQliteに登録してあげる
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
		//SQliteにすでに登録されていないか調べる
		NoodleMaster master = noodleSqlController.getNoodleMaster(noodleMaster.getJanCode());
		if(master != null){
			//重複エラーを返す
			throw new DuplexNoodleMasterException();
		}
		//SQliteに登録
		noodleSqlController.createNoodleMater(noodleMaster);
		//GAEに登録
		noodleGaeController.create(noodleMaster);
	}

	/**
	 * 商品履歴を登録する
	 * 
	 * @param noodleMaster
	 * @throws SQLException
	 */
	public void createNoodleHistory(NoodleMaster noodleMaster, Date measureTime){
		//商品マスタは何かしら登録されてから使われているはずなのでGAEにマスタを登録することはしない
		noodleSqlController.createNoodleHistory(noodleMaster, measureTime);
	}

}
