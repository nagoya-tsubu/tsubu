package com.androidtsubu.ramentimer;


/**
 * @gabuさん作成GAEのAPIを使ってGAEへの読み書きをするクラスです
 * @author hide
 */
public class NoodleGaeController {
	
	/**
	 * JANコードでGAEから商品マスタを得ます
	 * @param janCode
	 * @return
	 * @throws GaeException
	 */
	public NoodleMaster getNoodleMaster(int janCode) throws GaeException{
		return null;
	}
	
	/**
	 * GAEに商品マスタを追加作成します
	 * @param noodleMaster
	 * @throws GaeException
	 */
	public void create(NoodleMaster noodleMaster) throws GaeException{
		return;
	}

}
