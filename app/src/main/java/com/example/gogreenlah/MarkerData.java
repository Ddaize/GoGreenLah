package com.example.gogreenlah;

import com.google.android.gms.maps.model.LatLng;

public class MarkerData {

    private double lat;
    private double lng;
    private String title;
    private String snippet;

    public MarkerData(double lat, double lng, String title, String snippet) {
        this.lat = lat;
        this.lng = lng;
        this.title = title;
        this.snippet = snippet;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public String getTitle() {
        return title;
    }

    public String getSnippet() {
        return snippet;
    }

}
