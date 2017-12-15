package io.impaul.harna100.roundrobinpicker.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.SharedPrefSingleton;
import io.impaul.harna100.roundrobinpicker.places.PlaceUtil;
import io.impaul.harna100.roundrobinpicker.room.RoomSingleton;
import io.impaul.harna100.roundrobinpicker.room.models.User;


public class SplashScreenActivity extends AppCompatActivity {
	private static final String TAG = "SplashScreenActivity";
	private static final int MY_PERMISSIONS_REQUEST_LOCATION = 3000;

	private View pb_progress;
	private String userLocation;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		userLocation = "33.793339,-117.852069";
		pb_progress = findViewById(R.id.pb_progress);

		int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
		if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION }, MY_PERMISSIONS_REQUEST_LOCATION);
		}
		else{
			getPlaces();
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		switch (requestCode) {
			case MY_PERMISSIONS_REQUEST_LOCATION:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					getUserLocation();
				} else {
					Toast.makeText(this, "Fine, no relevant results for you", Toast.LENGTH_SHORT).show();
					getPlaces();
				}
				break;
		}
	}

	private void getUserLocation() {
		FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(this);
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
			&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

			return;
		}
		client.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
			@Override
			public void onSuccess(Location location) {
				if(location != null){
					StringBuilder sb = new StringBuilder();
					sb.append(location.getLatitude());
					sb.append(",");
					sb.append(location.getLongitude());
					userLocation = sb.toString();

				}
				getPlaces();
			}
		});
	}

	private void getPlaces() {
		if(SharedPrefSingleton.isFirstRun(this)){
			Log.d(TAG, "onCreate: Is first run");
			PlaceUtil placeUtil = new PlaceUtil("AIzaSyDyAAkQUZ5RJ08ui7xKCHL9b1jxF_l8j9w");
			placeUtil.getNearbyRaw("1000", userLocation, pb_progress, this).execute();
		}
		else{
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}


	private void nukeAll() {
		RoomSingleton.GetDb(this).userPlacesDao().nukeTable();
		RoomSingleton.GetDb(this).userDao().nukeTable();
		RoomSingleton.GetDb(this).placeDao().nukeTable();

	}
	private void createDummyUser(){
		User user = new User();
		user.setEmail("paul@ex.com");
		user.setPassword("paul");
		user.setName("Paul Harnack");
		RoomSingleton.GetDb(getApplicationContext()).userDao().insertAll(user);
	}
}
