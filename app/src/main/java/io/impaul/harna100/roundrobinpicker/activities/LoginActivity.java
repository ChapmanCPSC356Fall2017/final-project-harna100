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
	private Button btn_signup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		if(SharedPrefSingleton.GetUserId(this) != -1){
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		getReferences();
		setListeners();
		fillFields();

	}


	private void fillFields() {
		tv_email.setText("paul@ex.com", true);
		et_password.setText("paul");
	}

	private void getReferences(){
		tv_email = findViewById(R.id.email);
		et_password = findViewById(R.id.password);
		btn_signin = findViewById(R.id.email_sign_in_button);
		v_loginForm = findViewById(R.id.login_form);
		pv_progress = findViewById(R.id.login_progress);
		btn_signup = findViewById(R.id.btn_signup);
	}
	private void setListeners() {
		setLoginButtonListener();
		setEditorActionListener();
		setSignupButtonListener();
	}

	private void setSignupButtonListener() {
		btn_signup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(LoginActivity.this, SignupActivity.class));
			}
		});
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
				Intent intent = new Intent(getBaseContext(), MainActivity.class);
				SharedPrefSingleton.SetUserId(getBaseContext(), returnedUser.getId());
				startActivity(intent);
				finish();
			}
		}
	}


}

