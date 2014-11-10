package com.dragon.xchat;

import com.dragon.xchat.network.ConnectorHelper;
import com.dragon.xchat.utils.InputUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class RegisterActivity extends BaseActivity {

	
	private Button mRegisterButton;
	private EditText mUserName;
	private EditText mPassword;
	private EditText mPasswordConfirm;
	
	private String mUserNameValue;
	private String mPasswordValue;		
	
	private UserRegisterTask mRegisterTask = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
		setupViews();
	}
	
	private void setupViews(){
		mUserName = (EditText)findViewById(R.id.register_user_name);
		mPassword = (EditText)findViewById(R.id.register_password);
		mPasswordConfirm = (EditText)findViewById(R.id.register_password_confirm);
		
		mRegisterButton = (Button)findViewById(R.id.register_confirm);
		mRegisterButton.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				
				if(checkInput() && mRegisterTask == null){
					mRegisterTask = new UserRegisterTask(mUserNameValue,mPasswordValue);
					mRegisterTask.execute();
				}
			}
			
		});
	}
	
	private boolean checkInput(){
		//check username
		mUserNameValue = mUserName.getText().toString();
		mPasswordValue = mPassword.getText().toString();
		
		boolean result = InputUtils.checkInput(this, mUserNameValue, mPasswordValue);
		if(!result)
			return false;
		
		//check confirm password
		String confirmPassword = mPasswordConfirm.getText().toString();
		if(!mPasswordValue.equals(confirmPassword)){
			Toast.makeText(getApplicationContext(),
					R.string.passwork_not_same, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		//check success
		return true;
	}
	
	public class UserRegisterTask extends AsyncTask<Void,Integer, Boolean> {


		private final String userName;
        private final String password;

        UserRegisterTask(String userName, String password) {
        	this.userName = userName;
        	this.password = password;
        }
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mRegisterButton.setEnabled(false);
			mRegisterButton.setText(R.string.user_registering);
		}
        
        @Override
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
        	boolean success = false;
            try {
            	// TODO Auto-generated method stub
				success = ConnectorHelper.getInstance(RegisterActivity.this).connect();
				if(success){
					Log.d("TAG","connect success,begin to register");
					success = ConnectorHelper.getInstance(RegisterActivity.this).register(userName,password);
					if(success)
						Log.d("TAG","register success");
					else
						Log.d("TAG","register fail");
				}else{
					Log.d("TAG","connect fail");
				}
				
            } catch (Exception e) {
                return false;
            }

            // TODO: register the new account here.
            return success;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mRegisterTask = null;
            if(success){
            	Toast.makeText(RegisterActivity.this.getApplicationContext(),
            			R.string.register_success, Toast.LENGTH_SHORT).show();
            	finish();
            }
            else{
            	Toast.makeText(RegisterActivity.this.getApplicationContext(),
            			R.string.register_fail, Toast.LENGTH_SHORT).show();
            	mRegisterButton.setEnabled(true);
    			mRegisterButton.setText(R.string.action_register);
            }
        
        }

        @Override
        protected void onCancelled() {
            mRegisterTask = null;
        }
    }
	
	protected void onDestroy(){
		super.onDestroy(null);
	}
}
