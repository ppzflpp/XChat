package com.dragon.xchat.network;

import java.util.List;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;

import com.dragon.xchat.data.Friend;
import com.dragon.xchat.service.ChatService;

import android.content.Context;

public class ConnectorHelper {

	
	private Connector mConnector = null;
	
	public ConnectorHelper(ChatService service){
		if(mConnector == null)
			mConnector = new Connector(service);
	}

	
	public Chat createChat(String userId,MessageListener listener){
		if(mConnector != null){
			return mConnector.createChat(userId, listener);
		}
		return null;
	}
	
	public boolean connect(){
		if(mConnector != null )
			return mConnector.connect();
		return false;
	}
	
	public boolean register(String userName,String password){
		if(mConnector != null)
			return mConnector.register(userName,password);
		return false;
	}

	public boolean login(String userName,String password){
		if(mConnector != null)
			return mConnector.login(userName,password);
		return false;
	}
	
	public void sendMessage(String jId,String threadId,String msg){
		if(mConnector != null)
			 mConnector.sendMessage(jId,threadId,msg);
	}
	
	public void registerChatListener(){
		if(mConnector != null)
			 mConnector.registerChatListener();
	}
	
	public List<Friend> getAllFriends(){
		if(mConnector != null)
			return mConnector.getAllFriends();
		return null;
	}
	
	public void close(){
		if(mConnector != null)
			mConnector.close();
	}
	
}
