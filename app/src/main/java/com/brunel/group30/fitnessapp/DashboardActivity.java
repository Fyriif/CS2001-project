package com.brunel.group30.fitnessapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ViewFlipper;

import com.appizona.yehiahd.fastsave.FastSave;
import com.brunel.group30.fitnessapp.Custom.CustomNumberPicker;
import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.brunel.group30.fitnessapp.Services.CustomFirebaseMessagingService;
import com.brunel.group30.fitnessapp.Services.GoogleFitApi;
import com.brunel.group30.fitnessapp.Services.StepCountSensor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class DashboardActivity extends AppCompatActivity {
    GoogleFitApi mGoogleFitApi;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private UserInfo userInfo;

    private CircularProgressIndicator stepCountCircularProgressIndicator;
    private ViewFlipper dashboardViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FastSave.init(getApplicationContext());

        this.stepCountCircularProgressIndicator = findViewById(R.id.progress_circular_step_count);
        this.stepCountCircularProgressIndicator.setMaxProgress(10000);

        this.mAuth = FirebaseAuth.getInstance();
        this.mCurrentUser = this.mAuth.getCurrentUser();

        if (this.mCurrentUser == null) {
            startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        }

        invokeApi();

        BottomNavigationView.OnNavigationItemSelectedListener
                mOnNavigationItemSelectedListener = item -> {
            switch (item.getItemId()) {
                case R.id.navigation_dashboard_home:
                    dashboardViewFlipper.setDisplayedChild(0);
                    return true;
                case R.id.navigation_dashboard_calendar:
                    dashboardViewFlipper.setDisplayedChild(1);
                    CalendarView calendarView = findViewById(R.id.calendar_view);
                    calendarView.setOnDateChangeListener((view, year, month, dayOfMonth)
                            -> recordDataForDate());
                    return true;
                case R.id.navigation_dashboard_workouts:
                    dashboardViewFlipper.setDisplayedChild(2);
                    return true;
                case R.id.navigation_dashboard_account:
                    dashboardViewFlipper.setDisplayedChild(3);
                    return true;
            }

            return false;
        };

        dashboardViewFlipper = findViewById(R.id.view_dashboard);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle bundle = getIntent().getExtras();
        this.userInfo = new Gson().fromJson(bundle != null ?
                bundle.getString(UserInfo.COLLECTION_NAME) : null, UserInfo.class);

        CustomFirebaseMessagingService.isNewTokenRequired(getApplicationContext());
    }

    void invokeApi() {
        try {
            if (this.mGoogleFitApi == null) {
                this.mGoogleFitApi = new StepCountSensor(this,
                        this.stepCountCircularProgressIndicator);
            }
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GoogleFitApi.REQUEST_OAUTH_REQUEST_CODE) {
                mGoogleFitApi.subscribe();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (this.mGoogleFitApi != null && this.mCurrentUser != null) {
            ((StepCountSensor) this.mGoogleFitApi).unregisterFitnessDataListener();
        }
    }

    public void stepCountTarget(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_insert_step_target);
        dialog.findViewById(R.id.button_confirm).setOnClickListener(v1 -> dialog.dismiss());

        CustomNumberPicker numberPicker = dialog.findViewById(R.id.number_picker_step_target);
        numberPicker.setDisplayedValues(numberPicker.getArrayWithSteps(1000));

        dialog.show();
    }

    private void recordDataForDate() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_insert_data_calendar);
        dialog.findViewById(R.id.button_confirm).setOnClickListener(v1 -> dialog.dismiss());

        dialog.show();
    }
}