package net.ecoarttech.ihplus.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class StartCoordsAsyncTask extends AsyncTask<Void, Void, Document> {

	private static final String TAG = "IH+ - StartCoordsAsyncTask";
	public final static String SERVER_URL = "http://maps.google.com/maps";
	protected Context mContext;
	protected HashMap<String, Object> mRequestQueries;
	protected HttpResponse mResponse;
	protected DirectionCompletionListener mCompletionListener;
	protected boolean mShowDialog = false;
	protected ProgressDialog mDialog;
	protected String mLabel = "Loading";
	private String location;

	public StartCoordsAsyncTask(Context context, String location, DirectionCompletionListener listener) {
		this.mCompletionListener = listener;
		this.mContext = context;
		this.location = location;
		mRequestQueries = new HashMap<String, Object>();
		mRequestQueries.put("hl", "en");
		mRequestQueries.put("output", "kml");
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
	protected Document doInBackground(Void... arg0) {
		Uri.Builder uriBuilder = Uri.parse(SERVER_URL).buildUpon();
		for (String key : mRequestQueries.keySet()) {
			if (mRequestQueries.get(key) != null) {
				uriBuilder.appendQueryParameter(key, (String) mRequestQueries.get(key));
			}
		}
		URL url;
		try {
			String uriStr = uriBuilder.build().toString();
			uriStr = uriStr + "&q=" + location;
			url = new URL(uriStr);
			Log.d(TAG, "Uri: " + url);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(urlConnection.getInputStream());
			return doc;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(Document result) {
		super.onPostExecute(result);
		if (mDialog != null)
			mDialog.dismiss();
		if (mCompletionListener != null)
			mCompletionListener.onComplete(result);
	}
}
