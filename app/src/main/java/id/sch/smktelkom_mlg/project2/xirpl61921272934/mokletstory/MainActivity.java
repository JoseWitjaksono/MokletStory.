package id.sch.smktelkom_mlg.project2.xirpl61921272934.mokletstory;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jose Witjaksono on 30/03/2017.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

	//defining view objects
	private EditText editTextEmail;
	private EditText editTextPassword;
	private EditText editTextUsername;
	private Button buttonSignup;

	private TextView textViewSignin;

	private ProgressDialog progressDialog;


	//defining firebaseauth object
	private FirebaseAuth firebaseAuth;
	private DatabaseReference mDatabase;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		//initializing firebase auth object
		firebaseAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference();

		//if getCurrentUser does not returns null
		if(firebaseAuth.getCurrentUser() != null){
			//that means user is already logged in
			//so close this activity
			finish();

			//and open profile activity
			startActivity(new Intent(getApplicationContext(), IndexActivity.class));
		}

		//initializing views
		editTextEmail = (EditText) findViewById(R.id.editTextEmail);
		editTextPassword = (EditText) findViewById(R.id.editTextPassword);
		editTextUsername = (EditText) findViewById(R.id.editTextUsername);
		textViewSignin = (TextView) findViewById(R.id.textViewSignUp);


		buttonSignup = (Button) findViewById(R.id.buttonSignup);

		progressDialog = new ProgressDialog(this);

		//attaching listener to button
		buttonSignup.setOnClickListener(this);
		textViewSignin.setOnClickListener(this);
	}

	private void registerUser(){

		//getting email and password from edit texts
		final String email = editTextEmail.getText().toString().trim();
		final String password  = editTextPassword.getText().toString().trim();
		final String username = editTextUsername.getText().toString().trim();

		//checking if email and passwords are empty
		if(TextUtils.isEmpty(username)){
			Toast.makeText(this,"Please enter username",Toast.LENGTH_LONG).show();
			return;
		}

		if(TextUtils.isEmpty(email)){
			Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
			return;
		}

		if(TextUtils.isEmpty(password)){
			Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
			return;
		}

		//if the email and password are not empty
		//displaying a progress dialogyrfuv

		progressDialog.setMessage("Registering Please Wait...");
		progressDialog.show();

		//creating a new user
		firebaseAuth.createUserWithEmailAndPassword(email, password)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {
						//checking if success
						if(task.isSuccessful()){
							String UID = firebaseAuth.getCurrentUser().getUid();
							DatabaseReference currentUser = mDatabase.child("User_Info").child(UID);
							currentUser.child("username").setValue(username);
							currentUser.child("email").setValue(email);
							currentUser.child("password").setValue(password);
							finish();
							startActivity(new Intent(getApplicationContext(), IndexActivity.class));
						}else{
							//display some message here
							Toast.makeText(MainActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
						}
						progressDialog.dismiss();
					}
				});

	}

	@Override
	public void onClick(View view) {

		if(view == buttonSignup){
			registerUser();
		}

		if(view == textViewSignin){
			//open login activity when user taps on the already registered textview
			startActivity(new Intent(this, LoginActivity.class));
		}

	}
}
