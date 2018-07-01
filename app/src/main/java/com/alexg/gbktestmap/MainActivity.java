package com.alexg.gbktestmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.alexg.gbktestmap.fragments.ListFragment;
import com.alexg.gbktestmap.fragments.MapFragment;
import com.alexg.gbktestmap.fragments.ProfileFragment;
import com.alexg.gbktestmap.models.PointModel;
import com.alexg.gbktestmap.utils.GoogleUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ProfileFragment.OnFragmentProfileListener,
        ListFragment.ListItemClickListener {
    private final String TAG = MainActivity.class.getName();

    private Fragment listFragment;
    private Fragment mapFragment;
    private Fragment profileFragment;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFragments();

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_list:
                                changeFragment(listFragment);
                                break;
                            case R.id.action_map:
                                changeFragment(mapFragment);
                                break;
                            case R.id.action_profile:
                                changeFragment(profileFragment);
                                break;
                        }
                        return true;
                    }
                });

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        PointModel pointModel1 = new PointModel("Railway station", "48.477540", "35.015261");
        database.push().setValue(pointModel1);
        PointModel pointModel2 = new PointModel("Post office", "48.467873", "35.040897");
        database.push().setValue(pointModel2);

    }

    private void initFragments() {
        listFragment = ListFragment.newInstance();
        mapFragment = MapFragment.newInstance(null);
        profileFragment = ProfileFragment.newInstance();
        changeFragment(listFragment);
    }

    private void changeFragment(Fragment newFragment) {
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragmentContainer, newFragment)
                .commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    @Override
    public void onSignOutClicked() {
        signOut();
    }

    private void signOut() {
        GoogleUtils.getGoogleSignInClient(this).signOut()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        clearDB();
                        goToSignIn();
                        finish();
                    }
                });
    }

    private void clearDB() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.removeValue();
    }

    private void goToSignIn() {
        Intent intent = new Intent(this, SignActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(PointModel model) {
        mapFragment = MapFragment.newInstance(model);
        bottomNavigationView.setSelectedItemId(R.id.action_map);
        changeFragment(mapFragment);
    }
}
