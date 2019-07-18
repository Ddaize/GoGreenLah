package com.example.gogreenlah;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class RequestItemActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView imageName , imageDescription, textViewRequestInfo;
    private String imageUrl;
    private int requestNumber;
    private String imageID;
    private String requestInfo;
    private EditText editTextRequestInfo;
    private Button requestButton;
    private ImageUpload itemRequest;
    private DatabaseReference databaseReference;
    private boolean requested;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_item);

        imageView = findViewById(R.id.imageViewItem);
        imageName = findViewById(R.id.textViewItemName);
        imageDescription = findViewById(R.id.textViewImageDescription);
        textViewRequestInfo = findViewById(R.id.textViewRequestInfo);
        requestButton = findViewById(R.id.buttonRequestItem);
        editTextRequestInfo = findViewById(R.id.editTextRequestInfo);


        requestButton.setOnClickListener(this);
        textViewRequestInfo.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        ImageUpload item = (ImageUpload) intent.getSerializableExtra("image");

        Picasso.get().load(item.getImageUrl()).into(imageView);
        imageName.setText(item.getImageName());
        imageDescription.setText(item.getItemDescription());
        requestNumber = item.getRequestNumber();
        imageID = item.getItemID();
        requestInfo = item.getRequestInfo();

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads").child(imageID);

        if (requestNumber <= 1) {
            textViewRequestInfo.setText(" Number of request: " + requestNumber + "\n" + item.getRequestInfo() );
        } else {
            textViewRequestInfo.setText(" Number of request: " + requestNumber + "\n" + item.getRequestInfo());
        }
        this.itemRequest = item;

     //   Toast.makeText(this, item.getItemDescription(), Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onClick(View v) {
        if (v == requestButton) {
            if (TextUtils.isEmpty(editTextRequestInfo.getText().toString())) {
                Toast.makeText(this, "Please enter your tele...", Toast.LENGTH_SHORT).show();
            } else if (requested) {
                Toast.makeText(RequestItemActivity.this, "Already requested", Toast.LENGTH_SHORT).show();
            } else {
                requested = true;
                requestInfo = itemRequest.getRequestInfo() + "\n @" + editTextRequestInfo.getText().toString();
                requestNumber++;
                saveProductInfoToDatabase();
                textViewRequestInfo.setText(" Number of request: " + requestNumber + "\n" + requestInfo);
            }
        }
    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("requestNumber", requestNumber);
        productMap.put("requestInfo", requestInfo);

        databaseReference.updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
//                            Intent intent = new Intent(RequestItemActivity.this, RequestItemActivity.class);
//                            intent.putExtra("image", itemRequest);
//                            startActivity(intent);
//
//                            loadingBar.dismiss();
                           Toast.makeText(RequestItemActivity.this, "Requested for item", Toast.LENGTH_SHORT).show();
                        } else {
//                            loadingBar.dismiss();
//                            String message = task.getException().toString();
//                            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
