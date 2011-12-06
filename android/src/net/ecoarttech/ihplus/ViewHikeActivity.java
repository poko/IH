package net.ecoarttech.ihplus;

import java.util.ArrayList;

import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.ScenicVista;
import net.ecoarttech.ihplus.network.DownloadByHikeId;
import net.ecoarttech.ihplus.network.NetworkConstants;
import net.ecoarttech.ihplus.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class ViewHikeActivity extends Activity {
	private static String TAG = "IH+ - ViewHikeActivity";
	private int mHikeId;
	private ArrayList<Hike> mHikes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_hike);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			mHikeId = extras.getInt(Constants.BUNDLE_HIKE_ID);
			new DownloadByHikeId(hikeDownloadHandler, mHikeId, NetworkConstants.GET_HIKES_URL, NetworkConstants.RESPONSE_JSON_HIKES).execute();
		}
	}
	
	private void updateUI() {
		// for now, create a button for each vista
		LinearLayout holder = (LinearLayout) findViewById(R.id.vistas_holder);
		if (mHikes.size() > 0) {
			for ( int i = 0; i< mHikes.get(0).getVistas().size(); i++){//(final ScenicVista vista : mHikes.get(0).getVistas()) {
				final int index = i;
				final ScenicVista vista =  mHikes.get(0).getVistas().get(i);
				Button button = new Button(this);
				button.setText("View vista data. \nLat: " + vista.getLat() + "\nLng: " + vista.getLong());
				button.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO show gallery of all actions here .. 
						StringBuilder sb = new StringBuilder();
						sb.append("Vista Directions: " + vista.getAction()); //TODO - handle photos
						sb.append(String.format("\nUser %s had this to say: %s", mHikes.get(0).getUsername(), vista.getNote()));
						for (int j = 1; j < mHikes.size(); j++){
							Hike h = mHikes.get(j);
							if (h.getVistas().size() > index){
								ScenicVista sv = h.getVistas().get(index);
								sb.append(String.format("\nUser %s had this to say: %s", h.getUsername(), sv.getNote()));
							}
						}
						Toast.makeText(ViewHikeActivity.this, sb.toString(), Toast.LENGTH_LONG).show();
					}
				});
				holder.addView(button);
			}
		}
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
					for (int i = 0; i< json.length(); i++){
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
		new AlertDialog.Builder(this)
		.setTitle("uh oh something went wrong")
		.setMessage("Try again?")
		.setPositiveButton("Retry", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				new DownloadByHikeId(hikeDownloadHandler, mHikeId, NetworkConstants.GET_HIKES_URL, NetworkConstants.RESPONSE_JSON_HIKES).execute();
			}
		})
		.setNegativeButton("Cancel", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		}).show();
	}
}
