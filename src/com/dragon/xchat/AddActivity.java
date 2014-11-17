package com.dragon.xchat;

import com.dragon.xchat.data.ChatMessage;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;



public class AddActivity extends BaseActivity{

	private AddTask mAuthTask = null;
	private ListView mSearchResultListView = null;
	private SearchView mSearchView;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add);
		
		mSearchResultListView = (ListView)findViewById(R.id.search_result_list);
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
			
			Log.d("TAG","doInBackground");
	
			return result;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			
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
	public void onServiceDisonnected() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void messageRefresh(ChatMessage msg) {
		// TODO Auto-generated method stub
		
	}

	
}
