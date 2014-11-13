package com.dragon.xchat;

import com.dragon.xchat.data.ChatMessage;
import com.dragon.xchat.data.ChatMessageCallback;
import com.dragon.xchat.service.ChatService;
import com.dragon.xchat.service.IChatService;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;


/**
 * A login screen that offers login via email/password.
 */
public class BaseActivity extends FragmentActivity {

	protected Object mLock = new Object();
	protected boolean mCanLoadData = false;
	
	private ServiceConnectCallback mCallback;
	
	protected IChatService mChatService;
	protected ServiceConnection mServiceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			// TODO Auto-generated method stub
			mChatService =  IChatService.Stub.asInterface(binder); 
			if(mCallback != null){
				mCallback.onBind();
			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			if(mCallback != null){
				mCallback.onUnBind();
			}
		}
		
	};
	
	protected ChatMessageCallback mMessageCallback = new ChatMessageCallback.Stub() {
		
		@Override
		public void onMessageRefresh(ChatMessage msg) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent i = new Intent(this,ChatService.class);
		this.bindService(i, mServiceConnection,Context.BIND_AUTO_CREATE);
	}
	

	protected void onDestroy(String userName) {
		if (mChatService != null && userName != null) {
			try {
				mChatService
						.unregisterChatListener(userName, mMessageCallback);
				mChatService.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if(mServiceConnection != null){
			this.unbindService(mServiceConnection);
		}
		
		super.onDestroy();
	}
	
	protected void setServiceCallback(ServiceConnectCallback callback){
		mCallback = callback;
	}
	
	interface ServiceConnectCallback{
		void onBind();
		void onUnBind();
	}

}
