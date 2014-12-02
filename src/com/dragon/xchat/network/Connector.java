package com.dragon.xchat.network;

import java.io.IOException;
import java.util.*;

import javax.net.ssl.SSLContext;
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
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.address.provider.MultipleAddressesProvider;
import org.jivesoftware.smackx.bytestreams.socks5.provider.BytestreamsProvider;
import org.jivesoftware.smackx.chatstates.packet.ChatStateExtension;
import org.jivesoftware.smackx.commands.provider.AdHocCommandDataProvider;
import org.jivesoftware.smackx.delay.provider.DelayInformationProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverInfoProvider;
import org.jivesoftware.smackx.disco.provider.DiscoverItemsProvider;
import org.jivesoftware.smackx.iqlast.packet.LastActivity;
import org.jivesoftware.smackx.iqprivate.PrivateDataManager;
import org.jivesoftware.smackx.muc.packet.GroupChatInvitation;
import org.jivesoftware.smackx.muc.provider.MUCAdminProvider;
import org.jivesoftware.smackx.muc.provider.MUCOwnerProvider;
import org.jivesoftware.smackx.muc.provider.MUCUserProvider;
import org.jivesoftware.smackx.offline.packet.OfflineMessageInfo;
import org.jivesoftware.smackx.offline.packet.OfflineMessageRequest;
import org.jivesoftware.smackx.privacy.provider.PrivacyProvider;
import org.jivesoftware.smackx.search.ReportedData;
import org.jivesoftware.smackx.search.UserSearch;
import org.jivesoftware.smackx.search.UserSearchManager;
import org.jivesoftware.smackx.sharedgroups.packet.SharedGroupsInfo;
import org.jivesoftware.smackx.si.provider.StreamInitiationProvider;
import org.jivesoftware.smackx.vcardtemp.provider.VCardProvider;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.provider.DataFormProvider;
import org.jivesoftware.smackx.xevent.provider.MessageEventProvider;
import org.jivesoftware.smackx.xhtmlim.provider.XHTMLExtensionProvider;

import android.util.Log;

import com.dragon.xchat.data.ChatMessage;
import com.dragon.xchat.data.Friend;
import com.dragon.xchat.service.ChatService;
import com.dragon.xchat.utils.LogUtils;
import com.dragon.xchat.utils.StringUtils;

public class Connector {

	private static final String TAG = "XChat:Connector";
	private static final boolean USE_SSL = false;
	private static final String IP = "112.124.120.18";
	private static final int SSL_PORT = 5223;
	private static final int PORT = 5222;
	private XMPPConnection mConnection = null;
	private ConnectionConfiguration mConfig = null;
	private ChatManager mChatManager;
	private ChatManagerListener mChatManagerListener;
	private UserSearchManager mUserSearchManager;
	private Map<String, Chat> mChatList = new HashMap<String, Chat>();

	private ChatService mService = null;

	public Connector(ChatService service) {
		mService = service;
		SmackAndroid.init(mService.getApplicationContext());
	}

	public boolean connect() {

		if (mConfig == null) {

			configure();

			mConfig = new ConnectionConfiguration(IP, USE_SSL ? SSL_PORT : PORT);

			if (USE_SSL) {
				mConfig.setSecurityMode(SecurityMode.required);
				try {

					SSLContext sc = SSLContext.getInstance("TLS");
					MemorizingTrustManager mtm = new MemorizingTrustManager(
							mService.getApplicationContext());
					sc.init(null, new X509TrustManager[] { mtm },
							new java.security.SecureRandom());
					mConfig.setCustomSSLContext(sc);
					mConfig.setHostnameVerifier(mtm
							.wrapHostnameVerifier(new org.apache.http.conn.ssl.StrictHostnameVerifier()));

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else
				mConfig.setSecurityMode(SecurityMode.disabled);
		}

		if (mConnection == null) {
			mConnection = new XMPPTCPConnection(mConfig);
			mConnection.setPacketReplyTimeout(20 * 1000);
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

	public void sendMessage(String jId, String threadId, String msg) {
		Chat chat = mChatList.get(jId);
		Log.d("TAG", "jid = " + jId);
		if (chat == null) {
			Log.d("TAG", "chat is null");
			ChatManager manager = getChatManager();
			chat = manager.createChat(jId, new MessageListener() {

				@Override
				public void processMessage(Chat chat, Message msg) {
					// TODO Auto-generated method stub
					Log.d("TAG", "msg = " + msg.getBody());
					if (mService != null) {
						notifyMessage(msg);
					}
				}

			});
			mChatList.put(jId, chat);
		}

		Log.d("TAG", "chat = " + chat + ",listeners = "
				+ chat.getListeners().size());

		try {
			chat.sendMessage(msg);
			Log.d("TAG", "send message,msg = " + msg);
		} catch (NotConnectedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMPPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private ChatManager getChatManager() {
		if (mChatManager == null) {
			mChatManager = ChatManager.getInstanceFor(mConnection);
		}
		return mChatManager;
	}

	public void registerChatListener() {
		if (mConnection != null) {
			if (mChatManager == null) {
				mChatManager = ChatManager.getInstanceFor(mConnection);
			}

			if (mChatManagerListener == null) {
				mChatManagerListener = new ChatManagerListener() {

					@Override
					public void chatCreated(Chat chat, boolean isExist) {
						// TODO Auto-generated method stub
						if (isExist) {
							Log.i(TAG, "chat exist,return");
						} else {
							if (!chat.getListeners().isEmpty()) {
								chat.getListeners().clear();
							}
							chat.addMessageListener(new MessageListener() {

								@Override
								public void processMessage(Chat chat,
										Message msg) {
									// TODO Auto-generated method stub
									String jId = StringUtils.getJid(msg
											.getFrom());
									Chat c = mChatList.get(jId);
									if (c == null) {
										mChatList.put(jId, chat);
									} else {

									}

									notifyMessage(msg);
									LogUtils.printMessage("chatCreated", msg);
								}

							});
						}

					}
				};
			}

			mChatManager.addChatListener(mChatManagerListener);

		}
	}

	private void notifyMessage(Message msg) {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setjId(StringUtils.getJid(msg.getFrom()));
		chatMessage.setFrom(msg.getFrom());
		chatMessage.setBody(msg.getBody());
		chatMessage.setTo(msg.getTo());
		mService.notifyMessage(chatMessage);
	}

	public Chat createChat(String userId, final MessageListener msgListener) {
		if (mConnection != null) {
			if (mChatManager == null) {
				mChatManager = ChatManager.getInstanceFor(mConnection);
			}

			if (mChatManagerListener != null) {
				mChatManager.removeChatListener(mChatManagerListener);
				mChatManagerListener = null;
			}

			mChatManagerListener = new ChatManagerListener() {

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

	public List<Friend> searchFriend(String name) {

		List<Friend> friendsList = null;

		if (mUserSearchManager == null) {
			mUserSearchManager = new UserSearchManager(mConnection);
		}

		try {
			Form searchForm = mUserSearchManager.getSearchForm("search." + mConnection.getServiceName());
			Form answerForm = searchForm.createAnswerForm();
			answerForm.setAnswer("Username", true);
			answerForm.setAnswer("search", name);

			ReportedData data = mUserSearchManager.getSearchResults(answerForm,
					"search." + mConnection.getServiceName());

			List<ReportedData.Row> rows = data.getRows();
			Log.d("TAG","row size = " + rows.size());
			for (int i = 0; rows != null && i < rows.size(); i++) {
				if (friendsList == null)
					friendsList = new ArrayList<Friend>();

				Friend friend = new Friend();

				List<String> usernames = rows.get(i).getValues("Username");
				if(usernames.size() > 0)
					friend.setName(usernames.get(0));

				List<String> names = rows.get(i).getValues("Name");
				if(names.size() > 0)
					friend.setDesc(names.get(0));

				friendsList.add(friend);
			}

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

		return friendsList;
	}

	public boolean  addFriend(String name) {
		return addFriend(name,"");
	}

	public boolean addFriend(String name,String nickName){
		return addFriend(name,nickName,new String[]{"Firends"});
	}

	public boolean addFriend(String name,String nickName,String[] group){
		boolean result = false;
		name += "@" + mConnection.getServiceName();
		try {
			mConnection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);
			mConnection.getRoster().createEntry(name, nickName, group);
			result = true;
		}catch(Exception e){
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	private void configure() {

		// Private Data Storage
		ProviderManager.addIQProvider("query", "jabber:iq:private",
				new PrivateDataManager.PrivateDataIQProvider());

		// Time
		try {
			ProviderManager.addIQProvider("query", "jabber:iq:time",
					Class.forName("org.jivesoftware.smackx.packet.Time"));
		} catch (ClassNotFoundException e) {
			Log.w("TestClient",
					"Can't load class for org.jivesoftware.smackx.packet.Time");
		}

		// Roster Exchange
		// ProviderManager.addExtensionProvider("x","jabber:x:roster", new
		// RosterExchangeProvider());

		// Message Events
		ProviderManager.addExtensionProvider("x", "jabber:x:event",
				new MessageEventProvider());

		// Chat State
		ProviderManager.addExtensionProvider("active",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		ProviderManager.addExtensionProvider("composing",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		ProviderManager.addExtensionProvider("paused",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		ProviderManager.addExtensionProvider("inactive",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());
		ProviderManager.addExtensionProvider("gone",
				"http://jabber.org/protocol/chatstates",
				new ChatStateExtension.Provider());

		// XHTML
		ProviderManager.addExtensionProvider("html",
				"http://jabber.org/protocol/xhtml-im",
				new XHTMLExtensionProvider());

		// Group Chat Invitations
		ProviderManager.addExtensionProvider("x", "jabber:x:conference",
				new GroupChatInvitation.Provider());

		// Service Discovery # Items
		ProviderManager.addIQProvider("query",
				"http://jabber.org/protocol/disco#items",
				new DiscoverItemsProvider());

		// Service Discovery # Info
		ProviderManager.addIQProvider("query",
				"http://jabber.org/protocol/disco#info",
				new DiscoverInfoProvider());

		// Data Forms
		ProviderManager.addExtensionProvider("x", "jabber:x:data",
				new DataFormProvider());

		// MUC User
		ProviderManager.addExtensionProvider("x",
				"http://jabber.org/protocol/muc#user", new MUCUserProvider());

		// MUC Admin
		ProviderManager.addIQProvider("query",
				"http://jabber.org/protocol/muc#admin", new MUCAdminProvider());

		// MUC Owner
		ProviderManager.addIQProvider("query",
				"http://jabber.org/protocol/muc#owner", new MUCOwnerProvider());

		// Delayed Delivery
		ProviderManager.addExtensionProvider("x", "jabber:x:delay",
				new DelayInformationProvider());

		// Version
		try {
			ProviderManager.addIQProvider("query", "jabber:iq:version",
					Class.forName("org.jivesoftware.smackx.packet.Version"));
		} catch (ClassNotFoundException e) {
			// Not sure what's happening here.
		}

		ProviderManager.addIQProvider("query", "jabber:iq:search",
				new UserSearch.Provider());
		
		// VCard
		ProviderManager.addIQProvider("vCard", "vcard-temp",
				new VCardProvider());

		// Offline Message Requests
		ProviderManager.addIQProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageRequest.Provider());

		// Offline Message Indicator
		ProviderManager.addExtensionProvider("offline",
				"http://jabber.org/protocol/offline",
				new OfflineMessageInfo.Provider());

		// Last Activity
		ProviderManager.addIQProvider("query", "jabber:iq:last",
				new LastActivity.Provider());

		// User Search
		ProviderManager.addIQProvider("query", "jabber:iq:search",
				new UserSearch.Provider());

		// SharedGroupsInfo
		ProviderManager.addIQProvider("sharedgroup",
				"http://www.jivesoftware.org/protocol/sharedgroup",
				new SharedGroupsInfo.Provider());

		// JEP-33: Extended Stanza Addressing
		ProviderManager.addExtensionProvider("addresses",
				"http://jabber.org/protocol/address",
				new MultipleAddressesProvider());

		// FileTransfer
		ProviderManager.addIQProvider("si", "http://jabber.org/protocol/si",
				new StreamInitiationProvider());

		ProviderManager.addIQProvider("query",
				"http://jabber.org/protocol/bytestreams",
				new BytestreamsProvider());

		// Privacy
		ProviderManager.addIQProvider("query", "jabber:iq:privacy",
				new PrivacyProvider());
		ProviderManager.addIQProvider("command",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider());
		ProviderManager.addExtensionProvider("malformed-action",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.MalformedActionError());
		ProviderManager.addExtensionProvider("bad-locale",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadLocaleError());
		ProviderManager.addExtensionProvider("bad-payload",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadPayloadError());
		ProviderManager.addExtensionProvider("bad-sessionid",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.BadSessionIDError());
		ProviderManager.addExtensionProvider("session-expired",
				"http://jabber.org/protocol/commands",
				new AdHocCommandDataProvider.SessionExpiredError());
	}

}
