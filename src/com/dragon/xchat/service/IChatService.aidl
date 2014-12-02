package com.dragon.xchat.service;
import com.dragon.xchat.data.ChatMessageCallback;
import com.dragon.xchat.data.Friend;

interface IChatService{
	boolean login(String userName,String password);
	boolean register(String userName,String password); 
	void close();
	void registerChatMessageListener(String jId,ChatMessageCallback callback);
	void unregisterChatMessageListener(String jId,ChatMessageCallback callback);
	void sendMessage(String jId,String theadId,String msg);
	List<Friend> searchFriend(String name);
	boolean addFriend(String name);
	List<Friend> getAllFriends();
}