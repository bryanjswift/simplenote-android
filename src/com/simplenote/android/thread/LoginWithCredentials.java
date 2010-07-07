package com.simplenote.android.thread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.widget.Toast;

import com.simplenote.android.Constants;
import com.simplenote.android.Preferences;
import com.simplenote.android.R;
import com.simplenote.android.net.Api.Response;
import com.simplenote.android.net.HttpCallback;
import com.simplenote.android.net.SimpleNoteApi;
import com.simplenote.android.ui.FireIntent;

/**
 * Uses the SimpleNoteApi to login with existing credentials which must be provided
 * @author bryanjswift
 */
public class LoginWithCredentials extends Thread {
	private static final String LOGGING_TAG = Constants.TAG + "LoginWithExistingCredentials";
	private final Activity context;
	private final HashMap<String,String> credentials;
	/**
	 * Create new specialized Thread with credentials information
	 * @param context from which the thread was invoked
	 * @param credentials information to use when attempting to re-authenticate
	 */
	public LoginWithCredentials(Activity context, HashMap<String,String> credentials) {
		this.context = context;
		this.credentials = credentials;
	}
	/**
	 * Send a login request to the SimpleNote API
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		SimpleNoteApi.login(credentials.get(Preferences.EMAIL), credentials.get(Preferences.PASSWORD), new HttpCallback() {
			/**
			 * Authentication was successful, store the token in the preferences and start the list activity
			 * @see com.simplenote.android.net.HttpCallback#on200(java.lang.String)
			 */
			@Override
			public void on200(final Response response) {
				SharedPreferences prefs = context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
				Editor editor = prefs.edit();
				// API successfully returned, store token
				editor.putString(Preferences.TOKEN, response.body);
				if (editor.commit()) {
					Log.i(LOGGING_TAG, "Successfully saved new authentication token");
				} else {
					Log.i(LOGGING_TAG, "Failed to save new authentication token, uh oh.");
				}
				// start note list activity
				FireIntent.SimpleNoteList(context);
			}
			/**
			 * Authentication failed, show login dialog
			 * @see com.simplenote.android.net.HttpCallback#on401(java.lang.String)
			 */
				FireIntent.SigninDialog(context);
				Log.d(LOGGING_TAG, String.format("Authentication failed with status code %i", status));
				Toast.makeText(context, R.string.error_authentication_stored, Toast.LENGTH_LONG).show();
			@Override
			public void onError(final Response response) {
			}
		});
	}
}
