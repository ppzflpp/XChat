package com.dragon.xchat;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.dragon.xchat.data.Friend;
import com.dragon.xchat.dummyContent.DiscoverContent;
import com.dragon.xchat.network.ConnectorHelper;

public class ChatFragment extends ListFragment {

    private List<Friend> mFriendsList = null;
    private ChatAdapter mChatAdapter = null;
    private String mUserName = null;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment(String userName) {   	
    	mUserName = userName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Change Adapter to display your content
        mChatAdapter = new ChatAdapter(this.getActivity().getApplicationContext());
        mChatAdapter.setList(mFriendsList);
        setListAdapter(mChatAdapter);
    }

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if(position == 0){
        	return;
        }else{
        	Intent intent = new Intent(getActivity(),ChatActivity.class);
        	intent.putExtra("friend", mFriendsList.get(position-1));
        	getActivity().startActivity(intent);
        }
    }
    
    public void setFriendsList(List<Friend> list){
    	mFriendsList  = list;
    	if(mChatAdapter != null){
    		mChatAdapter.setList(mFriendsList);
    		mChatAdapter.notifyDataSetChanged();
    	}
    }

}
