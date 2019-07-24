package com.example.gogreenlah;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class MyItemActivity extends AppCompatActivity implements ImageAdapter.OnImageClickListener {

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private FirebaseUser user;

    private List<ImageUpload> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);

        recyclerView = findViewById(R.id.recyclerViewMyItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        user = FirebaseAuth.getInstance().getCurrentUser();

        getItemList();
    }


    private void getItemList() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    ImageUpload upload;
                    if (postSnapShot.child("itemName").getValue() == null){
                        continue;
                    }
                    String itemName = requireNonNull(postSnapShot.child("itemName").getValue()).toString();
                    String itemID = requireNonNull(postSnapShot.child("itemID").getValue()).toString();
                    String itemImage = requireNonNull(postSnapShot.child("image").getValue()).toString();
                    String itemType = requireNonNull(postSnapShot.child("itemType").getValue()).toString();
                    String requestInfo = requireNonNull(postSnapShot.child("requestInfo").getValue()).toString();
                    long number = (long) postSnapShot.child("requestNumber").getValue();
                    int requestNumber = (int) number;
                    if (postSnapShot.child("itemDescription").getValue() != null) {
                        String itemDescription = requireNonNull(postSnapShot.child("itemDescription").getValue()).toString();
                        upload = new ImageUpload(itemName, itemType, itemImage, itemID, requestInfo, requestNumber, itemDescription);

                    } else {
                        upload = new ImageUpload(itemName, itemType, itemImage, itemID, requestInfo, requestNumber);
                    }
                    itemList.add(upload);

                    adapter = new ImageAdapter(MyItemActivity.this, itemList);
                    recyclerView.setAdapter(adapter);
                    adapter.setOnImageClickListener(MyItemActivity.this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

}

    @Override
    public void onImageClick(int position) {

        Intent intent = new Intent(this, EditItemActivity.class);
        ImageUpload clickedImage = itemList.get(position);
        intent.putExtra("image", clickedImage);
        startActivity(intent);
    }
}
