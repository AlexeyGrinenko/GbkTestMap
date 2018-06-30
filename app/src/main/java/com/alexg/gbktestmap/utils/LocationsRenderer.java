package com.alexg.gbktestmap.utils;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alexg.gbktestmap.GbkMapApp;
import com.alexg.gbktestmap.R;
import com.alexg.gbktestmap.models.LocationItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import java.util.ArrayList;
import java.util.List;

public class LocationsRenderer extends DefaultClusterRenderer<LocationItem> {
    private final IconGenerator mIconGenerator = new IconGenerator(GbkMapApp.getAppContext());
    private final IconGenerator mClusterIconGenerator = new IconGenerator(GbkMapApp.getAppContext());
    private final ImageView mImageView;
    private final ImageView mClusterImageView;
    private final int mDimension;

    public LocationsRenderer(GoogleMap map, ClusterManager<LocationItem> mClusterManager, View view) {

        super(GbkMapApp.getAppContext(), map, mClusterManager);
        mClusterIconGenerator.setContentView(view);
        mClusterImageView = view.findViewById(R.id.image);

        mImageView = new ImageView(GbkMapApp.getAppContext());
        mDimension = (int) GbkMapApp.getAppContext().getResources().getDimension(R.dimen.cluster_image_size);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        int padding = (int) GbkMapApp.getAppContext().getResources().getDimension(R.dimen.cluster_bound_padding);
        mImageView.setPadding(padding, padding, padding, padding);
        mIconGenerator.setContentView(mImageView);
    }

    @Override
    protected void onBeforeClusterItemRendered(LocationItem person, MarkerOptions markerOptions) {
        mImageView.setImageResource(person.profilePhoto);
        Bitmap icon = mIconGenerator.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon)).title(person.getTitle());
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<LocationItem> cluster, MarkerOptions markerOptions) {
        List<Drawable> profilePhotos = new ArrayList<>(Math.min(4, cluster.getSize()));
        int width = mDimension;
        int height = mDimension;

        for (LocationItem p : cluster.getItems()) {
            // Draw 4 at most.
            if (profilePhotos.size() == 4) break;
            Drawable drawable = GbkMapApp.getAppContext().getResources().getDrawable(p.profilePhoto);
            drawable.setBounds(0, 0, width, height);
            profilePhotos.add(drawable);
        }
        MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
        multiDrawable.setBounds(0, 0, width, height);

        mClusterImageView.setImageDrawable(multiDrawable);
        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        // Always render clusters.
        return cluster.getSize() > 2;
    }
}
