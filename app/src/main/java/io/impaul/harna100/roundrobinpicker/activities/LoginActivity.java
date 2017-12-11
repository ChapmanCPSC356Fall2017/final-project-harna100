package io.impaul.harna100.roundrobinpicker.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.room.AppDatabase;
import io.impaul.harna100.roundrobinpicker.room.RoomSingleton;
import io.impaul.harna100.roundrobinpicker.room.models.User;

public class LoginActivity extends AppCompatActivity{

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
//		createDummyUser();
	}

	private void fillFields() {
		tv_email.setText("paul@ex.com", true);
		et_password.setText("paul");
	}

	private void createDummyUser(){
		User user = new User();
		user.setEmail("paul@ex.com");
		user.setPassword("paul");
		user.setFirstName("Paul");
		user.setLastName("Harnack");
		RoomSingleton.GetDb(getApplicationContext()).userDao().insertAll(user);
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
				Thread.sleep(1000);
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

				startActivity(intent);
				finish();
			}
		}
	}


}

