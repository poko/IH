package net.ecoarttech.ihplus;

import java.util.ArrayList;

import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.ScenicVista;
import net.ecoarttech.ihplus.network.DownloadByHikeId;
import net.ecoarttech.ihplus.network.NetworkConstants;
import net.ecoarttech.ihplus.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ViewHikesListActivity extends ListActivity {
	private static String TAG = "IH+ - ViewHikesListActivity";
	private int mHikeId;
	private ArrayList<Hike> mHikes;
	private HikeListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_hikes);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mHikeId = extras.getInt(Constants.BUNDLE_HIKE_ID);
			new DownloadByHikeId(hikeDownloadHandler, mHikeId, NetworkConstants.GET_HIKES_URL,
					NetworkConstants.RESPONSE_JSON_HIKES).execute();
		}
		mAdapter = new HikeListAdapter();
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// start individual hike view activity
				Intent i = new Intent(ViewHikesListActivity.this, ViewHikeActivity.class);
				i.putExtra(Constants.BUNDLE_HIKE, (Hike) mAdapter.getItem(position));
				startActivity(i);
			}
		});
	}

	private void updateUI() {
		// populate hike info
		((TextView) findViewById(R.id.hike_name)).setText(mHikes.get(0).getName());
		((TextView) findViewById(R.id.hike_desc)).setText(mHikes.get(0).getDescription());
		// populate list with hikes
		mAdapter.notifyDataSetChanged();
	}

	private Handler hikeDownloadHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "got message:" + msg.what);
			if (msg.what == NetworkConstants.SUCCESS) {
				// parse hike data out from server response
				try {
					JSONArray json = new JSONArray(msg.getData().getString(NetworkConstants.HIKE_JSON_KEY));
					mHikes = new ArrayList<Hike>();
					// create hike objects
					for (int i = 0; i < json.length(); i++) {
						Hike h = Hike.fromJson(json.getJSONObject(i), false);
						JSONArray vistas = json.getJSONObject(i).getJSONArray(NetworkConstants.RESPONSE_JSON_VISTAS);
						for (int j = 0; j < vistas.length(); j++) {
							h.addVista(ScenicVista.newFromJson(vistas.getJSONObject(j)));
						}
						mHikes.add(h);
					}
					updateUI();
				} catch (JSONException e) {
					e.printStackTrace();
					showFailureDialog();
				}
			} else {
				showFailureDialog();
			}
		}
	};

	private void showFailureDialog() {
		new AlertDialog.Builder(this).setTitle("oops something went wrong").setMessage("try again?").setPositiveButton(
				"Retry", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						new DownloadByHikeId(hikeDownloadHandler, mHikeId, NetworkConstants.GET_HIKES_URL,
								NetworkConstants.RESPONSE_JSON_HIKES).execute();
					}
				}).setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}

	private class HikeListAdapter extends BaseAdapter {

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) { // inflate a new xml resource
				convertView = getLayoutInflater().inflate(R.layout.hike_item, parent, false);
				holder = new ViewHolder();
				holder.info = (TextView) convertView.findViewById(R.id.hike_info);
				convertView.setTag(holder);
			} else { // view has already been loaded, get via tag
				holder = (ViewHolder) convertView.getTag();
			}
			// set fields
			Hike hike = mHikes.get(position);
			holder.info.setText(String.format("hiked by %s, %s.", hike.getUsername(), hike.getCreateDate()));

			return convertView;
		}

		@Override
		public int getCount() {
			if (mHikes == null)
				return 0;
			return mHikes.size();
		}

		@Override
		public Object getItem(int position) {
			if (mHikes == null)
				return null;
			return mHikes.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

	}

	private static class ViewHolder {
		TextView info;
	}
}
