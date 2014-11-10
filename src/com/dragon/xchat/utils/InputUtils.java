package com.dragon.xchat.utils;

import android.content.Context;
import android.widget.Toast;

import com.dragon.xchat.R;

public class InputUtils {

	private final static int USER_NAME_MIN_LENGTH = 6;
	private final static int USER_PASSWORD_MIN_LENGTH = 6;

	public static boolean checkInput(Context context, String userName,
			String passoword) {
		if (userName == null || userName.length() < USER_NAME_MIN_LENGTH) {
			Toast.makeText(
					context.getApplicationContext(),
					context.getString(R.string.user_name_length_fail,
							USER_NAME_MIN_LENGTH), Toast.LENGTH_SHORT).show();
			return false;
		}
		// check passowrd

		if (passoword == null || passoword.length() < USER_PASSWORD_MIN_LENGTH) {
			Toast.makeText(
					context.getApplicationContext(),
					context.getString(R.string.user_password_length_fail,
							USER_PASSWORD_MIN_LENGTH), Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		return true;
	}
}
