package com.tsubu.ramentimer;

import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;

/**
 * SQLiteへの読み書きをするクラスです
 * @author hide
 */
public class NoodleSqlController {
	
	/**
	 * SQLiteからJANコードで商品マスタを得ます
	 * @param janCode
	 * @return
	 * @throws SQLException
	 */
	public NoodleMaster getNoodleMaster(int janCode) throws SQLException{
		/**@todo 中身作る*/
		return null;
	}
	
	/**
	 * SQLiteからすべての商品マスタを得ます
	 * @return
	 * @throws SQLException
	 */
	public List<NoodleMaster> getNoodleMasters() throws SQLException{
		/**@todo 中身作る*/
		return new ArrayList<NoodleMaster>();
	}
	
	/**
	 * SQLiteからすべての商品履歴を得ます
	 * @return
	 * @throws SQLException
	 */
	public List<NoodleHistory> getNoodleHistories() throws SQLException{
		/**@todo 中身作る*/
		return new ArrayList<NoodleHistory>();
	}
	
	/**
	 * SQLiteに商品マスタを追加作成します
	 * @param noodleMaster
	 * @throws SQLException
	 */
	public void createNoodleMater(NoodleMaster noodleMaster) throws SQLException{
		/**@todo 中身作る*/
		return;
	}
	
	/**
	 * 引数の商品マスタをもとに商品履歴を追加作成します
	 * @param noodleMaster
	 * @throws SQLException
	 */
	public void createNoodleHistory(NoodleMaster noodleMaster) throws SQLException{
		return;
	}
}
