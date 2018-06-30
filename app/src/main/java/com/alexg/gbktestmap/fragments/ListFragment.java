package com.alexg.gbktestmap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alexg.gbktestmap.R;
import com.alexg.gbktestmap.holders.PointsViewHolder;
import com.alexg.gbktestmap.models.PointModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends Fragment {
    private final String TAG = ListFragment.class.getName();

    private static OnFragmentListListener mListener;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance() {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView databaseRecycler = view.findViewById(R.id.fragment_list_recycler_point);
        databaseRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        TextView  errorTextView = view.findViewById(R.id.fragment_list_tv_no_points);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        PointsAdapter adapter = new PointsAdapter(getActivity(), databaseReference, errorTextView);
        databaseRecycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListListener) {
            mListener = (OnFragmentListListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentListListener {
        void onItemClicked(PointModel position);
    }

    private static class PointsAdapter extends RecyclerView.Adapter<PointsViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;

        private List<String> mPointsIds = new ArrayList<>();
        private List<PointModel> mPointModels = new ArrayList<>();

        PointsAdapter(final Context context, DatabaseReference ref, final TextView errorView) {
            mContext = context;
            mDatabaseReference = ref;

            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                    if (errorView.getVisibility() == View.VISIBLE) {
                        errorView.setVisibility(View.GONE);
                    }

                    PointModel pointModel = dataSnapshot.getValue(PointModel.class);

                    mPointsIds.add(dataSnapshot.getKey());
                    mPointModels.add(pointModel);
                    notifyItemInserted(mPointModels.size() - 1);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    PointModel pointModel = dataSnapshot.getValue(PointModel.class);
                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = mPointsIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        mPointModels.set(commentIndex, pointModel);
                        notifyItemChanged(commentIndex);
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    String commentKey = dataSnapshot.getKey();

                    int commentIndex = mPointsIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        mPointsIds.remove(commentIndex);
                        mPointModels.remove(commentIndex);

                        notifyItemRemoved(commentIndex);

                        if(mPointsIds.size()==0){
                            errorView.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String previousChildName) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
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
                    int index =  position>=mPointsIds.size()? position-1 : position;
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
}
