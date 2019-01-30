package com.brunel.group30.fitnessapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.ViewFlipper;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private ViewFlipper dashboardViewFlipper;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard_home:
                        dashboardViewFlipper.setDisplayedChild(0);
                        return true;
                    case R.id.navigation_dashboard_calendar:
                        dashboardViewFlipper.setDisplayedChild(1);
                        return true;
                    case R.id.navigation_dashboard_notifications:
                        dashboardViewFlipper.setDisplayedChild(2);
                        return true;
                    case R.id.navigation_dashboard_account:
                        dashboardViewFlipper.setDisplayedChild(3);
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.mAuth = FirebaseAuth.getInstance();
        dashboardViewFlipper = findViewById(R.id.view_dashboard);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_sign_out))
                .setMessage(getString(R.string.confirm_sign_out))
                .setNegativeButton(getString(R.string.option_cancel), null)
                .setPositiveButton(getString(R.string.option_sign_out), (arg0, arg1) -> {
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }).create().show();
    }
}
