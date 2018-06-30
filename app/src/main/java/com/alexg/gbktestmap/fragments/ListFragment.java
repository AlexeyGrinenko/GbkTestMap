package com.alexg.gbktestmap.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexg.gbktestmap.R;
import com.alexg.gbktestmap.adapters.PointsAdapter;
import com.alexg.gbktestmap.models.PointModel;
import com.alexg.gbktestmap.utils.FirebaseUtils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ListFragment extends BaseFragment implements FirebaseUtils.FirebaseDataUtilsListener {
    private final String TAG = ListFragment.class.getName();

    private static ListItemClickListener mListener;

    private TextView errorTextView;
    private PointsAdapter adapter;
    private List<String> mPointsIds;
    private List<PointModel> mPointModels;

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
        errorTextView = view.findViewById(R.id.fragment_list_tv_no_points);

        mPointsIds = new ArrayList<>();
        mPointModels = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        ChildEventListener childEventListener = FirebaseUtils.setChildEventListener(this);
        databaseReference.addChildEventListener(childEventListener);

        adapter = new PointsAdapter(getActivity(), databaseReference, mPointModels, mPointsIds, mListener);
        databaseRecycler.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ListItemClickListener) {
            mListener = (ListItemClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ListItemClickListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void firebaseItemAdded(PointModel pointModel, String dataKey) {
        if (errorTextView.getVisibility() == View.VISIBLE) {
            errorTextView.setVisibility(View.GONE);
        }

        mPointsIds.add(dataKey);
        mPointModels.add(pointModel);
        adapter.notifyItemInserted(mPointModels.size() - 1);
    }

    @Override
    public void firebaseItemUpdated(PointModel pointModel, String dataKey) {
        int commentIndex = mPointsIds.indexOf(dataKey);
        if (commentIndex > -1) {
            mPointModels.set(commentIndex, pointModel);
            adapter.notifyItemChanged(commentIndex);
        }
    }

    @Override
    public void firebaseItemRemoved(String dataKey) {

        int commentIndex = mPointsIds.indexOf(dataKey);
        if (commentIndex > -1) {
            mPointsIds.remove(commentIndex);
            mPointModels.remove(commentIndex);

            adapter.notifyItemRemoved(commentIndex);

            if (mPointsIds.size() == 0) {
                errorTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void firebaseListenerCanceled() {
        showErrorAlert("Failed to load points");
    }


    public interface ListItemClickListener {
        void onItemClicked(PointModel position);
    }
}
