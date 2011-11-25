package net.ecoarttech.ihplus.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.ecoarttech.ihplus.model.Hike;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

public class UploadHikeTask extends AsyncTask<Void, Void, HttpResponse> {

	private static final String TAG = "IH+ - UploadHikeTask";
	private Hike mHike;
	private Handler mHandler;

	public UploadHikeTask(Hike hike, Handler handler) {
		this.mHike = hike;
		this.mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected HttpResponse doInBackground(Void... arg0) {
		Uri.Builder builder = Uri.parse(NetworkConstants.UPLOAD_HIKE_URL).buildUpon();
		Uri uri = builder.build();
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("hike_name", mHike.getName()));
		parameters.add(new BasicNameValuePair("username", "testName")); // TODO
		// parameters.add(new BasicNameValuePair("vistas", mHike.getVistas()));
		parameters.add(new BasicNameValuePair("start_lat", mHike.getStartLat().toString()));
		parameters.add(new BasicNameValuePair("start_lng", mHike.getStartLng().toString()));
		try {
			Log.d(TAG, "Uri: " + uri);
			HttpPost request = new HttpPost(uri.toString());
			request.setEntity(new UrlEncodedFormEntity(parameters));
			DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
			HttpResponse response;
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

			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		super.onPostExecute(result);
	}
}
