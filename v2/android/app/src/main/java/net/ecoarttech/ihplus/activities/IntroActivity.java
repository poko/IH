package net.ecoarttech.ihplus.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import net.ecoarttech.ihplus.R;

public class IntroActivity extends Activity {


	String[] LOCATION_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intro);

		Toast.makeText(this, "make sure gps is enabled.", Toast.LENGTH_LONG).show();

		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			ActivityCompat.requestPermissions(this, LOCATION_PERMISSIONS, 0); //todo make 0 a const
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		//TODO
	}

	public void onAboutClick(View v){
		startActivity(new Intent(this, AboutActivity.class));
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
