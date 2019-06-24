package com.example.gogreenlah;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class featureTwoActivity extends AppCompatActivity {

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

        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    ImageUpload upload = postSnapShot.getValue(ImageUpload.class);
                    uploads.add(upload);
                }

                adapter = new ImageAdapter(featureTwoActivity.this, uploads);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(featureTwoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
