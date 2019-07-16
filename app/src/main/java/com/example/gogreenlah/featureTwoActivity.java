package com.example.gogreenlah;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gogreenlah.ImageAdapter.OnImageClickListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class featureTwoActivity extends AppCompatActivity implements ImageAdapter.OnImageClickListener {

    public static final String EXTRA_URL = "imageUrl";

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private DatabaseReference databaseReference;
    private List<ImageUpload> uploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_two);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Toast.makeText(this, "test", Toast.LENGTH_SHORT).show();

        uploads = new ArrayList<>();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("uploads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    ImageUpload upload;
                    String itemName = postSnapShot.child("itemName").getValue().toString();
                    String itemID = postSnapShot.child("itemID").getValue().toString();
                    String itemImage = postSnapShot.child("image").getValue().toString();
                    String itemType = postSnapShot.child("itemType").getValue().toString();
                    upload = new ImageUpload(itemName,itemType, itemImage, itemID);
                    uploads.add(upload);
                }

                adapter = new ImageAdapter(featureTwoActivity.this, uploads);
                recyclerView.setAdapter(adapter);
                adapter.setOnImageClickListener(featureTwoActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(featureTwoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onImageClick(int position) {

        Intent intent = new Intent(this, requestItemActivity.class);
        ImageUpload clickedImage = uploads.get(position);
        intent.putExtra(EXTRA_URL, clickedImage.getImageUrl());
        startActivity(intent);


    }
}
