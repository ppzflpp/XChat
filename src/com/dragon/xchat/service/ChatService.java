package com.dragon.xchat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.packet.Message;

import com.dragon.xchat.data.ChatMessage;
import com.dragon.xchat.data.ChatMessageCallback;
import com.dragon.xchat.data.MessageCallback;
import com.dragon.xchat.network.ConnectorHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class ChatService extends Service {
	
	private Map<String,List<ChatMessage>> mChatMessageMap = new HashMap<String,List<ChatMessage>>();
	private Map<String,ChatMessageCallback> mChatMessageCallbackMap = new HashMap<String,ChatMessageCallback>();

	private ChatServiceImpl mService;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		if(mService == null){
			mService = new ChatServiceImpl(this);
		}
		return mService;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}


	@Override
	public void onRebind(Intent intent) {
		// TODO Auto-generated method stub
		super.onRebind(intent);
	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
	}


	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	public void notifyMessage(ChatMessage msg){
		String jid = msg.getjId();
		//if jid has register , notify it to refresh
		if(mChatMessageCallbackMap.containsKey(jid)){
			msg.setIsRead(false);
			try {
				mChatMessageCallbackMap.get(jid).onMessageRefresh(msg);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//put message to map
		List<ChatMessage> msgList = null;
		if(mChatMessageMap.containsKey(jid))
			msgList = mChatMessageMap.get(jid);
		else{
			msgList = new ArrayList<ChatMessage>();
			mChatMessageMap.put(jid, msgList);
		}
		msgList.add(msg);		
	}
	
	
	
	public class ChatServiceImpl extends IChatService.Stub{
		private ConnectorHelper mHelper = null;
		private Context context;
		
		public ChatServiceImpl(Context context){
			this.context = context;
		}

		@Override
		public boolean login(String userName, String password)
				throws RemoteException {
			// TODO Auto-generated method stub
			boolean result = getConnectorHepler().connect()
					&& getConnectorHepler().login(userName, password);
			if(result){
				getConnectorHepler().registerChatListener();
			}
			return result;
		}
		@Override
		public void close(){
			
		}
		
		private ConnectorHelper getConnectorHepler(){
			if(mHelper == null)
				mHelper = new ConnectorHelper(ChatService.this);
			return mHelper;
		}
		
		
		public void registerChatListener(String uid,ChatMessageCallback callback){
			if(callback != null){
				mChatMessageCallbackMap.put(uid, callback);
			}
			
			if(mChatMessageMap.containsKey(uid)){
				List<ChatMessage> list = mChatMessageMap.get(uid);
				for(int i = 0; list != null && i < list.size();i++){
					ChatMessage chatMessage = list.get(i);
					if(!chatMessage.isRead()){
						chatMessage.setIsRead(true);
						try {
							callback.onMessageRefresh(chatMessage);
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
			}
			
		}
		
		public void unregisterChatListener(String uid,ChatMessageCallback callback){
			if(uid != null){
				mChatMessageCallbackMap.remove(uid);
			}
		}
		
	}


}
