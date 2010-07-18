package com.bryanjswift.simplenote.net;

import java.util.HashMap;

import android.app.Activity;
import android.util.Log;

import com.bryanjswift.simplenote.Constants;
import com.bryanjswift.simplenote.Preferences;
import com.bryanjswift.simplenote.model.Note;
import com.bryanjswift.simplenote.net.SimpleNoteApi;
import com.bryanjswift.simplenote.net.Api.Response;
import com.bryanjswift.simplenote.persistence.SimpleNoteDao;

/**
 * Specialized HttpCallback to handle respsonses when trying to save notes to the server
 * @author bryanjswift
 */
public class ServerSaveCallback extends HttpCallback {
	private static final String LOGGING_TAG = Constants.TAG + "ServerSaveCallback";
	// Inheritable fields
	protected final SimpleNoteDao dao;
	protected final Activity context;
	protected final Note note;
	/**
	 * Create a callback related to the note which was saved
	 * @param note trying to be saved to the server
	 */
	public ServerSaveCallback(final Activity context, final Note note) {
		super();
		this.note = note;
		this.context = context;
		this.dao = new SimpleNoteDao(context);
	}
	/**
	 * @see com.bryanjswift.simplenote.net.HttpCallback#on200(com.bryanjswift.simplenote.net.Api.Response)
	 */
	@Override
	public void on200(Response response) {
		super.on200(response);
		Log.d(LOGGING_TAG, String.format("Successfully saved note '%s' on server", response.body));
		// Set needs sync to false in db
		dao.markSynced(note);
	}
	/**
	 * @see com.bryanjswift.simplenote.net.HttpCallback#on401(com.bryanjswift.simplenote.net.Api.Response)
	 */
	@Override
	public void on401(Response response) {
		super.on401(response);
		Log.d(LOGGING_TAG, "Unauthorized to save note on server");
		// User unauthorized, get new token and try again
	}
	/**
	 * @see com.bryanjswift.simplenote.net.HttpCallback#on404(com.bryanjswift.simplenote.net.Api.Response)
	 */
	@Override
	public void on404(Response response) {
		super.on404(response);
		Log.d(LOGGING_TAG, "Note not found on server");
		// Note doesn't exist, create it
		final HashMap<String,String> credentials = Preferences.getLoginPreferences(context);
		final String email = credentials.get(Preferences.EMAIL);
		final String auth = credentials.get(Preferences.TOKEN);
		SimpleNoteApi.create(note, auth, email, new ServerCreateCallback(context, note));
	}
}