package com.example.gogreenlah;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class NavigationActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseRef;
    private DatabaseReference databaseSet;
    private StorageReference storageRef;


    private TextView textViewUserEmail;

    private CircleImageView imageViewProfilePic;

    private Button buttonLogout, buttonFeatureOnePage, buttonFeatureTwoPage, buttonFeatureThreePage;

    private FirebaseUser user;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseRef = FirebaseDatabase.getInstance().getReference().child("users");
        storageRef = FirebaseStorage.getInstance().getReference().child("users");


        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        } else {

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);

            View headerView = navigationView.getHeaderView(0);

            user = firebaseAuth.getCurrentUser();

            Typeface myFont = Typeface.createFromAsset(getAssets(), "fonts/AlexandriaFLF-BoldItalic.ttf");

            textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);
            textViewUserEmail.setText("  welcome " + user.getEmail());
            textViewUserEmail.setTypeface(myFont);

            imageViewProfilePic = headerView.findViewById(R.id.imageViewProfilePic);
            textViewUserEmail = headerView.findViewById(R.id.textViewProfileName);
            textViewUserEmail.setText(user.getEmail());

            buttonLogout = (Button) findViewById(R.id.buttonLogout);
            buttonLogout.setOnClickListener(this);

            buttonFeatureOnePage = (Button) findViewById(R.id.buttonFeatureOnePage);
            buttonFeatureTwoPage = (Button) findViewById(R.id.buttonFeatureTwoPage);
            buttonFeatureThreePage = (Button) findViewById(R.id.buttonFeatureThreePage);

            buttonFeatureOnePage.setOnClickListener(this);
            buttonFeatureTwoPage.setOnClickListener(this);
            buttonFeatureThreePage.setOnClickListener(this);

            loadUserProfilePic();

        }

    }

    private void displayProfileInformation() {
        databaseSet.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // ImageUpload imageUpload = dataSnapshot.child("itemID").getValue().toString();

                    if (dataSnapshot.child("image").getValue() != null) {
                        String imageString = dataSnapshot.child("image").getValue().toString();
                        Picasso.get().load(Uri.parse(imageString)).fit().into(imageViewProfilePic);
                    }
                    //      String name = imageUpload.getImageName();
                    //   Toast.makeText(ImageDetails.this, name + " x", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadUserProfilePic() {
        databaseSet = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());

        if (databaseSet != null) {
            displayProfileInformation();
           // Toast.makeText(this, "image loaded", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "no profile pic", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        } else if (id == R.id.nav_my_items) {
            startActivity(new Intent(this, MyItemActivity.class));

        } else if (id == R.id.nav_profile_update) {
            startActivity(new Intent(this, UserProfileActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View view) {
        if (view == buttonLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (view == buttonFeatureOnePage) {
            startActivity(new Intent(this, featureOneActivity.class));
        }
        if (view == buttonFeatureTwoPage) {
            startActivity(new Intent(this, featureTwoActivity.class));
            //open feature 2
        }
        if (view == buttonFeatureThreePage) {
            startActivity(new Intent(this, featureThreeActivity.class));
            //open feature 3
        }
    }
}
