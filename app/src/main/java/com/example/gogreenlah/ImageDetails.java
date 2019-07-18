package com.example.gogreenlah;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ImageDetails extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private DatabaseReference databaseReference;
    private EditText editTextImageDescription;
    private TextView imageName;
    private String imageID;
    private Button updateItemButton;
    private String itemDescription;

    // private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        imageView = findViewById(R.id.imageViewUpload);
        imageName = findViewById(R.id.textViewName);
        updateItemButton = findViewById(R.id.updateItemButton);
        editTextImageDescription = findViewById(R.id.editTextImageDescription);

        updateItemButton.setOnClickListener(this);

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

    private void updateItem() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                itemDescription = editTextImageDescription.getText().toString();
                    ImageUpload imageUpload = dataSnapshot.getValue(ImageUpload.class);
                  //  imageUpload.setItemDescription(itemDescription);
                }
                saveProductInfoToDatabase();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();
      //  productMap.put("itemID", productRandomKey);
     //   productMap.put("date", saveCurrentDate);
     //   productMap.put("time", saveCurrentTime);
        //  productMap.put("description", Description);
     //   productMap.put("image", downloadImageUrl);
     //   productMap.put("itemType", itemType);
        //productMap.put("price", Price);
     //   productMap.put("itemName", itemName);
        productMap.put("itemDescription", itemDescription);

        databaseReference.updateChildren(productMap)
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

    @Override
    public void onClick(View view) {
        if (view == updateItemButton) {
            updateItem();
        }
    }
}
