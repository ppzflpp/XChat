package com.dragon.xchat.service;
import com.dragon.xchat.data.ChatMessageCallback;

interface IChatService{
	boolean login(String userName,String password);
	void close();
	void registerChatListener(String jId,ChatMessageCallback callback);
	void unregisterChatListener(String jId,ChatMessageCallback callback);
}