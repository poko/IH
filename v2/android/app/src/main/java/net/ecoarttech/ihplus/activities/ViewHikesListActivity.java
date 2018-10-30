package net.ecoarttech.ihplus.activities;

import java.util.ArrayList;
import java.util.List;

import net.ecoarttech.ihplus.R;
import net.ecoarttech.ihplus.api.HikesResponse;
import net.ecoarttech.ihplus.api.IHApiServiceInstanceKt;
import net.ecoarttech.ihplus.model.HikeV2;
import net.ecoarttech.ihplus.util.Constants;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewHikesListActivity extends ListActivity {
	private static String TAG = "IH+ - ViewHikesListActivity";
	private int mHikeId;
	private ArrayList<HikeV2> mHikes;
	private HikeListAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_hikes);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mHikeId = extras.getInt(Constants.BUNDLE_HIKE_ID);
			downloadHikes();
		}
		mAdapter = new HikeListAdapter();
		setListAdapter(mAdapter);
		getListView().setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				// start individual hike view activity
				Intent i = new Intent(ViewHikesListActivity.this, ViewHikeActivity.class);
				i.putExtra(Constants.BUNDLE_HIKE, (HikeV2) mAdapter.getItem(position));
				startActivity(i);
			}
		});
	}

	private void downloadHikes(){
		IHApiServiceInstanceKt.get().getHikesById(mHikeId).enqueue(hikesDownloadCallback);
	}

	private void updateUI() {
		// populate hike info
		((TextView) findViewById(R.id.hike_name)).setText(mHikes.get(0).getName());
		((TextView) findViewById(R.id.hike_desc)).setText(mHikes.get(0).getDescription());
		// populate list with hikes
		mAdapter.notifyDataSetChanged();
	}

	private Callback<HikesResponse> hikesDownloadCallback = new Callback<HikesResponse>() {
		@Override
		public void onResponse(Call<HikesResponse> call, Response<HikesResponse> response) {
			List<HikeV2> hikes = response.body().getHikes();
			mHikes = new ArrayList<>();
			Log.d(TAG, "Got Hikes!" + hikes);
			for (HikeV2 h : hikes){
				mHikes.add(h);
			}
			updateUI();
		}

		@Override
		public void onFailure(Call<HikesResponse> call, Throwable t) {
			Log.d(TAG, "Failed to get hikes");
			showFailureDialog();
		}
	};

	private void showFailureDialog() {
		new AlertDialog.Builder(this).setTitle("oops something went wrong").setMessage("try again?").setPositiveButton(
				"Retry", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						downloadHikes();
					}
				}).setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
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
			HikeV2 hike = mHikes.get(position);
			holder.info.setText(String.format("hiked by %s, %s.", hike.getUsername(), hike.getDate()));

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
