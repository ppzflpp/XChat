package com.dragon.xchat.data;

import android.os.Parcel;
import android.os.Parcelable;

public class Friend implements Parcelable{
	

	private String name;
	private String desc;
	private String uid;
	
	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}
	
	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeString(name);
		parcel.writeString(desc);
		parcel.writeString(uid);
		
	}
	
	public static final Parcelable.Creator<Friend> CREATOR = new Creator<Friend>() {  
		@Override  
		public Friend createFromParcel(Parcel source) {  
			Friend msg = new Friend();  
			msg.name = source.readString();
			msg.desc = source.readString();
			msg.uid = source.readString();
			return msg;  
		}

		@Override
		public Friend[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new Friend[1];
		} 
	};

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

}
