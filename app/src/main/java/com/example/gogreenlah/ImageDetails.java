package com.example.gogreenlah;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static java.util.Objects.requireNonNull;

public class ImageDetails extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private DatabaseReference databaseReference;
    private EditText editTextImageDescription;
    private TextView imageName;
    private String imageID;
    private Button updateItemButton;
    private String itemDescription;


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
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads").child(imageID);

        Picasso.get().load(imageUri).fit().into(imageView);

        if (imageID != null) {
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
                    if (dataSnapshot.child("itemName").getValue() != null) {
                        String name = requireNonNull(dataSnapshot.child("itemName").getValue()).toString();
                        imageName.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateItem() {
        saveProductInfoToDatabase();
        saveProductInfoToUserDatabase();

    }

    private void saveProductInfoToUserDatabase() {

        String user = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user).child(imageID);
        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("itemDescription", itemDescription);

        databaseReference.updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        } else {
                        }
                    }
                });
    }

    private void saveProductInfoToDatabase() {
        HashMap<String, Object> productMap = new HashMap<>();

        productMap.put("itemDescription", itemDescription);

        databaseReference.updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        } else {
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == updateItemButton) {
            itemDescription = editTextImageDescription.getText().toString();

            if (TextUtils.isEmpty(itemDescription)) {
                Toast.makeText(this, "Please enter product description...", Toast.LENGTH_SHORT).show();

            } else {
                updateItem();
                Toast.makeText(this, "Item is updated...", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
