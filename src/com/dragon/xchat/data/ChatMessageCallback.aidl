package com.dragon.xchat.data;

import com.dragon.xchat.data.ChatMessage;

interface ChatMessageCallback {
	void onMessageRefresh(in ChatMessage msg);
}
