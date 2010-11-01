package com.tsubu.ramentimer;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * ラーメン商品マスタです
 * @author hide
 */
public class NoodleMaster implements Parcelable{
	/** JANコード */
	private String janCode; //janCodeの型をintからStringに変更 by leibun  date 2010.11.01
	/** 名前 */
	private String name;
	/** 画像イメージ */
	private Bitmap image;
	/** 時間 */
	private int timerLimit;
	/** 麺種類 */
	private NoodleType noodleType;

	/**
	 * コンストラクタ
	 * 
	 * @param janCode
	 * @param name
	 * @param image
	 * @param timerLimit
	 * @param noodleType
	 */
	public NoodleMaster(String janCode, String name, Bitmap image, int timerLimit,
			NoodleType noodleType) {
		this.janCode = janCode;
		this.name = name;
		this.image = image;
		this.timerLimit = timerLimit;
		this.noodleType = noodleType;
	}
	
	/**
	 * コンストラクタ
	 * @param parcel
	 */
	public NoodleMaster(Parcel parcel){
		this.janCode = parcel.readString();
		this.name = parcel.readString();
		this.image = parcel.readParcelable(Bitmap.class.getClassLoader());
		this.timerLimit = parcel.readInt();
		this.noodleType = NoodleType.values()[parcel.readInt()];
	}
	
	public String getJanCode(){
		return janCode;
	}
	
	public String getName(){
		return name;
	}
	
	public Bitmap getImage(){
		return image;
	}
	
	public int getTimerLimit(){
		return timerLimit;
	}
	
	public NoodleType getNoodleType(){
		return noodleType;
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeString(janCode);
		dest.writeString(name);
		dest.writeParcelable(image, 0);
		dest.writeInt(timerLimit);
		dest.writeInt(noodleType.ordinal());
	}
	
	public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
		public NoodleMaster createFromParcel(Parcel in) {
			return new NoodleMaster(in);
		}

		public NoodleMaster[] newArray(int size) {
			return new NoodleMaster[size];
		}
	};

	
}
