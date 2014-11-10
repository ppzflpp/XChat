package com.dragon.xchat;

import com.dragon.xchat.data.ChatMessage;
import com.dragon.xchat.data.ChatMessageCallback;
import com.dragon.xchat.service.IChatService;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;


/**
 * A login screen that offers login via email/password.
 */
public class BaseActivity extends FragmentActivity {

	
	
	protected IChatService mChatService;
	protected ServiceConnection mServiceConnection = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			// TODO Auto-generated method stub
			mChatService =  IChatService.Stub.asInterface(binder); 
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			// TODO Auto-generated method stub
			
		}
		
	};
	
	protected ChatMessageCallback mMessageCallback = new ChatMessageCallback.Stub() {
		
		@Override
		public void onMessageRefresh(ChatMessage msg) throws RemoteException {
			// TODO Auto-generated method stub
			
		}
	};
	

	protected void onDestroy(String userName) {
		super.onDestroy();
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

}
