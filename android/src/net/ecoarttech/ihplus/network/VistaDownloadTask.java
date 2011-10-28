package net.ecoarttech.ihplus.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.ecoarttech.ihplus.model.ScenicVista;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class VistaDownloadTask extends AsyncTask<Void, Void, Document> {

	private static final String TAG = "IH+ - VistaDownloadTask";
	public final static String SERVER_URL = "";
	private ArrayList<ScenicVista> mVistas;

	public VistaDownloadTask(ArrayList<ScenicVista> vistas) {
		this.mVistas = vistas;
	}

	@Override
	protected void onPreExecute() {
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
	}

}
