package net.ecoarttech.ihplus.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import net.ecoarttech.ihplus.model.ScenicVista;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownloadVistaActionsTask extends AsyncTask<Void, Void, HttpResponse> {

	private static final String TAG = "IH+ - VistaDownloadTask";
	private ArrayList<ScenicVista> mVistas;
	private Handler mHandler;

	public DownloadVistaActionsTask(ArrayList<ScenicVista> vistas, Handler handler) {
		this.mVistas = vistas;
		this.mHandler = handler;
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected HttpResponse doInBackground(Void... arg0) {
		Uri.Builder builder = Uri.parse(NetworkConstants.GET_VISTA_URL).buildUpon();
		builder.appendQueryParameter(NetworkConstants.REQUEST_JSON_VISTAS_AMOUNT, Integer.toString(mVistas.size()));
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
				// parse out vista_actions
				JSONObject responseJson = new JSONObject(responseText.toString());
				JSONArray vistaActions = responseJson.getJSONArray(NetworkConstants.RESPONSE_JSON_VISTAS_ACTIONS);
				for (int i = 0; i < mVistas.size(); i++) {
					ScenicVista v = mVistas.get(i);
					JSONObject vistaJson = vistaActions.getJSONObject(i);
					v.setActionId(vistaJson.getInt(NetworkConstants.RESPONSE_JSON_VISTAS_ID));
					v.setAction(vistaJson.getString(NetworkConstants.RESPONSE_JSON_VISTAS_VERBIAGE));
					v.setActionType(vistaJson.getString(NetworkConstants.RESPONSE_JSON_VISTAS_TYPE));
				}
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
