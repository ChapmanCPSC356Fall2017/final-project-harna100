package io.impaul.harna100.roundrobinpicker.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.SharedPrefSingleton;
import io.impaul.harna100.roundrobinpicker.models.PlaceModel;
import io.impaul.harna100.roundrobinpicker.places.PlaceUtil;
import io.impaul.harna100.roundrobinpicker.places.models.NearbyRaw;
import io.impaul.harna100.roundrobinpicker.room.RoomSingleton;
import io.impaul.harna100.roundrobinpicker.room.models.Place;
import io.impaul.harna100.roundrobinpicker.room.models.User;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity{

	private static final String TAG = "LoginActivity";
	// UI references.
	private AutoCompleteTextView tv_email;
	private EditText et_password;
	private View pv_progress;
	private View v_loginForm;
	private Button btn_signin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		getReferences();
		setListeners();
		fillFields();



		if(SharedPrefSingleton.isFirstRun(this)){
			Log.d(TAG, "onCreate: Is first run");
			createDummyUser();
			getPlaces();
		}

	}

	private void getPlaces() {
		PlaceUtil placeUtil = new PlaceUtil("AIzaSyDyAAkQUZ5RJ08ui7xKCHL9b1jxF_l8j9w");
		placeUtil.getNearbyRaw("1000", "33.793339,-117.852069", pv_progress).execute();
	}

	private void nukeAll() {
		RoomSingleton.GetDb(this).userPlacesDao().nukeTable();
		RoomSingleton.GetDb(this).userDao().nukeTable();
		RoomSingleton.GetDb(this).placeDao().nukeTable();

	}

	private void fillFields() {
		tv_email.setText("paul@ex.com", true);
		et_password.setText("paul");
	}

	private void createDummyUser(){
		User user = new User();
		user.setEmail("paul@ex.com");
		user.setPassword("paul");
		user.setName("Paul Harnack");
		RoomSingleton.GetDb(getApplicationContext()).userDao().insertAll(user);
	}
	private void createDummyPlaces(){
		List<PlaceModel> places = new ArrayList<>();
		places.add(new PlaceModel("Place 5", "This is a description of place 5", "https://www.nationalgeographic.com/content/dam/animals/thumbs/rights-exempt/mammals/d/domestic-dog_thumb.jpg", new LatLng(42.306190, -83.714033), "Address 123 St. Orange CA 29866"));
		places.add(new PlaceModel("Place 1", "This is a description of place 1", "https://b.zmtcdn.com/data/reviews_photos/e64/738e59a141f1a89a732e791d12546e64_1446414326.jpg", new LatLng(42.306190, -83.714033), "Address 123 St. Orange CA 29866"));
		places.add(new PlaceModel("Place 2", "This is a description of place 2", "https://b.zmtcdn.com/data/reviews_photos/e64/738e59a141f1a89a732e791d12546e64_1446414326.jpg", new LatLng(42.306190, -83.714033), "Address 123 St. Orange CA 29866"));
		places.add(new PlaceModel("Place 3", "This is a description of place 3", "https://b.zmtcdn.com/data/reviews_photos/e64/738e59a141f1a89a732e791d12546e64_1446414326.jpg", new LatLng(42.306190, -83.714033), "Address 123 St. Orange CA 29866"));
		places.add(new PlaceModel("Place 4", "This is a description of place 4", "https://b.zmtcdn.com/data/reviews_photos/e64/738e59a141f1a89a732e791d12546e64_1446414326.jpg", new LatLng(42.306190, -83.714033), "Address 123 St. Orange CA 29866"));
		List<Place> toInsert = new ArrayList<>();
		for (PlaceModel place : places) {
			Place p = Place.NewPlace(place);
			toInsert.add(p);
		}
		RoomSingleton.GetDb(this).placeDao().insertPlaces(toInsert.toArray(new Place[toInsert.size()]));
	}

	private void getReferences(){
		tv_email = findViewById(R.id.email);
		et_password = findViewById(R.id.password);
		btn_signin = findViewById(R.id.email_sign_in_button);
		v_loginForm = findViewById(R.id.login_form);
		pv_progress = findViewById(R.id.login_progress);
	}
	private void setListeners() {
		setLoginButtonListener();
		setEditorActionListener();
	}

	private void setEditorActionListener() {
		et_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});
	}

	private void setLoginButtonListener() {
		btn_signin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

	}

	private void attemptLogin() {
		String email = tv_email.getText().toString();
		String password = et_password.getText().toString();


		new AttemptLoginTask().execute(email, password);
	}

	private class AttemptLoginTask extends AsyncTask<String, Void, User> {

		@Override
		protected void onPreExecute() {
			pv_progress.setVisibility(View.VISIBLE);
			btn_signin.setEnabled(false);
		}

		@Override
		protected User doInBackground(String... strings) {
			// TODO remove sleep
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(strings.length != 2){
				return null;
			}
			return RoomSingleton.GetDb(getApplicationContext()).userDao().authUser(strings[0], strings[1]);
		}

		@Override
		protected void onPostExecute(User returnedUser) {

			if(returnedUser == null){
				Toast.makeText(getBaseContext(), "Not a valid email/password", Toast.LENGTH_SHORT).show();
				pv_progress.setVisibility(View.GONE);
				btn_signin.setEnabled(true);
			}
			else{
				Intent intent = new Intent(getBaseContext(), PlaceSelectionActivity.class);
				SharedPrefSingleton.SetUserId(getBaseContext(), returnedUser.getId());
				startActivity(intent);
				finish();
			}
		}
	}


}

