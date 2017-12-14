package io.impaul.harna100.roundrobinpicker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.SharedPrefSingleton;
import io.impaul.harna100.roundrobinpicker.places.PlaceUtil;
import io.impaul.harna100.roundrobinpicker.room.RoomSingleton;
import io.impaul.harna100.roundrobinpicker.room.models.User;


public class SplashScreenActivity extends AppCompatActivity {
	private static final String TAG = "SplashScreenActivity";

	private View pb_progress;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		pb_progress = findViewById(R.id.pb_progress);

		if(SharedPrefSingleton.isFirstRun(this)){
			Log.d(TAG, "onCreate: Is first run");
			getPlaces();
		}
		else{
			startActivity(new Intent(this, LoginActivity.class));
			finish();
		}
	}


	private void getPlaces() {
		PlaceUtil placeUtil = new PlaceUtil("AIzaSyDyAAkQUZ5RJ08ui7xKCHL9b1jxF_l8j9w");
		placeUtil.getNearbyRaw("1000", "33.793339,-117.852069", pb_progress, this).execute();
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
