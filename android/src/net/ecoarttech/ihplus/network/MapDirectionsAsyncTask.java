package net.ecoarttech.ihplus.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class MapDirectionsAsyncTask extends AsyncTask<Void, Void, HttpResponse> {

	private static final String TAG = "MapDirectionsAsyncTask";
	public final static String SERVER_URL = "http://maps.google.com/maps/api/directions/json";
	protected Context mContext;
	protected HashMap<String, Object> mRequestQueries;
	protected HttpResponse mResponse;
	protected MapDirectionCompletionListener mCompletionListener;
	protected boolean mShowDialog = false;
	protected ProgressDialog mDialog;
	protected String mLabel = "Loading";

	public MapDirectionsAsyncTask(Context context, String from, String to,
			MapDirectionCompletionListener listener) {
		this.mCompletionListener = listener;
		this.mContext = context;
		mRequestQueries = new HashMap<String, Object>();
		mRequestQueries.put("sensor", "true");
		mRequestQueries.put("origin", from);
		mRequestQueries.put("destination", to);
		mRequestQueries.put("waypoints", "30.27497,-97.74141"); // TODO'

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
		Uri.Builder uriBuilder = Uri.parse(SERVER_URL).buildUpon();
		for (String key : mRequestQueries.keySet()) {
			if (mRequestQueries.get(key) != null) {
				uriBuilder.appendQueryParameter(key, (String) mRequestQueries
						.get(key));
			}
		}
		Uri uri = uriBuilder.build();
		Log.d(TAG, "Uri: " + uri);
		DefaultHttpClient httpClient = new DefaultHttpClient(
				new BasicHttpParams());
		// Create local HTTP context
		HttpContext localContext = new BasicHttpContext();
		HttpUriRequest request;
		request = new HttpGet(uri.toString());

		try {
			return httpClient.execute(request, localContext);
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
		StringBuilder responseText = readResponse(result);
		Log.d(TAG, "Response: " + responseText);
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
