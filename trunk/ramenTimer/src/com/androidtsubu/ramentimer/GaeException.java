package com.androidtsubu.ramentimer;

/**
 * @gabuさん作成GAEとのやりとり関連のException
 * @author hide
 *
 */
public class GaeException extends Exception {
	/**
	 * Serializableインタフェースを実装しているので、バージョン番号を付与しておく
	 * (Exceptionの親クラスであるThrowableクラスがSerializableインタフェースを実装している)
	 */
	private static final long serialVersionUID = 2332994598354016927L;

}
