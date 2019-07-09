package com.example.gogreenlah;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
            startActivity(new Intent(this, featureThreeActivity.class));
            //open feature 3
        }
    }
}
