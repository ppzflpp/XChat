package com.dragon.xchat;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.dragon.xchat.data.Friend;
import com.dragon.xchat.dummyContent.DiscoverContent;
import com.dragon.xchat.network.ConnectorHelper;

public class ChatFragment extends ListFragment {

    private FriendsTask mFriendsTask = null;
    private List<Friend> mFriendsList = null;
    private ChatAdapter mChatAdapter = null;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChatFragment(String userName) {   	
    	if(mFriendsTask == null){
    		mFriendsTask = new FriendsTask(userName);
    		mFriendsTask.execute();
    	}
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Change Adapter to display your content
        mChatAdapter = new ChatAdapter(this.getActivity().getApplicationContext());
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

    
    class FriendsTask extends AsyncTask<Void,Void,Boolean>{
    	
    	private String userName = null;
    	
    	public FriendsTask(String userName){
    		this.userName = userName;
    	}

		@Override
		protected Boolean doInBackground(Void... arg0) {
			// TODO Auto-generated method stub			
			mFriendsList = ConnectorHelper.getInstance(getActivity()).getAllFriends();
			if(mFriendsList == null)
				return false;
			return true;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			Log.d("TAG","result = " + result);
			if(result){
				mChatAdapter.setList(mFriendsList);
				mChatAdapter.notifyDataSetChanged();
			}
			
		}
    	
    }

}
