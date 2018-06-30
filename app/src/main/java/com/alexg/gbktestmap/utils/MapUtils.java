package com.alexg.gbktestmap.utils;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.alexg.gbktestmap.R;
import com.alexg.gbktestmap.models.LocationItem;
import com.alexg.gbktestmap.models.PointModel;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MapUtils {

    public static boolean canGetLocation(Context context) {
        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }


    public static ArrayList<LocationItem> convertPointsModels(ArrayList<PointModel> mPointsList) {
        ArrayList<LocationItem> mapsItemsList = new ArrayList<>();
        for (PointModel pointModel : mPointsList) {
            mapsItemsList.add(new LocationItem(Double.valueOf(pointModel.latitude), Double.valueOf(pointModel.longitude),
                    pointModel.getTitle(), pointModel.getSnippet(), R.drawable.ic_map_marker));
        }
        return mapsItemsList;
    }

    public static void animateCamera(GoogleMap googleMap, LatLng latLng, float zoom){
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(zoom).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
