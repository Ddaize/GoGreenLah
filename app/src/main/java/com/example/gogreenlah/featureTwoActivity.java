package com.example.gogreenlah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

public class featureTwoActivity extends AppCompatActivity implements OnImageClickListener {

    public static final String EXTRA_URL = "imageUrl";

    private RecyclerView recyclerView;
    private ImageAdapter adapter;

    private DatabaseReference databaseReference;
    private List<ImageUpload> uploads = new ArrayList<>();
    private List<ImageUpload> uploadsFullList = new ArrayList<>();
    private List<ImageUpload> uploadsFilteredList = new ArrayList<>();


    private String spinnerItemType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_two);

        final Spinner mySpinner = findViewById(R.id.spinnerFilter);
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(featureTwoActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.filteredList));
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(spinnerAdapter);
        mySpinner.setSelection(0);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


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
                    String requestInfo = postSnapShot.child("requestInfo").getValue().toString();
                    long number = (long) postSnapShot.child("requestNumber").getValue();
                    Integer requestNumber = (int) number;
                    if (postSnapShot.child("itemDescription").getValue()!= null) {
                        String itemDescription = postSnapShot.child("itemDescription").getValue().toString();
                        upload = new ImageUpload(itemName, itemType, itemImage, itemID, requestInfo,requestNumber, itemDescription);

                    } else {
                        upload = new ImageUpload(itemName, itemType, itemImage, itemID, requestInfo,requestNumber);
                    }
                    uploads.add(upload);
                    uploadsFilteredList.add(upload);
                }

                adapter = new ImageAdapter(featureTwoActivity.this, uploadsFilteredList);
                recyclerView.setAdapter(adapter);
                adapter.setOnImageClickListener(featureTwoActivity.this);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(featureTwoActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    getSelectedCategoryItems(mySpinner.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void getSelectedCategoryItems (String selected) {

        uploadsFilteredList.clear();

        if (selected.equals("All")) {
            for (ImageUpload item : uploads) {
                uploadsFilteredList.add(item);
            }
        } else {
            for (ImageUpload item : uploads) {
                if (item.getItemType().equals(selected)) {
                    uploadsFilteredList.add(item);
                }
            }
        }

        adapter = new ImageAdapter(featureTwoActivity.this, uploadsFilteredList);
        recyclerView.setAdapter(adapter);
        adapter.setOnImageClickListener(featureTwoActivity.this);
    }

    @Override
    public void onImageClick(int position) {

        Intent intent = new Intent(this, RequestItemActivity.class);
        ImageUpload clickedImage = uploadsFilteredList.get(position);
        intent.putExtra("image", clickedImage);
        startActivity(intent);
    }

}
