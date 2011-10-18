package net.ecoarttech.ihplus.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class DirectionsAsyncTask extends AsyncTask<Void, Void, HttpResponse> {

	private static final String TAG = "DirectionsAsyncTask";
	public final static String SERVER_URL = "http://maps.google.com/maps";
	protected Context mContext;
	protected HashMap<String, Object> mRequestQueries;
	protected HttpResponse mResponse;
	protected DirectionCompletionListener mCompletionListener;
	protected boolean mShowDialog = false;
	protected ProgressDialog mDialog;
	protected String mLabel = "Loading";

	public DirectionsAsyncTask(Context context, String from, String to,
			DirectionCompletionListener listener) {
		this.mCompletionListener = listener;
		this.mContext = context;
		mRequestQueries = new HashMap<String, Object>();
		mRequestQueries.put("f", "d");
		mRequestQueries.put("hl", "en");
		mRequestQueries.put("saddr", from);
		mRequestQueries.put("daddr", to);
		mRequestQueries.put("ie", "UTF8&0");
		mRequestQueries.put("om", "0");
		mRequestQueries.put("output", "kml");
	}

	protected DirectionsAsyncTask(Context context,
			DirectionCompletionListener listener) {
		this.mCompletionListener = listener;
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (mShowDialog) {
			mDialog = new ProgressDialog(mContext);
			mDialog.setMessage(mLabel);
			mDialog.show();
		}
	}

	@Override
	protected HttpResponse doInBackground(Void... arg0) {
		HttpClient httpClient = new DefaultHttpClient();
		// Create local HTTP context
		HttpContext localContext = new BasicHttpContext();
		Uri.Builder uri = Uri.parse(SERVER_URL).buildUpon();
		for (String key : mRequestQueries.keySet()) {
			if (mRequestQueries.get(key) != null) {
				uri
						.appendQueryParameter(key, (String) mRequestQueries
								.get(key));
			}
		}

		Log.d(TAG, "Uri: " + SERVER_URL);
		HttpGet request = new HttpGet(SERVER_URL.toString());
		try {
			mResponse = httpClient.execute(request, localContext);
			return mResponse;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		super.onPostExecute(result);
		if (mDialog != null)
			mDialog.dismiss();
		// parse http response
		// try {
		StringBuilder responseText = readResponse(result);
		Log.d(TAG, "Server response: " + responseText);
		if (mCompletionListener != null)
			mCompletionListener.onComplete(responseText.toString());
	}

	protected StringBuilder readResponse(HttpResponse result) {
		StringBuilder responseText = new StringBuilder();
		try {
			InputStreamReader is = new InputStreamReader(result.getEntity()
					.getContent());
			BufferedReader br = new BufferedReader(is);
			String line;
			while ((line = br.readLine()) != null) {
				responseText.append(line);
			}
			is.close();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return responseText;
	}

	public void addRequestParam(String key, Object value) {
		mRequestQueries.put(key, value);
	}

	public void showDialog() {
		mShowDialog = true;
	}

	public void showDialog(String label) {
		mShowDialog = true;
		mLabel = label;
	}

}
