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

public class DownloadExistingHike extends AsyncTask<Void, Void, HttpResponse> {

	private static final String TAG = "IH+ - DownloadExistingHikeTask";
	private Handler mHandler;
	private int mHikeId;

	public DownloadExistingHike(Handler handler, int id) {
		this.mHandler = handler;
		this.mHikeId = id;
	}

	@Override
	protected void onPreExecute() {
		// TODO - dialog? 
//		mDialog = new ProgressDialog(mContext);
//		mDialog.setMessage("Downloading Hike");
//		mDialog.show();
	}

	@Override
	protected HttpResponse doInBackground(Void... arg0) {
		Uri.Builder builder = Uri.parse(NetworkConstants.GET_HIKE_URL).buildUpon();
		builder.appendQueryParameter("hike_id", Integer.toString(mHikeId));
		Uri uri = builder.build();
		Log.d(TAG, "Uri: " + uri);
		HttpGet request = new HttpGet(uri.toString());
		DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
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
				msg.what = NetworkConstants.SUCCESS;
				Bundle data = new Bundle();
				data.putString(NetworkConstants.HIKE_JSON_KEY, responseJson.getString("hike"));
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
