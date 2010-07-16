package com.simplenote.android.ui;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.simplenote.android.Constants;
import com.simplenote.android.Preferences;
import com.simplenote.android.R;
import com.simplenote.android.net.Api.Response;
import com.simplenote.android.net.HttpCallback;
import com.simplenote.android.widget.LoginActionListener;

/**
 * Main Activity for SimpleNote application
 * @author bryanjswift
 */
public class SimpleNoteSplash extends Activity {
	private static final String LOGGING_TAG = Constants.TAG + "SimpleNoteSplash";
	/**
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HashMap<String,String> credentials = Preferences.getLoginPreferences(this);
		getWindow().setFormat(PixelFormat.RGBA_8888);
		setContentView(R.layout.splash);
		Typeface helveticaBold = Typeface.createFromAsset(getAssets(), "fonts/HelveticaNeueBold.ttf");
		((TextView) findViewById(R.id.splashLabel)).setTypeface(helveticaBold);
		if (credentials.containsKey(Preferences.EMAIL) &&
				(credentials.containsKey(Preferences.TOKEN) || credentials.containsKey(Preferences.PASSWORD))) {
			// valid token stored
			Log.d(LOGGING_TAG, "Auth information stored, going to list");
			FireIntent.SimpleNoteList(this);
			this.finish();
		} else {
			// set up events for the splash screen
			Log.d(LOGGING_TAG, "Need to get credentials, setup events for the splash screen");
			setupSplashFields();
		}
	}
	/**
	 * Attach focus event listeners to the EditTexts in the layout
	 */
	private void setupSplashFields() {
		final EditText email = (EditText) findViewById(R.id.splash_email);
		final EditText password = (EditText) findViewById(R.id.splash_password);
		password.setOnEditorActionListener(new LoginActionListener(this, email, password, new HttpCallback() {
			/**
			 * @see com.simplenote.android.net.HttpCallback#on200(com.simplenote.android.net.Api.Response)
			 */
			@Override
			public void on200(Response response) {
				FireIntent.SimpleNoteList(SimpleNoteSplash.this);
				SimpleNoteSplash.this.finish();
			}
		}));
	}
}
