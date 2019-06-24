package com.example.gogreenlah;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class featureOneActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button buttonChooseImage;
    private Button buttonUpload;
    private EditText editTextImageName;
    private ImageView imageView;
    private ProgressBar progressBar;

    private Uri imageUri;

    private StorageReference storageRef;
    private DatabaseReference databaseRef;

    private StorageTask uploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_one);

        buttonUpload = findViewById(R.id.buttonUpload);
        buttonChooseImage = findViewById(R.id.buttonChooseImage);
        editTextImageName = findViewById(R.id.editTextImageName);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        buttonUpload.setOnClickListener(this);
        buttonChooseImage.setOnClickListener(this);

        storageRef = FirebaseStorage.getInstance().getReference();
        databaseRef = FirebaseDatabase.getInstance().getReference();

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null &&
                data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).fit().into(imageView);
            //imageView.setImageURI(imageUri);
        }
    }

    //returns file uri
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadFile() {
        if (imageUri != null) {
            StorageReference fileReference = storageRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressBar.setProgress(0);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    progressBar.setProgress(0);
                                }
                            }, 500);
                            Toast.makeText(featureOneActivity.this, "upload successful", Toast.LENGTH_LONG).show();
                            ImageUpload imageUpload = new ImageUpload(editTextImageName.getText().toString().trim(),
                                    taskSnapshot.getStorage().getDownloadUrl().toString());
                            String imageUploadID = databaseRef.push().getKey();
                            databaseRef.child(imageUploadID).setValue(imageUpload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(featureOneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                           double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressBar.setProgress(100);
                        }
                    });
        } else {
            Toast.makeText(this, "No File Selected", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonUpload) {
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(this, "Uploading..., please wait", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "test", Toast.LENGTH_LONG).show();
                uploadFile();
            }
        }
        if (view == buttonChooseImage) {
            openFileChooser();
        }
    }
}
