package io.impaul.harna100.roundrobinpicker.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.impaul.harna100.roundrobinpicker.R;
import io.impaul.harna100.roundrobinpicker.room.RoomSingleton;
import io.impaul.harna100.roundrobinpicker.room.models.User;


public class SignupActivity extends AppCompatActivity {

	private static final String TAG = "SignupActivity";
	private EditText et_username;
	private EditText et_email;
	private EditText et_password;
	private EditText et_passwordConfirm;
	private Button btn_signup;
	private EditText et_name;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		getReferences();
		setListeners();
	}

	private void getReferences(){
		et_username = findViewById(R.id.et_username);
		et_email = findViewById(R.id.et_email);
		et_password = findViewById(R.id.et_password);
		et_passwordConfirm = findViewById(R.id.et_passwordConfirm);
		btn_signup = findViewById(R.id.btn_signup);
		et_name = findViewById(R.id.et_name);
	}

	private void setListeners(){
		setSignupButtonListener();
	}

	private void setSignupButtonListener(){
		btn_signup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				new SignupTask().execute();
			}
		});
	}

	private void signup(){
		boolean failedSignup = false;
		if(anyEmptyFields()){
			failedSignup = true;
		}
		if(notEmail()){
			failedSignup = true;
		}

		if(emailExists()){
			failedSignup = true;
		}

		if(usernameExists()){
			failedSignup = true;
		}

		if(doPasswordsMismatch()){
			failedSignup = true;
		}
		if(isPasswordWeak()){
			failedSignup = true;
		}

		if(failedSignup){
			return;
		}

		saveUser();
	}

	private boolean usernameExists() {
		int userNameCount = RoomSingleton.GetDb(this).userDao().userExists(et_username.getText().toString());
		boolean doesExist = userNameCount != 0;
		if(doesExist){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					et_username.setError("Username exists");
				}
			});

		}

		return doesExist;
	}

	private boolean isPasswordWeak() {
		final String passwordReqs = "";
		boolean isWeak = false;
		if(isWeak){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					et_password.setError("Password is weak\n" + passwordReqs);
				}
			});

		}

		return isWeak;
	}

	private boolean notEmail() {
		String email = et_email.getText().toString();
		boolean notEmail = !Patterns.EMAIL_ADDRESS.matcher(email).matches();
		if(notEmail){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					et_email.setError("Not a valid email format");
				}
			});

		}
		return notEmail;
	}


	private boolean doPasswordsMismatch() {
		boolean isMismatch = !et_password.getText().toString().equals(et_passwordConfirm.getText().toString());
		if(isMismatch){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					et_password.setError("Passwords Do Not Match");
					et_passwordConfirm.setError("Passwords Do Not Match");
				}
			});

		}
		return isMismatch;
	}

	private boolean emailExists() {
		int emailCount = RoomSingleton.GetDb(this).userDao().emailExists(et_email.getText().toString());
		boolean emailExists = emailCount != 0;
		if(emailExists){
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					et_email.setError("Email Exists Already");
				}
			});

		}
		return emailExists;
	}

	private boolean anyEmptyFields() {
		boolean emptyFields = false;
		EditText[] fields = new EditText[]{
				et_username,
				et_email,
				et_password,
				et_passwordConfirm,
				et_name
		};
		for (final EditText field : fields) {
			if(field.getText().toString().isEmpty()){
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						field.setError("Field Cannot Be Empty");
					}
				});

				emptyFields = true;
			}
		}
		return emptyFields;
	}


	private void saveUser() {
		Log.d(TAG, "saveUser: Saving user");
		User user = new User();
		user.setName(et_name.getText().toString());
		user.setEmail(et_email.getText().toString());
		user.setUsername(et_username.getText().toString());
		user.setPassword(et_password.getText().toString());
		RoomSingleton.GetDb(this).userDao().insertAll(user);
		Log.d(TAG, "saveUser: User Saved");
	}

	private class SignupTask extends AsyncTask<Void, Void, Void>{

		public SignupTask() {

		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected Void doInBackground(Void... voids) {
			signup();
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {

		}
	}


}
