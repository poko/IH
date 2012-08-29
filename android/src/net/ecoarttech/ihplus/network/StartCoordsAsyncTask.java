package net.ecoarttech.ihplus.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class StartCoordsAsyncTask extends AsyncTask<Void, Void, String> {

	private static final String TAG = "IH+ - StartCoordsAsyncTask";
	public final static String SERVER_URL = "http://maps.googleapis.com/maps/api/geocode/json";// "http://maps.google.com/maps";
	protected Context mContext;
	protected HashMap<String, Object> mRequestQueries;
	protected HttpResponse mResponse;
	protected DirectionCompletionListener mCompletionListener;
	protected boolean mShowDialog = false;
	protected ProgressDialog mDialog;
	protected String mLabel = "Loading";

	// private String location;

	public StartCoordsAsyncTask(Context context, String location, DirectionCompletionListener listener) {
		this.mCompletionListener = listener;
		this.mContext = context;
		// this.location = location;
		mRequestQueries = new HashMap<String, Object>();
		mRequestQueries.put("sensor", "true");
		mRequestQueries.put("address", location);
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
	protected String doInBackground(Void... arg0) {
		Uri.Builder uriBuilder = Uri.parse(SERVER_URL).buildUpon();
		for (String key : mRequestQueries.keySet()) {
			if (mRequestQueries.get(key) != null) {
				uriBuilder.appendQueryParameter(key, (String) mRequestQueries.get(key));
			}
		}
		URL url;
		try {
			Uri uri = uriBuilder.build();
			// uriStr = uriStr + "&q=" + location;
			// url = new URL(uriStr);

			Log.d(TAG, "Uri: " + uri);
			HttpGet request = new HttpGet(uri.toString());

			DefaultHttpClient httpClient = new DefaultHttpClient(NetworkConstants.getHttpParams());
			HttpResponse response = httpClient.execute(request);
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				Log.d(TAG, "status code: " + statusCode);
				// get message
				StringBuilder responseText = new StringBuilder();
				try {
					InputStreamReader is = new InputStreamReader(response.getEntity().getContent());
					BufferedReader br = new BufferedReader(is);
					String line;
					while ((line = br.readLine()) != null) {
						responseText.append(line);
					}
					Log.d(TAG, "Server response: " + responseText);
				} catch (Exception e) {
					Log.d(TAG, e.toString());
				}
				return responseText.toString();
			}
			// HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			// urlConnection.setRequestMethod("GET");
			// urlConnection.setDoOutput(true);
			// urlConnection.setDoInput(true);
			// urlConnection.connect();
			// DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			// DocumentBuilder db = dbf.newDocumentBuilder();
			// Document doc = db.parse(urlConnection.getInputStream());
			// return doc;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (mDialog != null)
			mDialog.dismiss();
		if (mCompletionListener != null)
			mCompletionListener.onComplete(result);
	}
}
