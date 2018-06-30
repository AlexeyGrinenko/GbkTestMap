package com.alexg.gbktestmap.utils;

import android.support.annotation.NonNull;

import com.alexg.gbktestmap.models.PointModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

public class FirebaseUtils {

    static ArrayList<PointModel> mPointsList = new ArrayList<>();

    private FirebaseDataUtilsListener mListener;
    private static DatabaseReference database;

    public static DatabaseReference getDatabaseReference() {
        if(database==null){
            database = FirebaseDatabase.getInstance().getReference();
        }
        return database;
    }

    public static ArrayList<PointModel> getPointsArrayList() {
        DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            mPointsList = fillPointsArrayList((Map<String, Object>) dataSnapshot.getValue());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
        return mPointsList;
    }

    private static ArrayList<PointModel> fillPointsArrayList(Map<String, Object> users) {
        ArrayList<PointModel> pointsList = new ArrayList<>();
        for (Map.Entry<String, Object> entry : users.entrySet()) {
            Map singleUser = (Map) entry.getValue();
            PointModel pointmodel = new PointModel((String) singleUser.get("name"),
                    (String) singleUser.get("latitude"), (String) singleUser.get("longitude"));
            pointsList.add(pointmodel);
        }
        return pointsList;
    }

    public static ChildEventListener setChildEventListener(final FirebaseDataUtilsListener listener){

        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                listener.firebaseItemAdded(dataSnapshot.getValue(PointModel.class), dataSnapshot.getKey());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                listener.firebaseItemUpdated(dataSnapshot.getValue(PointModel.class), dataSnapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                listener.firebaseItemRemoved( dataSnapshot.getKey());

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
               listener.firebaseListenerCanceled();
            }
        };
        return childEventListener;
    }

    public interface FirebaseDataUtilsListener {
        void firebaseItemAdded(PointModel pointModel, String dataKey);
        void firebaseItemUpdated(PointModel pointModel, String dataKey);
        void firebaseItemRemoved(String dataKey);
        void firebaseListenerCanceled();
    }

}

