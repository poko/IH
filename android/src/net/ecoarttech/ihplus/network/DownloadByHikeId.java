package net.ecoarttech.ihplus.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownloadByHikeId extends AsyncTask<Void, Void, HttpResponse> {

	private static final String TAG = "IH+ - DownloadExistingHikeTask";
	private Handler mHandler;
	private int mHikeId;
	private String mUrl;
	private String mResponseField;

	public DownloadByHikeId(Handler handler, int id, String url, String responseField) {
		this.mHandler = handler;
		this.mHikeId = id;
		this.mUrl = url;
		this.mResponseField = responseField;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected HttpResponse doInBackground(Void... arg0) {
		Uri.Builder builder = Uri.parse(mUrl).buildUpon();
		builder.appendQueryParameter(NetworkConstants.REQUEST_JSON_HIKE_ID, Integer.toString(mHikeId));
		Uri uri = builder.build();
		Log.d(TAG, "Uri: " + uri);
		HttpGet request = new HttpGet(uri.toString());
		DefaultHttpClient httpClient = new DefaultHttpClient(NetworkConstants.getHttpParams());
		HttpResponse response;
		try {
			response = httpClient.execute(request);
			return response;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		Message msg = new Message();
		msg.what = NetworkConstants.FAILURE;
		if (result != null) {
			try {
				InputStreamReader is = new InputStreamReader(result.getEntity().getContent());
				BufferedReader br = new BufferedReader(is);
				StringBuilder responseText = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
					responseText.append(line);
				}
				Log.d(TAG, "Server response: " + responseText);
				// parse out hike data
				JSONObject responseJson = new JSONObject(responseText.toString());
				String response = responseJson.getString(mResponseField);
				msg.what = NetworkConstants.SUCCESS;
				Bundle data = new Bundle();
				data.putString(NetworkConstants.HIKE_JSON_KEY, response);
				msg.setData(data);
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		mHandler.sendMessage(msg);
		super.onPostExecute(result);
	}
}
