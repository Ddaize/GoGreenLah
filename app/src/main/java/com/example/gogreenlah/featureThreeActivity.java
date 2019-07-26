package com.example.gogreenlah;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class featureThreeActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMyLocationClickListener, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnMarkerClickListener {

    GoogleMap map;
    private Button buttonRequestLocation;
    private static final int REQUEST_LOCATION_PERMISSION = 1;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_three);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buttonRequestLocation = findViewById(R.id.buttonRequestLocation);
        buttonRequestLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (ContextCompat.checkSelfPermission(featureThreeActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(featureThreeActivity.this, "You have already granted this permission", Toast.LENGTH_SHORT).show();
        } else {
            requestLocationPermission();
        }
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("Permission to access location is needed")
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(featureThreeActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    this.map.setMyLocationEnabled(true);
                    this.map.setOnMyLocationButtonClickListener(this);
                    this.map.setOnMyLocationClickListener(this);
                    Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.map = googleMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings mapUiSettings = googleMap.getUiSettings();
        mapUiSettings.setAllGesturesEnabled(true);
        mapUiSettings.setIndoorLevelPickerEnabled(true);

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(this);
            googleMap.setOnMyLocationClickListener(this);
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("coordinates");

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                List <MarkerData> coordinates = new ArrayList<>();
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    MarkerData markerData;
                    //saveMarkerInfoToDatabase(markerData);

                    double lat = (double) s.child("Lat").getValue();
                    double lng = (double) s.child("Lng").getValue();
                    String title = s.child("title").getValue().toString();
                    String snippet = s.child("snippet").getValue().toString();

                    markerData = new MarkerData(lat, lng, title, snippet);
                    coordinates.add(markerData);
                }
                for (int i = 0; i < coordinates.size(); i++) {

                    createMarker(coordinates.get(i).getLat(), coordinates.get(i).getLng(), coordinates.get(i).getTitle(), coordinates.get(i).getSnippet());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
/*
        LatLng NUS = new LatLng(1.290665504, 103.772663576);
        map.addMarker(new MarkerOptions().position(NUS).title("NUS").snippet("School"));
        map.moveCamera(CameraUpdateFactory.newLatLng(NUS));
        */



    }

    protected Marker createMarker(double lat, double lng, String title, String snippet) {

        return this.map.addMarker(new MarkerOptions()
                .position(new LatLng(lat, lng))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name)));
    }

    /*
    private void saveMarkerInfoToDatabase(MarkerData markerData) {
        HashMap<String, Object> markerDataMap = new HashMap<>();
        String locationKey = markerData.getTitle();
        markerDataMap.put("location", locationKey);
        markerDataMap.put("Lat", markerData.getLat());
        markerDataMap.put("Lng", markerData.getLng());

        mDatabase.child(locationKey).updateChildren(markerDataMap);
    }
  */

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}
