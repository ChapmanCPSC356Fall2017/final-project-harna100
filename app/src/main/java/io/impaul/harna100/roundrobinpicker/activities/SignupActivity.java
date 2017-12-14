package io.impaul.harna100.roundrobinpicker.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import io.impaul.harna100.roundrobinpicker.R;


public class SignupActivity extends AppCompatActivity {

	private EditText et_userName;
	private EditText et_email;
	private EditText et_password;
	private EditText et_passwordConfirm;
	private Button btn_signup;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		getReferences();
		setListeners();
	}

	private void getReferences(){
		et_userName = findViewById(R.id.et_username);
		et_email = findViewById(R.id.et_email);
		et_password = findViewById(R.id.et_password);
		et_passwordConfirm = findViewById(R.id.et_passwordConfirm);
		btn_signup = findViewById(R.id.btn_signup);
	}

	private void setListeners(){
		setSignupButtonListener();
	}

	private void setSignupButtonListener(){
		btn_signup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				signup();
			}
		});
	}

	private void signup(){

	}
}
