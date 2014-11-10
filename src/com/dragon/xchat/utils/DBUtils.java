package com.dragon.xchat.utils;

import android.content.Context;

public class DBUtils {
	private static DBUtils mUtils = null;
	public DBUtils getInstance(Context context){
		if(mUtils == null){
			mUtils = new DBUtils();
		}
		return mUtils;
	}
	
	public void wirteMessage(){
		//write message to db
	}
	
}
