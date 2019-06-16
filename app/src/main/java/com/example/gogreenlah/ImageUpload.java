package com.example.gogreenlah;

public class ImageUpload {
    private String imageName;
    private String imageUrl;

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

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
