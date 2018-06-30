package com.alexg.gbktestmap.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.alexg.gbktestmap.R;

public class PointsViewHolder extends RecyclerView.ViewHolder {

    public TextView nameView;
    public TextView latitudeView;
    public TextView longitudeView;
    public View containerView;

    public PointsViewHolder(View itemView) {
        super(itemView);
        containerView = itemView;
        nameView = itemView.findViewById(R.id.item_point_tv_name);
        latitudeView = itemView.findViewById(R.id.item_point_tv_latitude);
        longitudeView = itemView.findViewById(R.id.item_point_tv_longitude);
    }
}
