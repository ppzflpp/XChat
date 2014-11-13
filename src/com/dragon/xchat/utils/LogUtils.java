package com.dragon.xchat.utils;

import org.jivesoftware.smack.packet.Message;

import android.util.Log;

public class LogUtils {
	private static final String TAG = "XChat";
	private static final boolean DEBUG = true;
	private static final boolean DEBUG_MESSAGE = true;
	public static void printMessage(String prefix,Message msg){
		if(DEBUG_MESSAGE){
			Log.d(TAG,"" + prefix + ":from = " + msg.getFrom()
					+",to = " + msg.getTo()
					+",body = " + msg.getBody());
		}
	}
	public static void log(String tag,String msg){
		if(DEBUG){
			Log.d(TAG, ""+tag + ":" + msg);
		}
	}
}
