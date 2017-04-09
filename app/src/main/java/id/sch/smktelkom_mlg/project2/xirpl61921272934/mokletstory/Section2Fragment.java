package id.sch.smktelkom_mlg.project2.xirpl61921272934.mokletstory;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import id.sch.smktelkom_mlg.project2.xirpl61921272934.mokletstory.R;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class Section2Fragment extends Fragment {

	//constant to track image chooser intent
	private static final int PICK_IMAGE_REQUEST = 234;

	//view objects
	private Button buttonChoose;
	private Button buttonUpload;
	private EditText editTextName;
	private TextView textViewTumbal,textViewKoneksi;
	private ImageView imageView;

	//uri to store file
	private Uri filePath;

	//firebase objects
	private StorageReference storageReference;
	private DatabaseReference mDatabase,myDatabase;
	private FirebaseAuth firebaseAuth;

	public Section2Fragment() {
		// Required empty public constructor
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View myView = inflater.inflate(R.layout.fragment_section2, container, false);
		buttonChoose = (Button) myView.findViewById(R.id.buttonChoose);
		buttonUpload = (Button) myView.findViewById(R.id.buttonUpload);
		imageView = (ImageView) myView.findViewById(R.id.imageView);
		editTextName = (EditText) myView.findViewById(R.id.editText);
		textViewTumbal = (TextView) myView.findViewById(R.id.textViewTumbal);
		textViewKoneksi = (TextView) myView.findViewById(R.id.textViewKoneksi);

		storageReference = FirebaseStorage.getInstance().getReference();
		mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
		myDatabase = FirebaseDatabase.getInstance().getReference();
		firebaseAuth = FirebaseAuth.getInstance();

		buttonChoose.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				showFileChooser();
			}
		});

		buttonUpload.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view) {
				uploadFile();
			}
		});

		getUsername();
		return myView;
	}

	private void getUsername() {
			DatabaseReference userName = myDatabase.child("User_Info").child(firebaseAuth.getCurrentUser().getUid()).child("username");
			userName.addValueEventListener(new ValueEventListener() {
				@Override
				public void onDataChange(DataSnapshot dataSnapshot) {
					String username = dataSnapshot.getValue(String.class);
					textViewTumbal.setText(username);
				}

				@Override
				public void onCancelled(DatabaseError databaseError) {

				}
			});
	}

	private void showFileChooser() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
	}

	public String getFileExtension(Uri uri) {
		ContentResolver cR = getActivity().getContentResolver();
		MimeTypeMap mime = MimeTypeMap.getSingleton();
		return mime.getExtensionFromMimeType(cR.getType(uri));
	}

	private void uploadFile() {
		//checking if file is available
		if (filePath != null) {
			//displaying progress dialog while image is uploading
			final ProgressDialog progressDialog = new ProgressDialog(getActivity());
			progressDialog.setTitle("Uploading");
			progressDialog.show();

			//getting the storage reference
			StorageReference sRef = storageReference.child(Constants.STORAGE_PATH_UPLOADS + System.currentTimeMillis() + "." + getFileExtension(filePath));

			//adding the file to reference
			sRef.putFile(filePath)
					.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
						@Override
						public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
							//dismissing the progress dialog
							progressDialog.dismiss();

							//displaying success toast
							Toast.makeText(getActivity().getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

							//creating the upload object to store uploaded image details
							Upload upload = new Upload(textViewTumbal.getText().toString().trim(), editTextName.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());

							//adding an upload to firebase database
							String uploadId = mDatabase.push().getKey();
							mDatabase.child(uploadId).setValue(upload);
						}
					})
					.addOnFailureListener(new OnFailureListener() {
						@Override
						public void onFailure(@NonNull Exception exception) {
							progressDialog.dismiss();
							Toast.makeText(getActivity().getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
						}
					})
					.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
						@Override
						public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
							//displaying the upload progress
							double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
							progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
						}
					});
		} else {
			//display an error if no file is selected
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
			filePath = data.getData();
			try {
				Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getApplicationContext().getContentResolver(), filePath);
				imageView.setImageBitmap(bitmap);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
