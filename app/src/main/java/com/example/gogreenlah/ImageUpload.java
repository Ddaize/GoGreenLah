package com.example.gogreenlah;

public class ImageUpload {
    private String imageName;
    private String imageUrl;
    private String itemType;
    private String itemDescription;

    public ImageUpload() {
        //empty constructor is needed
    }

    public ImageUpload(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "Untitled";
        }
        this.imageName = name;
        this.imageUrl = imageUrl;
    }

    public String getImageName() {

        return imageName;
    }

    public String getImageUrl() {

        return imageUrl;
    }

    public String getItemType() {
        return itemType;
    }

    public String getItemDescription() {
        return itemDescription;
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
