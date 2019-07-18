package com.example.gogreenlah;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class RequestItemActivity extends AppCompatActivity {

    private ImageView imageView;
    private TextView imageName , imageDescription;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_item);

        imageView = findViewById(R.id.imageViewItem);
        imageName = findViewById(R.id.textViewItemName);
        imageDescription = findViewById(R.id.textViewImageDescription);

        Intent intent = getIntent();
        ImageUpload item = (ImageUpload) intent.getSerializableExtra("image");

        Picasso.get().load(item.getImageUrl()).into(imageView);
        imageName.setText(item.getImageName());
        imageDescription.setText(item.getItemDescription());

     //   Toast.makeText(this, item.getItemDescription(), Toast.LENGTH_SHORT).show();

    }
}
