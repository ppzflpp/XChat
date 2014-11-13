package com.dragon.xchat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dragon.xchat.network.ConnectorHelper;
import com.dragon.xchat.service.ChatService;
import com.dragon.xchat.service.IChatService;
import com.dragon.xchat.utils.InputUtils;
import com.dragon.xchat.utils.LogUtils;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity {

	private UserLoginTask mAuthTask = null;

	// UI references.
	private AutoCompleteTextView mUserNameView;
	private EditText mPasswordView;
	private Button mRegisterButton;
	private Button mLoginButton;

	private String mUserNameValue;
	private String mPasswordValue;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUserNameView = (AutoCompleteTextView) findViewById(R.id.username);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		mRegisterButton = (Button) findViewById(R.id.register_button);
		mRegisterButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				registerUser();
			}
		});

		mLoginButton = (Button) findViewById(R.id.login_button);
		mLoginButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});
	}

	private void registerUser() {
		Intent intent = new Intent();
		intent.setClass(this, RegisterActivity.class);
		this.startActivity(intent);
	}

	public void attemptLogin() {
		mUserNameValue = mUserNameView.getText().toString();
		mPasswordValue = mPasswordView.getText().toString();
		boolean result = InputUtils.checkInput(this, mUserNameValue,
				mPasswordValue);
		if (result){
			if(mAuthTask == null){
				mAuthTask = new UserLoginTask(mUserNameValue,mPasswordValue);
				mAuthTask.execute();
			}
		}
			
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		
	}



	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String userName;
		private final String password;

		UserLoginTask(String userName, String password) {
			this.userName = userName;
			this.password = password;
		}
		
		protected void onPreExecute(Void...params) {
			mLoginButton.setEnabled(false);
			mLoginButton.setText(R.string.user_logining);
			mRegisterButton.setEnabled(false);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = false;
			
			try {
				result = mChatService.login(userName, password);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
			return result;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, MainActivity.class);
				intent.putExtra("user_name",userName);
				startActivity(intent);
				finish();
			} else {
				Toast.makeText(getApplicationContext(), R.string.login_fail, Toast.LENGTH_SHORT).show();
				mLoginButton.setEnabled(true);
				mLoginButton.setText(R.string.action_login);
				mRegisterButton.setEnabled(true);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	protected void onDestroy(){
		super.onDestroy(null);
	}
}
