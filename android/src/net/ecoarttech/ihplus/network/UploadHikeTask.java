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
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UploadHikeTask extends AsyncTask<Void, Void, HttpResponse> {

	private static final String TAG = "IH+ - UploadHikeTask";
	private Context mContext;
	private Hike mHike;
	private Handler mHandler;

	public UploadHikeTask(Context context, Hike hike, Handler handler) {
		this.mContext = context;
		this.mHike = hike;
		this.mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
		// TODO - add progress dialog.
	}

	@Override
	protected HttpResponse doInBackground(Void... arg0) {
		Uri.Builder builder = Uri.parse(NetworkConstants.UPLOAD_HIKE_URL).buildUpon();
		Uri uri = builder.build();
		ArrayList<NameValuePair> parameters = new ArrayList<NameValuePair>();
		parameters.add(new BasicNameValuePair("hike_name", mHike.getName()));
		parameters.add(new BasicNameValuePair("username", mHike.getUsername()));
		parameters.add(new BasicNameValuePair("description", mHike.getDescription()));
		parameters.add(new BasicNameValuePair("vistas", mHike.getVistasAsJson(mContext)));
		parameters.add(new BasicNameValuePair("start_lat", mHike.getStartLat().toString()));
		parameters.add(new BasicNameValuePair("start_lng", mHike.getStartLng().toString()));
		// TODO - if vistas are photos, add photos
		try {
			Log.d(TAG, "Uri: " + uri);
			Log.d(TAG, "params: " + parameters);
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
				// TODO - parse out success/failure
				try {
					JSONObject respJson = new JSONObject(responseText.toString());
					if (respJson.getBoolean(NetworkConstants.SERVER_RESULT))
						msg.what = NetworkConstants.SUCCESS;
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mHandler.sendMessage(msg);
		}
		super.onPostExecute(result);
	}
}
