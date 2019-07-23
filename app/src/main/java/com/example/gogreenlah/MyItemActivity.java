package com.example.gogreenlah;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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

public class MyItemActivity extends AppCompatActivity implements ImageAdapter.OnImageClickListener {

    public static final String EXTRA_URL = "imageUrl";

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private DatabaseReference databaseReference;
    private FirebaseUser user;

    private List<String> userItemsID = new ArrayList<>();
    private List<ImageUpload> itemList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);

        recyclerView = findViewById(R.id.recyclerViewMyItems);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        user = FirebaseAuth.getInstance().getCurrentUser();

        // getUserItems();

        getItemList();
    }

    private void getUserItems() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

        Toast.makeText(this, "getting user item", Toast.LENGTH_SHORT).show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    if (postSnapShot.child("itemID").getValue() != null) {
                        String itemID = postSnapShot.child("itemID").getValue().toString();
                        Toast.makeText(MyItemActivity.this, itemID, Toast.LENGTH_SHORT).show();
                        userItemsID.add(itemID);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getItemList() {

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());


        //    Toast.makeText(this, userItemsID.get(j), Toast.LENGTH_SHORT).show();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapShot : dataSnapshot.getChildren()) {
                    ImageUpload upload;
                    if (postSnapShot.child("itemName").getValue() == null){
                        continue;
                    }
                    String itemName = postSnapShot.child("itemName").getValue().toString();
                    String itemID = postSnapShot.child("itemID").getValue().toString();
                    String itemImage = postSnapShot.child("image").getValue().toString();
                    String itemType = postSnapShot.child("itemType").getValue().toString();
                    String requestInfo = postSnapShot.child("requestInfo").getValue().toString();
                    long number = (long) postSnapShot.child("requestNumber").getValue();
                    Integer requestNumber = (int) number;
                    if (postSnapShot.child("itemDescription").getValue() != null) {
                        String itemDescription = postSnapShot.child("itemDescription").getValue().toString();
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

        Intent intent = new Intent(this, RequestItemActivity.class);
        ImageUpload clickedImage = itemList.get(position);
        intent.putExtra("image", clickedImage);
        startActivity(intent);
    }
}
