package com.alexg.gbktestmap.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class PointModel implements Parcelable {

    public String name;
    public String latitude;
    public String longitude;

    public PointModel() {
    }

    public PointModel(String name, String latitude, String longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public LatLng getPosition() {
        return new LatLng(Double.valueOf(latitude), Double.valueOf(longitude));
    }

    public String getTitle() {
        return name;
    }

    public String getSnippet() {
        return null;
    }


    private PointModel(Parcel in) {
        name= in.readString();
        latitude= in.readString();
        longitude= in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
    }

    public static final Parcelable.Creator<PointModel> CREATOR = new Parcelable.Creator<PointModel>() {
        @Override
        public PointModel createFromParcel(Parcel in) {
            return new PointModel(in);
        }

        @Override
        public PointModel[] newArray(int size) {
            return new PointModel[size];
        }
    };

}
