package com.dragon.xchat;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.dragon.xchat.data.Friend;

import java.util.List;

/**
 * Created by zhangfeilong on 2014/12/2.
 */
public class SearchResultFragment extends ListFragment {
    private List<Friend> mFriendsList = null;
    private SearchResultAdapter mSearchResultAdapter = null;
    private UserSearchTask mSearchTask;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public SearchResultFragment () {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Change Adapter to display your content
        mSearchResultAdapter = new SearchResultAdapter(this.getActivity().getApplicationContext());
        mSearchResultAdapter.setList(mFriendsList);
        setListAdapter(mSearchResultAdapter);
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
        if(mSearchTask == null){
            mSearchTask = new UserSearchTask(mFriendsList.get(position).getName());
            mSearchTask.execute();
        }
    }

    public class UserSearchTask extends AsyncTask<Void, Void, Boolean> {

        private  String userName;

        UserSearchTask(String userName) {
            this.userName = userName;
        }

        protected void onPreExecute(Void...params) {

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            boolean result = false;

            if(getActivity() instanceof BaseActivity){
                try {
                    result = ((BaseActivity) getActivity()).mChatService.addFriend(userName);
                }catch(Exception e){
                    e.printStackTrace();
                    result = false;
                }
            }

            return result;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mSearchTask = null;
            if(success){
                Toast.makeText(getActivity().getApplicationContext(),R.string.add_friend_success,Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(getActivity().getApplicationContext(),R.string.add_friend_fail,Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mSearchTask = null;
        }
    }

    public void setFriendsList(List<Friend> list){
        mFriendsList  = list;
        if(mSearchResultAdapter != null){
            mSearchResultAdapter.setList(mFriendsList);
            mSearchResultAdapter.notifyDataSetChanged();
        }
    }
}