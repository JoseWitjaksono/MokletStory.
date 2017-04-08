package id.sch.smktelkom_mlg.project2.xirpl61921272934.mokletstory;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class Section3Fragment extends Fragment implements View.OnClickListener {

	private TextView textViewUserEmail,textViewUsername;
	private Button buttonLogout;
	private FirebaseAuth firebaseAuth;
	private DatabaseReference mDatabase;
	ProfileActivity pa = new ProfileActivity();

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View myView = inflater.inflate(R.layout.activity_profile, container,false);
		buttonLogout = (Button) myView.findViewById(R.id.buttonLogout);
		textViewUserEmail = (TextView) myView.findViewById(R.id.textViewUserEmail);
		textViewUsername = (TextView) myView.findViewById(R.id.textViewUsername);
		firebaseAuth = FirebaseAuth.getInstance();
		mDatabase = FirebaseDatabase.getInstance().getReference();

		if(firebaseAuth.getCurrentUser() == null){
			pa.finish();
			startActivity(new Intent(getActivity(), LoginActivity.class));
		}
		FirebaseUser user = firebaseAuth.getCurrentUser();

		textViewUserEmail = (TextView)myView.findViewById(R.id.textViewUserEmail);
		buttonLogout = (Button) myView.findViewById(R.id.buttonLogout);

		textViewUserEmail.setText(user.getEmail());
		buttonLogout.setOnClickListener(this);

		getUsername();
		return myView;
	}

	private void getUsername() {
		if(textViewUsername.getText().toString() == "") {
			DatabaseReference userName = mDatabase.child("User_Info").child(firebaseAuth.getCurrentUser().getUid()).child("username");
			userName.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					String username = dataSnapshot.getValue(String.class);
					textViewUsername.setText(username);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
		} else {
		}
	}




	@Override
	public void onClick(View view) {
		if(view == buttonLogout){
			firebaseAuth.signOut();
			pa.finish();
			startActivity(new Intent(getActivity(), LoginActivity.class));
		}
	}

}
