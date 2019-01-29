package com.brunel.group30.fitnessapp;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.widget.ViewFlipper;

public class DashboardActivity extends AppCompatActivity {

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

        dashboardViewFlipper = findViewById(R.id.view_dashboard);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
