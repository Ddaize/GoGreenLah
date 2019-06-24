package com.example.gogreenlah;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView textViewUserEmail;
    private Button buttonLogout;

    private Button buttonFeatureOnePage, buttonFeatureTwoPage, buttonFeatureThreePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        firebaseAuth = FirebaseAuth.getInstance();


        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        FirebaseUser user = firebaseAuth.getCurrentUser();

        textViewUserEmail = (TextView) findViewById(R.id.textViewUserEmail);

        textViewUserEmail.setText(" welcome " + user.getEmail());
        buttonLogout = (Button) findViewById(R.id.buttonLogout);

        buttonLogout.setOnClickListener(this);

        buttonFeatureOnePage = (Button) findViewById(R.id.buttonFeatureOnePage);
        buttonFeatureTwoPage = (Button) findViewById(R.id.buttonFeatureTwoPage);
        buttonFeatureThreePage = (Button) findViewById(R.id.buttonFeatureThreePage);

        buttonFeatureOnePage.setOnClickListener(this);
        buttonFeatureTwoPage.setOnClickListener(this);
        buttonFeatureThreePage.setOnClickListener(this);

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
            Toast.makeText(this, "open feature 3", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, featureThreeActivity.class));
            //open feature 3
        }
    }
}
