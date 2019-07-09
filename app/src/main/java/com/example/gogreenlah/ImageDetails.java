package com.example.gogreenlah;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageDetails extends AppCompatActivity {

    private ImageView imageView;

    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_details);

        imageView = findViewById(R.id.imageViewDetails);

        String imageString = getIntent().getStringExtra("uri");
        Uri imageUri = Uri.parse(imageString);

        Picasso.get().load(imageUri).fit().into(imageView);

    }
}
