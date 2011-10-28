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

public class DirectionsAsyncTask extends AsyncTask<Void, Void, Document> {

	private static final String TAG = "DirectionsAsyncTask";
	public final static String SERVER_URL = "http://maps.google.com/maps";
	protected Context mContext;
	protected HashMap<String, Object> mRequestQueries;
	protected HttpResponse mResponse;
	protected DirectionCompletionListener mCompletionListener;
	protected boolean mShowDialog = false;
	protected ProgressDialog mDialog;
	protected String mLabel = "Loading";
	private String sAdd;
	private String dAdd;

	public DirectionsAsyncTask(Context context, String from, String to,
			DirectionCompletionListener listener) {
		this.mCompletionListener = listener;
		this.mContext = context;
		sAdd = from;
		dAdd = to;
		mRequestQueries = new HashMap<String, Object>();
		mRequestQueries.put("f", "d");
		mRequestQueries.put("hl", "en");
		// mRequestQueries.put("saddr", from);
		// mRequestQueries.put("daddr", to);
		mRequestQueries.put("ie", "UTF8&0");
		mRequestQueries.put("om", "0");
		mRequestQueries.put("output", "kml");
		mRequestQueries.put("dirflg", "w");
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
				uriBuilder.appendQueryParameter(key, (String) mRequestQueries
						.get(key));
			}
		}
		URL url;
		try {
			String uriStr = uriBuilder.build().toString();
			uriStr = uriStr + "&saddr=" + sAdd;
			uriStr = uriStr + "&daddr=" + dAdd;
			url = new URL(uriStr);
			Log.d(TAG, "Uri: " + url);
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);
			urlConnection.setDoInput(true);
			urlConnection.connect();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(urlConnection.getInputStream());
			return doc;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
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