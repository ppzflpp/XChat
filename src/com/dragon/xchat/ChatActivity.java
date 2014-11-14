package com.dragon.xchat;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import com.dragon.xchat.data.ChatMessage;
import com.dragon.xchat.data.ChatMessageCallback;
import com.dragon.xchat.data.Friend;
import com.dragon.xchat.network.ConnectorHelper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.RemoteException;
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
	private String mReceivedMsg = "";

	private final static int MSG_UPDATE_CHAT = 0;
	private Handler mMainHander = new Handler() {
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_UPDATE_CHAT:
				addContentView(true, mReceivedMsg);
				adjustChatContentPosition();
				break;
			}
		}
	};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		mFriend = (Friend) this.getIntent().getParcelableExtra("friend");
		setTitle(this.getString(R.string.chat_with, mFriend.getName()));

		mHandlerThread = new HandlerThread("Chat") {

			@Override
			protected void onLooperPrepared() {
				// TODO Auto-generated method stub
				super.onLooperPrepared();


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
					addContentView(false, mChatInputView.getText().toString());
					adjustChatContentPosition();
					mChatInputView.setText("");
					mThreadHandler.post(new Runnable() {
						public void run() {
							if (mChatService != null) {
								try {
									mChatService.sendMessage(mFriend.getUid(),
											null, content);
								} catch (RemoteException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}

						}

					});
				}
			}
		});

		mScrollview = (ScrollView) findViewById(R.id.scrollview);	
		
		synchronized(mLock){
			if(!mServiceOrViewReady){
				mServiceOrViewReady = true;
				return;
			}
		}
		registerMessageCallback();
	}

	public void messageRefresh(ChatMessage msg){
		if(msg != null){
			mReceivedMsg = msg.getBody();
			mMainHander.sendEmptyMessage(MSG_UPDATE_CHAT);			
		}
	}

	private void addContentView(boolean isReceivedMsg, String msg) {
		View view = null;
		if (isReceivedMsg) {
			view = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.msg_receive_item, null);
			((TextView) view.findViewById(R.id.msg_receive_name))
					.setText(mFriend.getName());
			((TextView) view.findViewById(R.id.msg_receive_content))
					.setText(msg);
		} else {
			view = LayoutInflater.from(getApplicationContext()).inflate(
					R.layout.msg_send_item, null);
			((TextView) view.findViewById(R.id.msg_send_name))
					.setText(R.string.me);
			((TextView) view.findViewById(R.id.msg_send_content)).setText(msg);
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

	protected void onResume() {
		super.onResume();
	}

	protected void onDestroy() {
		super.onDestroy(mFriend.getUid());
	}

	@Override
	public void onServiceConnected() {
		// TODO Auto-generated method stub
		synchronized(mLock){
			if(!mServiceOrViewReady){
				mServiceOrViewReady = true;
				return;
			}
		}
		registerMessageCallback();
	}

	@Override
	public void onServiceDisonnected() {
		// TODO Auto-generated method stub
		
	}
	
	private void registerMessageCallback(){
		try{
		if(mChatService != null){
			mChatService.registerChatMessageListener(mFriend.getUid(), mMessageCallback);
		}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private void adjustChatContentPosition(){
		mMainHander.post(new Runnable() {  
		    @Override  
		    public void run() {  
		    	mScrollview.fullScroll(ScrollView.FOCUS_DOWN);  
		    }  
		}); 
	}
	

}
