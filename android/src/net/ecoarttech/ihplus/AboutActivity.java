package net.ecoarttech.ihplus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		TextView about = (TextView) findViewById(R.id.about);
		Linkify.addLinks(about, Linkify.ALL);
	}
	
	public void onCreditsClick(View v){
		startActivity(new Intent(this, InfoActivity.class));
	}
	
	public void onHikeClick(View v) {
		Intent i = new Intent(this, CreateHikeActivity.class);
		startActivity(i);
	}

	public void onSearchClick(View v) {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}

}
