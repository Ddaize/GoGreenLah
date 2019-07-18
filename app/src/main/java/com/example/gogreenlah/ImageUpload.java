package com.example.gogreenlah;

import android.net.Uri;

import java.io.Serializable;

public class ImageUpload implements Serializable {
    private String imageName;
    private String imageUrl;
    private String itemType;
    private String itemDescription;
    private Uri imageUri;
    private String itemID;

    public ImageUpload() {
        //empty constructor is needed
    }

    public ImageUpload(String name, String itemType, String imageUrl, String itemID) {
        if (name.trim().equals("")) {
            name = "Untitled";
        }
        this.imageName = name;
        this.imageUrl = imageUrl;
        this.itemType = itemType;
        this.itemID = itemID;
    }

    public ImageUpload(String name, String itemType, String imageUrl, String itemID, String itemDescription) {
        if (name.trim().equals("")) {
            name = "Untitled";
        }
        this.imageName = name;
        this.imageUrl = imageUrl;
        this.itemType = itemType;
        this.itemID = itemID;
        this.itemDescription = itemDescription;
    }

    public String getImageName() {

        return imageName;
    }

    public String getImageUrl() {

        return imageUrl;
    }
    public String getItemDescription() {

        return itemDescription;
    }

    public String getItemType() {
        return itemType;
    }



    public void setImageUrl(String imageUrl) {

        this.imageUrl = imageUrl;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }
}
