package com.androidtsubu.ramentimer;

import java.util.Date;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * ラーメンを食べた履歴です
 * @author hide
 *
 */
public class NoodleHistory implements Parcelable{
	/**商品マスタ*/
	private NoodleMaster noodleMaster;
	/**計測時間*/
	private Date measureTime;
	/**マスタのJANコード*/
	private String janCode;	//janCodeの型をintからStringに変更 by leibun  date 2010.11.01
	/**マスタの名称*/
	private String name;
	/**マスタの画像イメージ*/
	private Bitmap image;
	
	/**
	 * コンストラクタ
	 * @param noodleMaster
	 * @param measureTime
	 */
	public NoodleHistory(NoodleMaster noodleMaster, Date measureTime){
		this.noodleMaster = noodleMaster;
		this.measureTime = measureTime;
		this.janCode = noodleMaster.getJanCode();
		this.name = noodleMaster.getName();
		this.image = noodleMaster.getImage();
	}
	
	/**
	 * コンストラクタ
	 * @param parcel
	 */
	public NoodleHistory(Parcel parcel){
		this.noodleMaster = parcel.readParcelable(NoodleMaster.class.getClassLoader());
		this.measureTime =  new Date(parcel.readLong());
		this.janCode = parcel.readString();
		this.name = parcel.readString();
		this.image = parcel.readParcelable(Bitmap.class.getClassLoader());
	}

	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelable(noodleMaster, 0);
		dest.writeLong(measureTime.getTime());
		dest.writeString(janCode);
		dest.writeString(name);
		dest.writeParcelable(image, 0);
	}
}
