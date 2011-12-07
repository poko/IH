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

public class DownloadHikesTask extends AsyncTask<Void, Void, HttpResponse> {

	private static final String TAG = "IH+ - DownloadHikesTask";
	private Handler mHandler;
	private double mLat;
	private double mLng;

	public DownloadHikesTask(Handler handler, double lat, double lng) {
		this.mHandler = handler;
		this.mLat = lat;
		this.mLng = lng;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected HttpResponse doInBackground(Void... arg0) {
		Uri.Builder builder = Uri.parse(NetworkConstants.SEARCH_HIKES_URL).buildUpon();
		builder.appendQueryParameter(NetworkConstants.REQUEST_JSON_HIKES_LAT, Double.toString(mLat));
		builder.appendQueryParameter(NetworkConstants.REQUEST_JSON_HIKES_LNG, Double.toString(mLng));
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
				// parse out vista_actions
				JSONObject responseJson = new JSONObject(responseText.toString());
				msg.what = NetworkConstants.SUCCESS;
				Bundle data = new Bundle();
				data.putString(NetworkConstants.HIKES_JSON_KEY, responseJson.getString(NetworkConstants.RESPONSE_JSON_HIKES));
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
