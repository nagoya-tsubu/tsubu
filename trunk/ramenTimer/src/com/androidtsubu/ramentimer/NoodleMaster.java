package com.androidtsubu.ramentimer;

import java.text.DecimalFormat;

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
	/** ゆで時間 */
	private int timerLimit;

	/**
	 * コンストラクタ
	 * 
	 * @param janCode
	 * @param name
	 * @param image
	 * @param timerLimit
	 * @param noodleType
	 */
	public NoodleMaster(String janCode, String name, Bitmap image, int timerLimit) {
		this.janCode = janCode;
		this.name = name;
		this.image = image;
		this.timerLimit = timerLimit;
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
	
	public void setImage(Bitmap image){
		this.image = image;
	}
	
	public int getTimerLimit(){
		return timerLimit;
	}
	
	public String getTimerLimitString(){
		DecimalFormat df = new DecimalFormat("0");
		int min = getTimerLimit() / 60;
		int sec = getTimerLimit() % 60;
		StringBuilder buf = new StringBuilder(df.format(min));
		buf.append("分");
		df = new DecimalFormat("00");
		buf.append(df.format(sec));
		buf.append("秒");
		
		return buf.toString();
	}

	/**
	 * 完全なデータかどうかを返す
	 * @return
	 */
	public boolean isCompleteData(){
		if(janCode == null || janCode.equals("")){
			return false;
		}
		if(name == null || name.equals("")){
			return false;
		}
		if(image == null){
			return false;
		}
		if(timerLimit <= 0){
			return false;
		}
		return true;
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
	}
	
	public static final Parcelable.Creator<NoodleMaster> CREATOR = new Parcelable.Creator<NoodleMaster>() {
		public NoodleMaster createFromParcel(Parcel in) {
			return new NoodleMaster(in);
		}

		public NoodleMaster[] newArray(int size) {
			return new NoodleMaster[size];
		}
	};
}
