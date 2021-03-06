package net.ecoarttech.ihplus.activities;

import net.ecoarttech.ihplus.R;
import net.ecoarttech.ihplus.api.NetworkConstants;
import net.ecoarttech.ihplus.model.ActionType;
import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.HikeV2;
import net.ecoarttech.ihplus.model.ScenicVista;
import net.ecoarttech.ihplus.model.ScenicVistaV2;
import net.ecoarttech.ihplus.api.NetworkConstants;
import net.ecoarttech.ihplus.util.Constants;
import net.ecoarttech.ihplus.util.Util;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewHikeActivity extends Activity {
	private static String TAG = "IH+ - ViewHikeActivity";
	private HikeV2 mHike;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_hike);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			// setup view
			mHike = (HikeV2) extras.get(Constants.BUNDLE_HIKE);
			updateUI();
		}
	}

	private void updateUI() {
		((TextView) findViewById(R.id.hike_name)).setText(mHike.getName());
		((TextView) findViewById(R.id.hike_desc)).setText(mHike.getDescription());
		((TextView) findViewById(R.id.hike_info)).setText(String.format("Hiked by %s on %s", mHike.getUsername(), mHike
				.getDate()));
		LinearLayout holder = (LinearLayout) findViewById(R.id.vistas_holder);
		Log.d(TAG, "vistas amoutn: " + mHike.getVistas().size());
		for (int i = 0; i < mHike.getVistas().size(); i++) {
			ScenicVistaV2 vista = mHike.getVistas().get(i);
			// inflate vista item, fill in data
			View vistaItem = getLayoutInflater().inflate(R.layout.vista_item, null);
			((TextView) vistaItem.findViewById(R.id.vista_divider)).setText("Scenic Vista #" + (i + 1));
			((TextView) vistaItem.findViewById(R.id.vista_action)).setText("Instructions:\n"
					+ Html.fromHtml(vista.getVerbiage()));
			// Content
			View content;
			if (vista.getAction_type() == ActionType.NOTE || vista.getAction_type() == ActionType.TEXT) {
				content = ((TextView) vistaItem.findViewById(R.id.vista_content_note));
				((TextView) content).setText("Response:\n" + vista.getNote());
				content.setVisibility(View.VISIBLE);
			} else if (vista.getAction_type() == ActionType.PHOTO) {
				content = ((ImageView) vistaItem.findViewById(R.id.vista_content_photo));
				// dl image from server
				Util.downloadImage(NetworkConstants.PHOTO_URL + vista.getPhoto(), (ImageView) content);
			}
			holder.addView(vistaItem);
		}
	}
}
