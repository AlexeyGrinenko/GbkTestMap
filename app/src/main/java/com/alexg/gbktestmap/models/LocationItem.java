package com.alexg.gbktestmap.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class LocationItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    public int profilePhoto;

    public LocationItem(double lat, double lng, int pictureResource) {
        mPosition = new LatLng(lat, lng);
        mTitle = null;
        mSnippet = null;
        profilePhoto = pictureResource;

    }

    public LocationItem(double lat, double lng, String title, String snippet, int pictureResource) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        profilePhoto = pictureResource;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() { return mTitle; }

    @Override
    public String getSnippet() { return mSnippet; }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setSnippet(String snippet) {
        mSnippet = snippet;
    }
}
