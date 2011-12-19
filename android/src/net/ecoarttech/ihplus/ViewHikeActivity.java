package net.ecoarttech.ihplus;

import net.ecoarttech.ihplus.model.ActionType;
import net.ecoarttech.ihplus.model.Hike;
import net.ecoarttech.ihplus.model.ScenicVista;
import net.ecoarttech.ihplus.util.Constants;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ViewHikeActivity extends Activity {
	private static String TAG = "IH+ - ViewHikeActivity";
	private Hike mHike;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_hike);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null){
			// setup view
			mHike = (Hike) extras.get(Constants.BUNDLE_HIKE);
			updateUI();
		}
	}
	
	private void updateUI(){
		((TextView) findViewById(R.id.hike_name)).setText(mHike.getName());
		((TextView) findViewById(R.id.hike_desc)).setText(mHike.getDescription());
		LinearLayout holder = (LinearLayout) findViewById(R.id.vistas_holder);
		for (int i = 0; i < mHike.getVistas().size(); i++){
			ScenicVista vista =  mHike.getVistas().get(i);
			// Vista divider
			TextView divider = new TextView(this);
			divider.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			divider.setText("Scenic Vista #"+(i+1));
			divider.setBackgroundColor(getResources().getColor(R.color.header_gray));
			divider.setPadding(10, 5, 5, 5);
			holder.addView(divider);
			// Verbiage
			TextView verbiage = new TextView(this);
			verbiage.setText("Instructions:\n"+Html.fromHtml(vista.getAction()));
			verbiage.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			verbiage.setBackgroundColor(getResources().getColor(R.color.light_gray));
			verbiage.setPadding(10, 5, 5, 5);
			holder.addView(verbiage);
			// Content
			View content;
			if (vista.getActionType() == ActionType.NOTE){
				content = new TextView(this);
				((TextView) content).setText(vista.getNote());
			}
			else{
				content = new ImageView(this);
				//TODO - dl image from server
			}
			content.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			holder.addView(content);
		}
	}
}
