package com.dragon.xchat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.dragon.xchat.ChatActivity;
import com.dragon.xchat.MainActivity;
import com.dragon.xchat.R;

import org.jivesoftware.smack.packet.Message;

import com.dragon.xchat.data.ChatMessage;
import com.dragon.xchat.data.ChatMessageCallback;
import com.dragon.xchat.data.Friend;
import com.dragon.xchat.data.MessageCallback;
import com.dragon.xchat.network.ConnectorHelper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class ChatService extends Service {
	
	private Map<String,List<ChatMessage>> mChatMessageMap = new HashMap<String,List<ChatMessage>>();
	private Map<String,ChatMessageCallback> mChatMessageCallbackMap = new HashMap<String,ChatMessageCallback>();

	private ChatServiceImpl mService;
	private NotificationManager mNotificationManager ;
	private LocationClient mLocationClient = null;
	private BDLocationListener mBDListener = new BDLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation bdLocation) {
			String city = bdLocation.getAddrStr();
			if(city != null && !city.equals("")){
				Log.d("TAG","city = " + city);
				mLocationClient.unRegisterLocationListener(this);
				mLocationClient.stop();
				mLocationClient = null;
			}
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		if(mService == null){
			mService = new ChatServiceImpl(this);
		}
		Log.i("TAG", "onBind");
		return mService;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		if(mNotificationManager == null){
			mNotificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
		}

		if(mLocationClient == null) {
			mLocationClient = new LocationClient(getApplicationContext());
			LocationClientOption option = new LocationClientOption();
			option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
			option.setCoorType("bd09ll");
			option.setScanSpan(5000);
			option.setIsNeedAddress(true);
			option.setNeedDeviceDirect(true);
			mLocationClient.setLocOption(option);
			mLocationClient.registerLocationListener(mBDListener);
			mLocationClient.start();
		}
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

	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mLocationClient != null) {
			mLocationClient.unRegisterLocationListener(mBDListener);
			mLocationClient.stop();
		}
	}

	private void processNotification(boolean showNotification,Friend friend){
		if(showNotification) {
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), MainActivity.class);
			
			PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			
			Notification notification = new Notification.Builder(getApplicationContext())
					.setContentText(getApplicationContext().getString(R.string.has_unread_msg))
					.setContentTitle(getApplicationContext().getString(R.string.app_name))
					.setSmallIcon(R.drawable.ic_launcher)
					.setContentIntent(pendingIntent)
					.build();
			
			if (mNotificationManager != null) {
				mNotificationManager.notify(0, notification);
			}
		}else{
			if(mNotificationManager != null) {
				mNotificationManager.cancel(0);
			}
		}
	}

	public void notifyMessage(ChatMessage msg){
		refreshMessage(msg);
	}

	private void refreshMessage(ChatMessage msg){
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
		}else{
			msg.setIsRead(false);
			//1st:send notification
			Friend friend = new Friend();
			friend.setName(msg.getjId());
			friend.setUid(msg.getjId());
			processNotification(true,friend);
			
			//2nd refresh main list
			ChatMessageCallback callback = mChatMessageCallbackMap
					.get(MainActivity.MAIN_ACTIVITY_REFRESH_TAG);
			if (callback != null) {
				try {
					callback.onMessageRefresh(msg);
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		public boolean register(String userName, String password)
				throws RemoteException {
			// TODO Auto-generated method stub
			boolean result = getConnectorHepler().connect()
					&& getConnectorHepler().register(userName, password);
			
			return result;
		}
		
		@Override
		public void close(){
			
		}
		

		@Override
		public void sendMessage(String jId, String threadId, String msg)
				throws RemoteException {
			// TODO Auto-generated method stub
			mHelper.sendMessage(jId, threadId, msg);
		}
		

		@Override
		public List<Friend> getAllFriends() throws RemoteException {
			// TODO Auto-generated method stub
			return mHelper.getAllFriends();
		}
		
		private ConnectorHelper getConnectorHepler(){
			if(mHelper == null)
				mHelper = new ConnectorHelper(ChatService.this);
			return mHelper;
		}
		
		
		public void registerChatMessageListener(String uid,ChatMessageCallback callback){
			if(callback != null){
				Log.d("TAG","uid = " + uid);
				mChatMessageCallbackMap.put(uid, callback);
			}

			processNotification(false,null);

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
		
		public void unregisterChatMessageListener(String uid,ChatMessageCallback callback){
			if(uid != null){
				mChatMessageCallbackMap.remove(uid);
			}
		}

		@Override
		public List<Friend> searchFriend(String name) throws RemoteException {
			// TODO Auto-generated method stub
			return mHelper.searchFriend(name);
		}

		@Override
		public boolean addFriend(String name) throws RemoteException {
			// TODO Auto-generated method stub
			return mHelper.addFriend(name);
		}


		
	}


}
