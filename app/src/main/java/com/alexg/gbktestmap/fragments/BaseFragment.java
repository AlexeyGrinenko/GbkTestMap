package com.alexg.gbktestmap.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.alexg.gbktestmap.models.PointModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;


public class BaseFragment extends Fragment {

    protected DatabaseReference mDatabaseReference;
    protected ArrayList<PointModel> mPointsList;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPointsList = new ArrayList<>();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount()>0) {
                            fillPointsArrayList((Map<String, Object>) dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });

    }

    private void fillPointsArrayList(Map<String, Object> users) {
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            Map singleUser = (Map) entry.getValue();
            PointModel pointmodel = new PointModel((String) singleUser.get("name"),
                    (String) singleUser.get("latitude"), (String) singleUser.get("longitude"));
            mPointsList.add(pointmodel);
        }
    }
}
