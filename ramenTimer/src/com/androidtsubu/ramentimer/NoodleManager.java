package com.androidtsubu.ramentimer;

import java.sql.SQLException;

/**
 * データの読み書きをするマネージャーです
 * @author hide
 *
 */
public class NoodleManager {
	/**SQLite読み書きクラス*/
	private NoodleSqlController noodleSqlController;
	/**GAE読み書きクラス*/
	private NoodleGaeController noodleGaeController;
	
	/**
	 * コンストラクタ
	 */
	public NoodleManager(){
		noodleGaeController = new NoodleGaeController();
	}
	
	/**
	 * JANコードを引数にSQliteやGAEから商品マスタを得ます
	 * @param janCode
	 * @return
	 */
	public NoodleMaster getNoodleMaster(String janCode){
		/**@todo SQLiteからデータをひっぱってみる。なければGAEからデータをひっぱってみる*/
		try {
			//とりあえずGAEからだけひっぱってみる
			return noodleGaeController.getNoodleMaster(janCode);
		} catch (GaeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * SQLiteとGAEに商品マスタを登録する
	 * @param noodleMaster
	 * @throws SQLException
	 * @throws GaeException
	 */
	public void createNoodleMaster(NoodleMaster noodleMaster) throws SQLException,GaeException{
		/**@todo SQLiteとGAEに商品マスタを登録する。SQLiteに登録できなくてGAEに登録できた場合はどうするか*/
	}
	
	/**
	 * 商品履歴を登録する
	 * @param noodleMaster
	 * @throws SQLException
	 */
	public void createNoodleHistory(NoodleMaster noodleMaster) throws SQLException{
		/**@todo GAEにマスタを登録してから履歴を登録する？*/
	}
	
	
}
