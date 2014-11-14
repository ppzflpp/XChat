package com.dragon.xchat.utils;

public class StringUtils {
	public static String getJid(String from){
		int index = from.indexOf("/");
		String jId = null;
		if(index == -1){
			jId = from;
		}else{
			jId = from.substring(0, index);
		}
		return jId;
	}
}
