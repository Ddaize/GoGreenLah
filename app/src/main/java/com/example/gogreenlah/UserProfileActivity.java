package com.example.gogreenlah;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    private CircleImageView profileImage;
    private TextView profileName;
    private Button buttonUpdate;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseSet;
    private StorageReference storageRef;
    private FirebaseUser user;
    private String userID, downloadImageUrl;

    private boolean imageChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        profileImage = findViewById(R.id.imageViewProfilePicChange);
        profileName = findViewById(R.id.textViewEmail);
        buttonUpdate = findViewById(R.id.buttonUpdateProfile);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        storageRef = FirebaseStorage.getInstance().getReference().child("users");

        user = firebaseAuth.getCurrentUser();
        userID = user.getUid();
        databaseSet = FirebaseDatabase.getInstance().getReference("users").child(userID);
        profileName.setText(user.getEmail());

        profileImage.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
        imageChanged = false;

        loadUserProfilePic();

    }

    private void loadUserProfilePic() {

        if (databaseSet != null) {
            databaseSet.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        if ( dataSnapshot.child("image").getValue() != null) {
                            String imageString = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(Uri.parse(imageString)).fit().into(profileImage);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            Toast.makeText(this, "image loaded", Toast.LENGTH_SHORT).show();
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).fit().into(profileImage);
            //imageView.setImageURI(imageUri);
        }
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("image", downloadImageUrl);

        databaseRef.child(userID).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(UserProfileActivity.this, "Uploaded to database", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(UserProfileActivity.this, "Unable to upload to database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateProfile() {
        if (imageChanged) {
            final StorageReference fileReference = storageRef.child(imageUri.getLastPathSegment() + userID + ".jpg");
            final UploadTask uploadTask = fileReference.putFile(imageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(UserProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                private static final String TAG = "UserProfileActivity";

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(UserProfileActivity.this, "upload successful", Toast.LENGTH_LONG).show();
                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            downloadImageUrl = fileReference.getDownloadUrl().toString();
                            return fileReference.getDownloadUrl();
                        }

                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageUrl = task.getResult().toString();

                                SaveProductInfoToDatabase();
                            }
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v == profileImage) {
            openFileChooser();
            imageChanged = true;

        } else if (v == buttonUpdate) {
            updateProfile();
        }
    }
}
