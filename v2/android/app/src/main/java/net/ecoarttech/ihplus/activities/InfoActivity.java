package net.ecoarttech.ihplus.activities;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

import net.ecoarttech.ihplus.R;

public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
		TextView info = (TextView) findViewById(R.id.info);
		Linkify.addLinks(info, Linkify.ALL);
	}

}
