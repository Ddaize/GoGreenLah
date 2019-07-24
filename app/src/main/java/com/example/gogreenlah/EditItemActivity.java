package com.example.gogreenlah;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class EditItemActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private TextView imageName, imageDescription, textViewRequestInfo;
    private EditText itemDescription;
    private String imageUrl;
    private int requestNumber;
    private String imageID;
    private String requestInfo;
    private String newDescription;

    private EditText editTextRequestInfo;
    private Button deleteButton, updateItem;
    private ImageUpload itemRequest;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        imageView = findViewById(R.id.imageViewEditItem);
        imageName = findViewById(R.id.textViewEditItemName);
        imageDescription = findViewById(R.id.textViewEditImageDescription);
        textViewRequestInfo = findViewById(R.id.textViewEditRequestInfo);
        deleteButton = findViewById(R.id.buttonDeleteItem);
        updateItem = findViewById(R.id.buttonUpdateItem);
        itemDescription = findViewById(R.id.editTextEditItemDescription);

        deleteButton.setOnClickListener(this);
        updateItem.setOnClickListener(this);
        textViewRequestInfo.setMovementMethod(new ScrollingMovementMethod());

        Intent intent = getIntent();
        ImageUpload item = (ImageUpload) intent.getSerializableExtra("image");

        Picasso.get().load(item.getImageUrl()).into(imageView);
        imageName.setText(item.getImageName());
        imageDescription.setText(item.getItemDescription());
        itemDescription.setText(item.getItemDescription());
        requestNumber = item.getRequestNumber();
        imageID = item.getItemID();

        imageDescription.setOnClickListener(this);

        setRequestInfo();

    }

    private void setRequestInfo() {
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads").child(imageID);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String requestInfo = dataSnapshot.child("requestInfo").getValue().toString();
                int requestNumber = (int) ((long) dataSnapshot.child("requestNumber").getValue());

                if (requestNumber <= 1) {
                    textViewRequestInfo.setText(" Number of request: " + requestNumber + "\n" + requestInfo);
                } else {
                    textViewRequestInfo.setText(" Number of request: " + requestNumber + "\n" + requestInfo);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    private void deleteItem() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseRefUser = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child(imageID);
//        databaseRefUser.child("itemDescription").setValue(null);
        databaseRefUser.setValue(null);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("uploads").child(imageID);

//        databaseRef.child("itemDescription").setValue(null);
        databaseRef.setValue(null);
    }

    @Override
    public void onClick(View v) {
        if (v == deleteButton) {
            deleteItem();
            Toast.makeText(this, "item deleted", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MyItemActivity.class));
        } else if (v == imageDescription) {
            imageDescription.setVisibility(View.INVISIBLE);
            itemDescription.setVisibility(View.VISIBLE);
        } else if (v == updateItem) {
            imageDescription.setVisibility(View.VISIBLE);
            itemDescription.setVisibility(View.GONE);
            newDescription = itemDescription.getText().toString();
            imageDescription.setText(newDescription);
            saveProductInfoToDatabase();
            saveProductInfoToUser();
        }
    }

    private void saveProductInfoToDatabase() {

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads").child(imageID);

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("itemDescription", newDescription);

        databaseReference.updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                           // Toast.makeText(EditItemActivity.this, "updated item", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveProductInfoToUser() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseRefUser = FirebaseDatabase.getInstance().getReference("users").child(user.getUid()).child(imageID);

        HashMap<String, Object> productMap = new HashMap<>();
        productMap.put("itemDescription", newDescription);

        databaseRefUser.updateChildren(productMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(EditItemActivity.this, "updated item", Toast.LENGTH_SHORT).show();
                        } else {
//                            Toast.makeText(AdminAddNewProductActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
