package com.alexg.gbktestmap.utils;

import android.app.Activity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class GoogleUtils {

    private static GoogleSignInClient mGoogleSignInClient;

    public static GoogleSignInClient getGoogleSignInClient(Activity activity) {
        if (mGoogleSignInClient == null) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();
            mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        }
        return mGoogleSignInClient;
    }

}
