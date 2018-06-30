package com.alexg.gbktestmap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.alexg.gbktestmap.utils.Consts;
import com.alexg.gbktestmap.utils.GoogleUtils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class SignActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 10501;
    private final String TAG = SignActivity.class.getName();
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        mProgressBar = findViewById(R.id.sign_in_progressBar);

        SharedPreferences sharedPref = getSharedPreferences(Consts.TAG_USER_LOGGED, Context.MODE_PRIVATE);
        String userId = sharedPref.getString(Consts.KEY_USER_ID, "");
        if (!TextUtils.isEmpty(userId)) {
            startMainActivity();
        } else {
            mProgressBar.setVisibility(View.GONE);
            initGoogleButton();
        }
    }

    private void startMainActivity() {
        Intent mainActivity = new Intent(this, MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    private void initGoogleButton() {
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setVisibility(View.VISIBLE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });
    }

    private void signIn() {
        Intent signInIntent = GoogleUtils.getGoogleSignInClient(this).getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
            startMainActivity();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        }
    }

    private void updateUI(@Nullable GoogleSignInAccount account) {
        if (account != null) {
            mProgressBar.setVisibility(View.INVISIBLE);

            SharedPreferences sharedPref = getSharedPreferences(Consts.TAG_USER_LOGGED, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(Consts.KEY_USER_ID, account.getId());
            editor.apply();
        }
    }

}
