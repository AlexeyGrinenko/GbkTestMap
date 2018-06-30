package com.alexg.gbktestmap.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.alexg.gbktestmap.GbkMapApp;
import com.alexg.gbktestmap.R;
import com.alexg.gbktestmap.models.LocationItem;
import com.alexg.gbktestmap.models.PointModel;
import com.alexg.gbktestmap.utils.GoogleUtils;
import com.alexg.gbktestmap.utils.LocationsRenderer;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

public class MapFragment extends BaseFragment {
    private final String TAG = MapFragment.class.getName();

    private static final String KEY_POSITION = "com.alexg.gbktestmap.fragments.KEY_POSITION";

    private final int RC_LOCATION = 10502;
    private final int RC_PERMISSION_LOCATION = 10503;

    private MapView mMapView;
    private GoogleMap mGoogleMap;

    private ClusterManager<LocationItem> mClusterManager;
    private int currentLocationInitCounter = 0;
    private PointModel mPointModel = null;

    public MapFragment() {
    }

    public static MapFragment newInstance(PointModel model) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putParcelable(KEY_POSITION, model);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPointModel = getArguments().getParcelable(KEY_POSITION);
            getArguments().clear();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(GbkMapApp.getAppContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestPermission();

        return rootView;
    }

    private void requestPermission() {
        if (ActivityCompat.checkSelfPermission(GbkMapApp.getAppContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(GbkMapApp.getAppContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    RC_PERMISSION_LOCATION);
        } else {
            init();
        }
    }

    private void init() {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                mGoogleMap = mMap;
                checkLocationStatus();
                initMapListeners();
                fillMapWithPoints();
            }
        });
    }

    private void fillMapWithPoints() {
        if (mPointsList != null && !mPointsList.isEmpty()) {
            ArrayList<LocationItem> mapsItemsList = new ArrayList<>();
            for (PointModel pointModel : mPointsList) {
                mapsItemsList.add(new LocationItem(Double.valueOf(pointModel.latitude), Double.valueOf(pointModel.longitude),
                        pointModel.getTitle(), pointModel.getSnippet(), R.drawable.ic_map_marker));
            }

            mClusterManager = new ClusterManager<>(GbkMapApp.getAppContext(), mGoogleMap);
            mGoogleMap.setOnCameraIdleListener(mClusterManager);
            final LocationsRenderer renderer = new LocationsRenderer(mGoogleMap, mClusterManager,
                    getLayoutInflater().inflate(R.layout.multi_profile, null));
            mClusterManager.setRenderer(renderer);

            mClusterManager.addItems(mapsItemsList);
            initClusterClickListener();
            mClusterManager.cluster();
            if (mPointModel == null) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(Double.valueOf(mPointsList.get(0).latitude),
                                Double.valueOf(mPointsList.get(0).longitude)), 5));
            } else {
                LatLng localePosition = mPointModel.getPosition();
                for (LocationItem locationModel : mapsItemsList) {
                    if (locationModel.getTitle().equalsIgnoreCase(mPointModel.name)
                            && locationModel.getPosition().latitude == mPointModel.getPosition().latitude
                            && locationModel.getPosition().longitude == mPointModel.getPosition().longitude) {

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(localePosition).zoom(17).build();
                        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                        final LocationItem locationItem = locationModel;
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Marker marker = renderer.getMarker(locationItem);
                                if (marker != null) {
                                    marker.showInfoWindow();
                                }
                            }
                        }, 2000);
                    }
                }

            }
        }
    }

    private void initClusterClickListener() {
        mGoogleMap.setOnCameraIdleListener(mClusterManager);
        mGoogleMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<LocationItem>() {
            @Override
            public boolean onClusterClick(Cluster<LocationItem> cluster) {
                // Create the builder to collect all essential cluster items for the bounds.
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder.include(item.getPosition());
                }
                final LatLngBounds bounds = builder.build();

                try {
                    mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
    }

    private void checkLocationStatus() {
        if (GoogleUtils.canGetLocation(GbkMapApp.getAppContext())) {
            initLocation();
        } else {
            showSettingsAlert();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle(R.string.text_initialization_error);
        alertDialog.setMessage(R.string.text_turn_on_location_service);

        alertDialog.setPositiveButton(
                getResources().getString(R.string.text_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, RC_LOCATION);
                    }
                });

        alertDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_LOCATION) {
            checkLocationStatus();
        }
    }

    private void initMapListeners() {
        mGoogleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (marker.isInfoWindowShown()) marker.hideInfoWindow();
            }
        });
        mGoogleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.isInfoWindowShown()) {
                    marker.hideInfoWindow();
                } else {
                    marker.showInfoWindow();
                }
                return true;
            }
        });
        mGoogleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.e(TAG, "LongClickListener start");
                showAddDialog(latLng);
            }
        });
    }

    @SuppressLint("MissingPermission")
    private void initLocation() {
        if (mPointModel == null) {
            LocationServices.getFusedLocationProviderClient(GbkMapApp.getAppContext()).getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.e(TAG, location.getLatitude() + "/" + location.getLongitude());
                                LatLng localePosition = new LatLng(location.getLatitude(), location.getLongitude());

                                CameraPosition cameraPosition = new CameraPosition.Builder().target(localePosition).zoom(3).build();
                                mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            } else if (currentLocationInitCounter < 5) {
                                currentLocationInitCounter++;
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.e(TAG, "initLocation repeat " + currentLocationInitCounter);
                                        initLocation();
                                    }
                                }, 300);
                            }
                        }
                    });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RC_PERMISSION_LOCATION:
                if (grantResults.length > 0) {
                    init();
                } else {
                    requestPermission();
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    private void showAddDialog(final LatLng latLng) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        final View dialogView = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_location, null);
        alertDialog.setView(dialogView);

        alertDialog.setPositiveButton(
                getResources().getString(R.string.text_add),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        EditText editText = dialogView.findViewById(R.id.dialog_new_location_editText);
                        String enteredText = editText.getText().toString();
                        if (!TextUtils.isEmpty(enteredText)) {
                            addNewLocation(enteredText, latLng);
                        }
                    }
                });

        alertDialog.setNegativeButton(
                getResources().getString(R.string.text_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void addNewLocation(String name, LatLng latLng) {
        PointModel pointModel = new PointModel(name, String.valueOf(latLng.latitude), String.valueOf(latLng.longitude));
        mDatabaseReference.push().setValue(pointModel);
        mPointsList.add(pointModel);
        if (mClusterManager == null) {
            mClusterManager = new ClusterManager<>(GbkMapApp.getAppContext(), mGoogleMap);
            mGoogleMap.setOnCameraIdleListener(mClusterManager);
            mClusterManager.setRenderer(new LocationsRenderer(mGoogleMap, mClusterManager,
                    getLayoutInflater().inflate(R.layout.multi_profile, null)));
            initClusterClickListener();
        }
        mClusterManager.addItem(new LocationItem(latLng.latitude, latLng.longitude, R.drawable.ic_map_marker));
        mClusterManager.cluster();
    }

}
