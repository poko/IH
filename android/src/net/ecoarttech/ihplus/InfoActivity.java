package net.ecoarttech.ihplus;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info);
		
		TextView info = (TextView) findViewById(R.id.info);
		Linkify.addLinks(info, Linkify.ALL);
	}

}
