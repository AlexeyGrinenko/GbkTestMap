package com.alexg.gbktestmap.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alexg.gbktestmap.R;
import com.alexg.gbktestmap.fragments.ListFragment;
import com.alexg.gbktestmap.holders.PointsViewHolder;
import com.alexg.gbktestmap.models.PointModel;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class PointsAdapter extends RecyclerView.Adapter<PointsViewHolder> {

    private Context mContext;
    private DatabaseReference mDatabaseReference;

    private ListFragment.ListItemClickListener mListener;

    private List<String> mPointsIds;
    private List<PointModel> mPointModels;

    public PointsAdapter(final Context context, DatabaseReference ref, List<PointModel> pointModelList,
                         List<String> pointsIds, ListFragment.ListItemClickListener listener) {
        mContext = context;
        mDatabaseReference = ref;
        mListener = listener;
        mPointModels = pointModelList;
        mPointsIds = pointsIds;
    }

    @Override
    public PointsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_point, parent, false);
        return new PointsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointsViewHolder holder, final int position) {
        PointModel pointModel = mPointModels.get(position);
        holder.nameView.setText(pointModel.name);
        holder.latitudeView.setText(pointModel.latitude);
        holder.longitudeView.setText(pointModel.longitude);
        holder.containerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                int index = position >= mPointsIds.size() ? position - 1 : position;
                mDatabaseReference.child(mPointsIds.get(index)).removeValue();
                return true;
            }
        });
        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClicked(mPointModels.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPointModels.size();
    }
}
