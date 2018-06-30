package com.alexg.gbktestmap.fragments;


import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.alexg.gbktestmap.R;


public abstract class BaseFragment extends Fragment {

    protected void showErrorAlert(String errorText) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        alertDialog.setTitle(R.string.text_error_occurred);
        alertDialog.setMessage(errorText);

        alertDialog.setPositiveButton(
                getResources().getString(R.string.text_ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });

        alertDialog.show();
    }
}
