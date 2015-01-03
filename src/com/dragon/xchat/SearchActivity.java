package com.dragon.xchat;

import android.app.ListFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;
import com.dragon.xchat.data.ChatMessage;
import com.dragon.xchat.data.Friend;

import java.util.List;


public class SearchActivity extends BaseActivity{

	private AddTask mAuthTask = null;
	private SearchResultFragment mSearchResultListFragment = null;
	private SearchView mSearchView;
	private List<Friend> mFriends;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);

		mSearchResultListFragment =(SearchResultFragment) this.getFragmentManager().findFragmentById(R.id.search_result_list);

		mSearchView = (SearchView)LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.actionbar_search_view, null);
		mSearchView.setOnQueryTextListener(new OnQueryTextListener(){

			@Override
			public boolean onQueryTextChange(String arg0) {
				// TODO Auto-generated method stub
				return true;
			}

			@Override
			public boolean onQueryTextSubmit(String arg0) {
				// TODO Auto-generated method stub
				if(mAuthTask == null){
					mAuthTask = new AddTask(arg0);
					mAuthTask.execute();
				}
				return true;
			}
			
		});

		this.getActionBar().setDisplayShowHomeEnabled(true);
		this.getActionBar().setDisplayShowCustomEnabled(true);
		this.getActionBar().setCustomView(mSearchView);

	}

	
	public class AddTask extends AsyncTask<Void, Void, Boolean> {

		private  String userName;

		AddTask(String userName) {
			this.userName = userName;
		}
		
		protected void onPreExecute(Void...params) {
			
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = false;
			
			Log.d("TAG", "doInBackground");
			try {
				if (mChatService != null) {
					List<Friend> friends = mChatService.searchFriend(userName);
					if(friends == null) {
						mFriends = null;
						return false;
					}else {
						mFriends = friends;
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return result;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			if(success){
				mSearchResultListFragment.setFriendsList(mFriends);
			}else{
				Toast.makeText(getApplicationContext(),R.string.search_no_friends,Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			
		}
	}
	
	protected void onDestroy(){
		super.onDestroy(null);
	}


	@Override
	public void onServiceConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceDisconnected() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void messageRefresh(ChatMessage msg) {
		// TODO Auto-generated method stub
		
	}

	
}
