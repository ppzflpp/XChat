package com.dragon.xchat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.dragon.xchat.data.Friend;
import com.dragon.xchat.network.ConnectorHelper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends BaseActivity {

	private Friend mFriend = null;
	private LinearLayout mInputContentParentView;
	private EditText mChatInputView;
	private Button mChatSendView;
	private ScrollView mScrollview;
	
	private HandlerThread mHandlerThread = null;
	private Handler mThreadHandler = null;
	private Chat mChat;
	private String mReceivedMsg = "";
	private MessageListener mMessageListener;
	
	private final static int UPDATE_CHAT_MSG = 0;
	private Handler mMainHander = new Handler(){
		public void handleMessage(android.os.Message msg){
			super.handleMessage(msg);
			switch(msg.what){
			case UPDATE_CHAT_MSG:
				addContentView(true,mReceivedMsg);
				//mScrollview.fullScroll(ScrollView.FOCUS_DOWN);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);


		mFriend = (Friend) this.getIntent().getSerializableExtra("friend");
		setTitle(this.getString(R.string.chat_with, mFriend.getName()));
	
		mHandlerThread = new HandlerThread("Chat"){

			@Override
			protected void onLooperPrepared() {
				// TODO Auto-generated method stub
				super.onLooperPrepared();
				
				mMessageListener = new MessageListener() {

					@Override
					public void processMessage(Chat chat, Message msg) {
						// TODO Auto-generated method stub
						processMessageLocked(chat,msg);			
					}

				};
				
			}		
		};
		
		mHandlerThread.start();
		mThreadHandler = new Handler(mHandlerThread.getLooper());

		mInputContentParentView = (LinearLayout) findViewById(R.id.chat_content);
		mChatInputView = (EditText) findViewById(R.id.chat_input);
		mChatSendView = (Button) findViewById(R.id.chat_send);
		mChatSendView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				final String content = mChatInputView.getText().toString();
				if (content == null || content.isEmpty()) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.chat_input), Toast.LENGTH_SHORT)
							.show();
				} else {
					addContentView(false,mChatInputView.getText().toString());

					mChatInputView.setText("");
					mThreadHandler.post(new Runnable() {
						public void run() {
							if (mChat == null) {
								mChat = ConnectorHelper.getInstance(
										getApplicationContext()).createChat(
										mFriend.getUid(), mMessageListener);
							}
							try {
								mChat.sendMessage(content);
							} catch (NotConnectedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (XMPPException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
				}
			}

		});
		
		mScrollview = (ScrollView)findViewById(R.id.scrollview);

	}
	
	private void processMessageLocked(Chat chat,Message msg){
		if(mChat != null || chat != null)
			Log.d("TAG","mChat = " + mChat.getThreadID() + ",chat = " + chat.getThreadID());
		if(mChat != chat){
			if(mChat != null){
				mChat.removeMessageListener(mMessageListener);
			}
			mChat = chat;
		}
		
		mReceivedMsg = msg.getBody();
		
		Log.d("TAG","mReceivedMsg = " + mReceivedMsg);
		
		mMainHander.sendEmptyMessage(UPDATE_CHAT_MSG); 
	}
	
	private void addContentView(boolean isReceivedMsg,String msg) {
		View view = null;
		if(isReceivedMsg){
			view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.msg_receive_item, null);
			((TextView)view.findViewById(R.id.msg_receive_name)).setText(mFriend.getName());
			((TextView)view.findViewById(R.id.msg_receive_content)).setText(msg);
		}
		else{
			view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.msg_send_item, null);
			((TextView)view.findViewById(R.id.msg_send_name)).setText(R.string.me);
			((TextView)view.findViewById(R.id.msg_send_content)).setText(msg);
		}
		
		LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		if (!isReceivedMsg) {
			ll.gravity = Gravity.RIGHT;
			ll.leftMargin = 20;
		} else {
			ll.gravity = Gravity.LEFT;
			ll.rightMargin = 20;
		}
		mInputContentParentView.addView(view, ll);
	}
	
	protected void onResume()
	{
		super.onResume();
	}


	protected void onDestroy(){
		super.onDestroy(mFriend.getUid());
	}
	
}
