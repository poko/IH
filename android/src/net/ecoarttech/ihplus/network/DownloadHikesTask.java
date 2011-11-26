package net.ecoarttech.ihplus.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
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
		builder.appendQueryParameter("latitude", Double.toString(mLat));
		builder.appendQueryParameter("longitude", Double.toString(mLng));
		Uri uri = builder.build();
		Log.d(TAG, "Uri: " + uri);
		HttpGet request = new HttpGet(uri.toString());
		DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
		HttpResponse response;
		try {
			response = httpClient.execute(request);
			return response;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(HttpResponse result) {
		if (result != null) {
			Message msg = new Message();
			msg.what = NetworkConstants.FAILURE;
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
				data.putString(NetworkConstants.HIKES_JSON_KEY, responseJson.getString("hikes"));
				msg.setData(data);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mHandler.sendMessage(msg);
		}
		super.onPostExecute(result);
	}
}
