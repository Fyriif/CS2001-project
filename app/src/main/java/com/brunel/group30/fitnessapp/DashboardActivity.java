package com.brunel.group30.fitnessapp;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import java.util.List;

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

                        FloatingActionButton barcodeFab = findViewById(R.id.button_barcode_scanner);
                        barcodeFab.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), BarcodeScannerActivity.class)));

                        getDailyNutrition();
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

        updateStats();
    }

    void updateStats() {
        CircularProgressIndicator weightInsightCircularProgressIndicator = dashboardInsightsViewFlipper.findViewById(R.id.circular_progress_insights_weight);
        weightInsightCircularProgressIndicator.setCurrentProgress(this.userInfo.getWeight());

        TextView bmiValInsightTextView = dashboardInsightsViewFlipper.findViewById(R.id.text_view_insights_bmi_val);
        bmiValInsightTextView.setText(String.valueOf(this.userInfo.calculateBMI()));

        TextView bmiInsightTextView = dashboardInsightsViewFlipper.findViewById(R.id.text_view_insights_bmi);
        bmiInsightTextView.setText(String.valueOf(BMI.Companion.getString(this.userInfo.calculateBMI())));

        this.stepCountCircularProgressIndicator.setProgress(
                this.stepCountCircularProgressIndicator.getProgress(),
                userInfo.getGoals().getStepsTarget());
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

    void getDailyNutrition() {
        GoogleFitApi.getDailyNutrition(this, GoogleSignIn.getLastSignedInAccount(this)).addOnSuccessListener(dataReadResponse -> {
            List<DataPoint> dataPoints = dataReadResponse.getBuckets().get(0).getDataSets().get(0).getDataPoints();

            TextView dailyNutritionTextView = findViewById(R.id.text_view_daily_calorie_intake);
            dailyNutritionTextView.setText(String.valueOf(dataPoints.isEmpty() ? 0 : dataPoints.get(0).getValue(Field.FIELD_CALORIES).asInt()));
        });
    }

    public void stepCountTarget(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_insert_step_target);
        CustomNumberPicker numberPicker = dialog.findViewById(R.id.number_picker_step_target);

        dialog.findViewById(R.id.button_confirm).setOnClickListener(v1 -> {
            userInfo.getGoals().setStepsTarget((numberPicker.getValue() + 1) * 1000);
            userInfo.getGoals().updateDB(mCurrentUser.getUid());

            // Re-save the object in device's SharedPreferences
            FastSave.getInstance().saveObject(UserInfo.COLLECTION_NAME, userInfo);
            updateStats();

            dialog.dismiss();
        });

        numberPicker.setDisplayedValues(numberPicker.getArrayWithSteps(1000, ""));
        numberPicker.setValue(((int) this.stepCountCircularProgressIndicator.getMaxProgress() / 1000) - 1);

        dialog.show();
    }

    public void waterCountTarget(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_insert_water_target);
        CustomNumberPicker numberPicker = dialog.findViewById(R.id.number_picker_water_target);

        dialog.findViewById(R.id.button_confirm).setOnClickListener(v1 -> {
            userInfo.getGoals().setHydrationTarget((numberPicker.getValue() + 1) * 1000);
            userInfo.getGoals().updateDB(mCurrentUser.getUid());
            
            // Re-save the object in device's SharedPreferences
            FastSave.getInstance().saveObject(UserInfo.COLLECTION_NAME, userInfo);
            updateStats();

            dialog.dismiss();
        });
        numberPicker.setDisplayedValues(numberPicker.getArrayWithSteps(250, "ml"));
        numberPicker.setValue(((int) this.stepCountCircularProgressIndicator.getMaxProgress() / 1000) - 1);

        dialog.show();
    }

    public void calorieCountTarget(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_insert_calories_target);
        EditText caloriesTargetEditText = dialog.findViewById(R.id.edit_calories);

        dialog.findViewById(R.id.button_confirm).setOnClickListener(v1 -> {
            userInfo.getGoals().setCalorieTarget((Integer.parseInt(caloriesTargetEditText.getText().toString())));
            userInfo.getGoals().updateDB(mCurrentUser.getUid());

            // Re-save the object in device's SharedPreferences
            FastSave.getInstance().saveObject(UserInfo.COLLECTION_NAME, userInfo);
            updateStats();

            dialog.dismiss();
        });

        caloriesTargetEditText.setText(String.valueOf(userInfo.getGoals().getCalorieTarget()));
        dialog.show();
    }
}
