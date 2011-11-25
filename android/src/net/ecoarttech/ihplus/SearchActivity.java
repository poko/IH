package net.ecoarttech.ihplus;

import java.util.ArrayList;

import net.ecoarttech.ihplus.adapter.SearchListAdapter;
import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.network.DownloadHikesTask;
import net.ecoarttech.ihplus.network.NetworkConstants;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

public class SearchActivity extends ListActivity {
	private static String TAG = "IH+ - SearchActivity";
	private Context mContext;
	private SearchListAdapter mAdapter;
	private ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);
		this.mContext = this;

		mDialog = ProgressDialog.show(mContext, "", "FPO - Searching hikes");
		mDialog.setCancelable(true);
		new DownloadHikesTask(downloadHikesHandler).execute();

		mAdapter = new SearchListAdapter(this, null);
		setListAdapter(mAdapter);
		getListView().requestFocus();
	}

	private Handler downloadHikesHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			mDialog.dismiss();
			Log.d(TAG, "got hikes! " + msg.what);
			if (msg.what == NetworkConstants.SUCCESS) {
				ArrayList<Hike> hikes = new ArrayList<Hike>();
				JSONArray hikesJson;
				try {
					hikesJson = new JSONArray(msg.getData().getString(NetworkConstants.HIKES_JSON_KEY));
					for (int i = 0; i < hikesJson.length(); i++) {
						hikes.add(Hike.fromJson(hikesJson.getJSONObject(i)));
					}
					mAdapter.setHikes(hikes);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				Toast.makeText(mContext, "FPO sorry, an error occured searching for hikes", Toast.LENGTH_LONG).show();
			}
		}
	};

}
