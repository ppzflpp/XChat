package com.dragon.xchat.network;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.harmony.javax.security.sasl.SaslException;
import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterGroup;
import org.jivesoftware.smack.SmackAndroid;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.XMPPException.XMPPErrorException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.xdata.Form;

import com.dragon.xchat.data.ChatMessage;
import com.dragon.xchat.data.Friend;
import com.dragon.xchat.service.ChatService;
import com.dragon.xchat.utils.LogUtils;
import com.dragon.xchat.utils.StringUtils;

import android.content.Context;
import android.util.Log;

public class Connector {

	private static final String TAG = "XChat:Connector";
	
	private static final String IP = "112.124.120.18";
	private static final int PORT = 5222;
	private XMPPConnection mConnection = null;
	private ConnectionConfiguration mConfig = null;
	private ChatManager mChatManager;
	private ChatManagerListener mChatManagerListener;
	private UserSearchManager mUserSearchManager;
	private Map<String,Chat> mChatList = new HashMap<String,Chat>();

	private ChatService mService = null;

	public Connector(ChatService service) {
		mService = service;
		SmackAndroid.init(mService.getApplicationContext());
	}

	public boolean connect() {

		if (mConfig == null) {

			mConfig = new ConnectionConfiguration(IP, PORT);

			try {
				MemorizingTrustManager.setKeyStoreFile("private", "sslkeys.bks");

				SSLContext sc = SSLContext.getInstance("TLS");
				MemorizingTrustManager mtm = new MemorizingTrustManager(mService.getApplicationContext());
				sc.init(null, new X509TrustManager[] { mtm },
						new java.security.SecureRandom());
				mConfig.setCustomSSLContext(sc);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//mConfig.setSecurityMode(SecurityMode.disabled);
		}

		if (mConnection == null) {
			mConnection = new XMPPTCPConnection(mConfig);
			//mConnection.setPacketReplyTimeout(20 * 1000);
			try {
				mConnection.connect();
			} catch (SmackException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (mConnection != null && mConnection.isConnected()) {
			return true;
		} else if (mConnection != null) {
			try {
				mConnection.disconnect();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mConnection = null;
			return false;
		}
		return false;
	}

	public boolean register(String userName, String password) {
		boolean success = true;

		if (mConnection != null) {
			AccountManager am = AccountManager.getInstance(mConnection);
			try {
				am.createAccount(userName, password);
			} catch (NoResponseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				success = false;
			} catch (XMPPErrorException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				success = false;
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				success = false;
			}

		}
		return success;
	}

	public boolean login(String userName, String password) {
		boolean success = false;
		if (mConnection != null) {
			try {
				mConnection.login(userName, password);
				success = true;
			} catch (SaslException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XMPPException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SmackException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;
	}

	public void sendMessage(String jId,String threadId,String msg){
		Chat chat = mChatList.get(jId);
		Log.d("TAG","jid = " + jId);
		if(chat == null){
			Log.d("TAG","chat is null");
			ChatManager manager = getChatManager();
			chat = manager.createChat(jId, new MessageListener(){

				@Override
				public void processMessage(Chat chat, Message msg) {
					// TODO Auto-generated method stub
					Log.d("TAG","msg = " + msg.getBody());
					if(mService != null){
						notifyMessage(msg);
					}
				}
				
			});
			mChatList.put(jId, chat);
		}
		
		Log.d("TAG","chat = " + chat + ",listeners = " + chat.getListeners().size());
		
		try {
			chat.sendMessage(msg);
			Log.d("TAG","send message,msg = " + msg);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private ChatManager getChatManager(){
		if (mChatManager == null){
			mChatManager = ChatManager.getInstanceFor(mConnection);			
		}
		return mChatManager;
	}
	
	public void registerChatListener(){
		if (mConnection != null) {
			if (mChatManager == null){
				mChatManager = ChatManager.getInstanceFor(mConnection);			
			}
			
			if(mChatManagerListener == null)
			{
				mChatManagerListener = new ChatManagerListener(){

					@Override
					public void chatCreated(Chat chat, boolean isExist) {
						// TODO Auto-generated method stub
						if(isExist){
							Log.i(TAG,"chat exist,return");
						}else{						
							if(!chat.getListeners().isEmpty()){
								chat.getListeners().clear();
							}
							chat.addMessageListener(new MessageListener(){

								@Override
								public void processMessage(Chat chat,
										Message msg) {
									// TODO Auto-generated method stub
									String jId = StringUtils.getJid(msg.getFrom());
									Chat c = mChatList.get(jId);
									if(c == null){
										mChatList.put(jId, chat);
									}else{
										
									}
									
									notifyMessage(msg);
									LogUtils.printMessage("chatCreated",msg);
								}
								
							});
						}		
				
					}
				};
			}
			
			mChatManager.addChatListener(mChatManagerListener);
			
		}
	}
	
	private void notifyMessage(Message msg){
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setjId(StringUtils.getJid(msg.getFrom()));
		chatMessage.setFrom(msg.getFrom());
		chatMessage.setBody(msg.getBody());
		chatMessage.setTo(msg.getTo());
		mService.notifyMessage(chatMessage);
	}
	
	public Chat createChat(String userId,final MessageListener msgListener){
		if (mConnection != null) {
			if (mChatManager == null){
				mChatManager = ChatManager.getInstanceFor(mConnection);			
			}
			
			if(mChatManagerListener != null)
			{
				mChatManager.removeChatListener(mChatManagerListener);
				mChatManagerListener = null;
			}
			
			mChatManagerListener = new ChatManagerListener(){

				@Override
				public void chatCreated(Chat chat, boolean arg1) {
					// TODO Auto-generated method stub
					chat.addMessageListener(msgListener);
				}
				
			};
			
			mChatManager.addChatListener(mChatManagerListener);
		}
		return mChatManager.createChat(userId, msgListener);
	}

	public void close() {
		if (mConnection != null) {
			try {
				mConnection.disconnect();
			} catch (NotConnectedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		mConnection = null;
		mConfig = null;
		mChatManager = null;
	}

	public List<Friend> getAllFriends() {

		List<Friend> friends = new ArrayList<Friend>();

		if (mConnection != null) {
			Roster roster = mConnection.getRoster();
			Collection<RosterGroup> groups = roster.getGroups();

			for (RosterGroup group : groups) {
				Collection<RosterEntry> entries = group.getEntries();
				for (RosterEntry entry : entries) {
					Friend friend = new Friend();
					friend.setName(entry.getName());
					friend.setUid(entry.getUser());
					friend.setDesc("");
					friends.add(friend);
				}
			}
		}
		return friends;
	}
	
	public boolean searchFriend(String name){
		if(mUserSearchManager == null)
			Log.d("TAG","000");
			//ServiceDiscoveryManager.getInstanceFor(mConnection)
			mUserSearchManager = new UserSearchManager(mConnection);
		
		try {
			Log.d("TAG","111");
			Log.d("TAG","getServiceName = " + mConnection.getServiceName()
					);
			Form searchForm = mUserSearchManager.getSearchForm(mConnection.getServiceName());
			Log.d("TAG","222");
			Form answerForm = searchForm.createAnswerForm();
			Log.d("TAG","333");
			answerForm.setAnswer("userAccount",true);
			answerForm.setAnswer("userPhote",name);
			
			
			
			ReportedData data = mUserSearchManager.getSearchResults(answerForm, "search"
					+ mConnection.getServiceName());
			Log.d("TAG","data = " + data);

		} catch (NoResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public void addFriend(String name){
		
	}
}
