package com.androidtsubu.ramentimer;

/**
 * リクエストコードの種類
 * 呼び出し元の分岐を管理
 * @author leibun
 *
 */
public enum RequestCode {
	DASHBORAD2READER,		//ダッシュボードからリーダー
	DASHBORAD2HISTORY,	//ダッシュボードから履歴
	DASHBORAD2TIMER,		//ダッシュボードからタイマー
	READER2TIMER,			//リーダーからタイマー
	READER2CREATE,		//リーダーから登録
	CREATE2TIMER,			//登録からタイマー
	TIMER2CREATE,			//タイマーから登録
	HISTORY2TIMER,		//履歴からタイマー	
	//ActionBar用
	ACTION_HISTORY,
	ACTION＿TIMER;
	
	//Intent.putExtra()の第１引数に利用
	public static final String KEY_RESUEST_CODE= "REQUEST_CODE";
	
};