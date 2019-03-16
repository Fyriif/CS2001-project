package com.brunel.group30.fitnessapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.appizona.yehiahd.fastsave.FastSave;
import com.brunel.group30.fitnessapp.Custom.CustomNumberPicker;
import com.brunel.group30.fitnessapp.Custom.CustomViewPager;
import com.brunel.group30.fitnessapp.Enums.BMI;
import com.brunel.group30.fitnessapp.Fragments.NutrientsPageAdapter;
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
    private ViewFlipper dashboardInsightsViewFlipper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FastSave.init(getApplicationContext());

        this.dashboardInsightsViewFlipper = findViewById(R.id.view_flipper_insights);
        this.stepCountCircularProgressIndicator = findViewById(R.id.progress_circular_step_count);

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
                    if (dashboardViewFlipper.getCurrentView() != dashboardViewFlipper.getChildAt(0)) {
                        dashboardViewFlipper.setDisplayedChild(0);
                    }
                    return true;
                case R.id.navigation_dashboard_nutrients:
                    if (dashboardViewFlipper.getCurrentView() != dashboardViewFlipper.getChildAt(1)) {
                        dashboardViewFlipper.setDisplayedChild(1);

                        TabLayout nutrientsTabLayout = findViewById(R.id.tabs_nutrients_fragment);
                        CustomViewPager customViewPager = findViewById(R.id.view_pager_nutrients_fragment);
                        NutrientsPageAdapter nutrientsPageAdapter = new NutrientsPageAdapter(getSupportFragmentManager());

                        customViewPager.setAdapter(nutrientsPageAdapter);
                        nutrientsTabLayout.setupWithViewPager(customViewPager);
                    }
                    return true;
                case R.id.navigation_dashboard_workouts:
                    if (dashboardViewFlipper.getCurrentView() != dashboardViewFlipper.getChildAt(2)) {
                        dashboardViewFlipper.setDisplayedChild(2);
                    }
                    return true;
                case R.id.navigation_dashboard_account:
                    if (dashboardViewFlipper.getCurrentView() != dashboardViewFlipper.getChildAt(3)) {
                        dashboardViewFlipper.setDisplayedChild(3);
                    }
                    return true;
            }

            return false;
        };

        this.dashboardViewFlipper = findViewById(R.id.view_dashboard);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        Bundle bundle = getIntent().getExtras();
        this.userInfo = new Gson().fromJson(bundle != null ?
                bundle.getString(UserInfo.COLLECTION_NAME) : null, UserInfo.class);

        CustomFirebaseMessagingService.isNewTokenRequired(getApplicationContext());

        this.stepCountCircularProgressIndicator.setMaxProgress(userInfo.getGoals().getStepsTarget());

        updateStats();
    }

    void updateStats() {
        TextView weightInsightTextView = dashboardInsightsViewFlipper.findViewById(R.id.text_view_insights_weight_kg);
        weightInsightTextView.setText(String.valueOf(this.userInfo.getWeight()));

        TextView bmiValInsightTextView = dashboardInsightsViewFlipper.findViewById(R.id.text_view_insights_bmi_val);
        bmiValInsightTextView.setText(String.valueOf(this.userInfo.calculateBMI()));

        TextView bmiInsightTextView = dashboardInsightsViewFlipper.findViewById(R.id.text_view_insights_bmi);
        bmiInsightTextView.setText(String.valueOf(BMI.Companion.getString(this.userInfo.calculateBMI())));
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

    public void stepCountTarget(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_insert_step_target);
        CustomNumberPicker numberPicker = dialog.findViewById(R.id.number_picker_step_target);

        dialog.findViewById(R.id.button_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userInfo.getGoals().setStepsTarget((numberPicker.getValue() + 1) * 1000);
                userInfo.getGoals().updateDB(mCurrentUser.getUid());
                dialog.dismiss();
            }
        });

        numberPicker.setDisplayedValues(numberPicker.getArrayWithSteps(1000));
        numberPicker.setValue(((int) this.stepCountCircularProgressIndicator.getMaxProgress() / 1000) - 1);

        dialog.show();
    }

    private void recordDataForDate() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_insert_data_calendar);
        dialog.findViewById(R.id.button_confirm).setOnClickListener(v1 -> dialog.dismiss());

        dialog.show();
    }
}
