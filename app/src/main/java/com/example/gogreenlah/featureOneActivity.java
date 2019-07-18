package com.example.gogreenlah;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class featureOneActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Button buttonChooseImage;
    private Button buttonUpload;
    private EditText editTextImageName;
    private ImageView imageView;
    private ProgressBar progressBar;

    //item attributes
    private String productRandomKey;
    private String saveCurrentDate, saveCurrentTime;
    private String itemName, itemType, itemURL;
    private Integer requestNumber = 0;

    private Uri imageUri;
    private String downloadImageUrl;

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

        Spinner mySpinner = findViewById(R.id.spinnerItemType);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(featureOneActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(spinnerAdapter);
        mySpinner.setSelection(0);

        buttonUpload.setOnClickListener(this);
        buttonChooseImage.setOnClickListener(this);

        storageRef = FirebaseStorage.getInstance().getReference().child("uploads");
        databaseRef = FirebaseDatabase.getInstance().getReference().child("uploads");
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

            Calendar calendar = Calendar.getInstance();

            SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
            saveCurrentDate = currentDate.format(calendar.getTime());

            SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
            saveCurrentTime = currentTime.format(calendar.getTime());

            productRandomKey = saveCurrentDate + saveCurrentTime;

            final StorageReference fileReference = storageRef.child(imageUri.getLastPathSegment() + productRandomKey + ".jpg");
            final UploadTask uploadTask = fileReference.putFile(imageUri);

            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(featureOneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                private static final String TAG = "featureOneActivity";

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

                    Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }

                            downloadImageUrl = fileReference.getDownloadUrl().toString();
                            return fileReference.getDownloadUrl();
                        }

                           /* Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                            while (!urlTask.isSuccessful()) ;
                            Uri downloadUri = urlTask.getResult();
                            Log.d(TAG, "onSuccess:firebase download url: " + downloadUri.toString());


                            ImageUpload imageUpload = new ImageUpload(editTextImageName.getText().toString().trim(),
                                    spinner.getSelectedItem().toString(),
                                    downloadUri.toString());
                            String imageUploadID = databaseRef.push().getKey();
                            databaseRef.child(imageUploadID).setValue(imageUpload);
                            */
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                downloadImageUrl = task.getResult().toString();

                                //Toast.makeText(AdminAddNewProductActivity.this, "got the Product image Url Successfully...", Toast.LENGTH_SHORT).show();

                                SaveProductInfoToDatabase();
                            }
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressBar.setProgress((int) progress);
                }
            });
        } else {
            Toast.makeText(this, "Choose an image file", Toast.LENGTH_SHORT).show();
        }
    }

    private void SaveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("itemID", productRandomKey);
        productMap.put("date", saveCurrentDate);
        productMap.put("time", saveCurrentTime);
        productMap.put("itemDescription", null);
        productMap.put("image", downloadImageUrl);
        productMap.put("itemType", itemType);
        productMap.put("requestNumber", requestNumber);
        productMap.put("itemName", itemName);
        productMap.put("requestInfo", "");

        databaseRef.child(productRandomKey).updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Intent intent = new Intent(AdminAddNewProductActivity.this, AdminCategoryActivity.class);
//                            startActivity(intent);
//
//                            loadingBar.dismiss();
//                            Toast.makeText(AdminAddNewProductActivity.this, "Product is added successfully..", Toast.LENGTH_SHORT).show();
                        } else {
//                            loadingBar.dismiss();
//                            String message = task.getException().toString();
//                            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openDialog() {
        ImageDescDialog imageDialog = new ImageDescDialog();
        Bundle bundle = new Bundle();
        bundle.putString("uri", imageUri.toString());
        bundle.putString("id", productRandomKey);
        imageDialog.setArguments(bundle);
        imageDialog.show(getSupportFragmentManager(), "image description");
    }

    private void validateProductData() {

        itemName = editTextImageName.getText().toString();

        Spinner spinner = findViewById(R.id.spinnerItemType);
        itemType = spinner.getSelectedItem().toString();

        if (imageUri == null) {
            Toast.makeText(this, "Product image is mandatory...", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(itemName)) {
            Toast.makeText(this, "Please write product name...", Toast.LENGTH_SHORT).show();
        } else {
            uploadFile();
            openDialog();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == buttonUpload) {
            if (uploadTask != null && uploadTask.isInProgress()) {
                Toast.makeText(this, "Uploading..., please wait", Toast.LENGTH_SHORT).show();
            } else {
                validateProductData();
            }
        }
        if (view == buttonChooseImage) {
            openFileChooser();
        }
    }
}
