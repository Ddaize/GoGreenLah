package com.example.gogreenlah;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ImageDetails extends AppCompatActivity {

    private ImageView imageView;
    private DatabaseReference databaseReference;
    private TextView imageName;
    private String imageID;

   // private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        imageView = findViewById(R.id.imageViewUpload);
        imageName = findViewById(R.id.textViewName);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        imageID = extras.getString("id");
        String imageString = extras.getString("uri");

        Uri imageUri = Uri.parse(imageString);
       // Toast.makeText(this, imageID, Toast.LENGTH_SHORT).show();
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads").child(imageID);

        Picasso.get().load(imageUri).fit().into(imageView);

        if (imageID != null) {
            Toast.makeText(ImageDetails.this, imageID, Toast.LENGTH_SHORT).show();
            displaySpecificProductInformation();
        } else {
           Toast.makeText(ImageDetails.this, "null", Toast.LENGTH_SHORT).show();
        }


    }

    private void displaySpecificProductInformation() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                   // ImageUpload imageUpload = dataSnapshot.child("itemID").getValue().toString();
                    String name = dataSnapshot.child("itemName").getValue().toString();
              //      String name = imageUpload.getImageName();
                 //   Toast.makeText(ImageDetails.this, name + " x", Toast.LENGTH_SHORT).show();
                    imageName.setText(name);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
