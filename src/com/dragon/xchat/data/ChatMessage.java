package com.dragon.xchat.data;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatMessage implements Parcelable{
	private String from;
	private String to;
	private String jId;
	private String body;
	private boolean isRead;

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getjId() {
		return jId;
	}

	public void setjId(String jId) {
		this.jId = jId;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public boolean isRead(){
		return isRead;
	}
	
	public void setIsRead(boolean isRead){
		this.isRead = isRead;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int arg1) {
		// TODO Auto-generated method stub
		parcel.writeString(from);
		parcel.writeString(to);
		parcel.writeString(jId);
		parcel.writeString(body);
		parcel.writeInt(isRead == true ? 1 : 0);
		
	}
	
	public static final Parcelable.Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {  
		@Override  
		public ChatMessage createFromParcel(Parcel source) {  
			ChatMessage msg = new ChatMessage();  
			msg.from = source.readString();
			msg.to = source.readString();
			msg.jId = source.readString();
			msg.body = source.readString();
			msg.isRead = source.readInt() == 1; 
			return msg;  
		}

		@Override
		public ChatMessage[] newArray(int arg0) {
			// TODO Auto-generated method stub
			return new ChatMessage[1];
		} 
	};
}
