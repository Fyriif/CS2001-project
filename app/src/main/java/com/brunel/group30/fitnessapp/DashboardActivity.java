package com.brunel.group30.fitnessapp;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
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
                        getWeeklyNutrition();
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
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation_view_dashboard);
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
        bmiValInsightTextView.setText(
                String.format(
                        getString(R.string.val),
                        BMI.Companion.getString(this.userInfo.calculateBMI())
                )
        );

        TextView bmiInsightTextView = dashboardInsightsViewFlipper.findViewById(R.id.text_view_insights_bmi);
        bmiInsightTextView.setText(
                String.format(
                        getString(R.string.val),
                        BMI.Companion.getString(this.userInfo.calculateBMI())
                )
        );

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

            int caloriesDaily = 0, fatDaily = 0, sodiumDaily = 0, fibreDaily = 0, proteinDaily = 0, satFatDaily = 0, sugarDaily = 0, carbsDaily = 0;
            for (DataPoint dataPoint : dataPoints) {
                caloriesDaily += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("calories").doubleValue();
                fatDaily += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("fat.total").doubleValue();
                sodiumDaily += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("sodium").doubleValue();
                fibreDaily += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("dietary_fiber").doubleValue();
                proteinDaily += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("protein").doubleValue();
                satFatDaily += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("fat.saturated").doubleValue();
                sugarDaily += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("sugar").doubleValue();
                carbsDaily += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("carbs.total").doubleValue();
            }
          
            CircularProgressIndicator dailyNutritionCircularProgress = findViewById(R.id.circular_progress_daily_calorie_intake);
            dailyNutritionCircularProgress.setCurrentProgress(caloriesDaily);

            TextView dailyFatTextView = findViewById(R.id.text_view_fat_daily);
            TextView dailyFibreTextView = findViewById(R.id.text_view_fibre_daily);
            TextView dailyProteinTextView = findViewById(R.id.text_view_protein_daily);
            TextView dailySaturatedFatTextView = findViewById(R.id.text_view_sat_fat_daily);
            TextView dailySodiumTextView = findViewById(R.id.text_view_sodium_daily);
            TextView dailySugarTextView = findViewById(R.id.text_view_sugar_daily);
            TextView dailyCarbohydratesTextView = findViewById(R.id.text_view_weekly_total_carbs);

            updateStatWithVal(dailyFatTextView, fatDaily, "mg");
            updateStatWithVal(dailyFibreTextView, fibreDaily, "mg");
            updateStatWithVal(dailyProteinTextView, proteinDaily, "mg");
            updateStatWithVal(dailySaturatedFatTextView, satFatDaily, "mg");
            updateStatWithVal(dailySodiumTextView, sodiumDaily, "mg");
            updateStatWithVal(dailySugarTextView, sugarDaily, "mg");
            updateStatWithVal(dailyCarbohydratesTextView, carbsDaily, "mg");
        });
    }

    void getWeeklyNutrition() {
        GoogleFitApi.getWeeklyNutrition(this, GoogleSignIn.getLastSignedInAccount(this)).addOnSuccessListener(dataReadResponse -> {
            List<Bucket> buckets = dataReadResponse.getBuckets();

            int fat = 0, sodium = 0, fibre = 0, protein = 0, satFat = 0, sugar = 0, carbsTotal = 0;
            for (Bucket bucket : buckets) {
                for (DataPoint dataPoint : bucket.getDataSets().get(0).getDataPoints()) {
                    fat += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("fat.total").doubleValue();
                    sodium += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("sodium").doubleValue();
                    fibre += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("dietary_fiber").doubleValue();
                    protein += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("protein").doubleValue();
                    satFat += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("fat.saturated").doubleValue();
                    sugar += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("sugar").doubleValue();
                    carbsTotal += dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("carbs.total").doubleValue();
                }
            }

            TextView weeklyFatTextView = findViewById(R.id.text_view_weekly_fat);
            TextView weeklyFibreTextView = findViewById(R.id.text_view_weekly_fibre);
            TextView weeklyProteinTextView = findViewById(R.id.text_view_weekly_protein);
            TextView weeklySaturatedFatTextView = findViewById(R.id.text_view_weekly_saturated_fat);
            TextView weeklySodiumTextView = findViewById(R.id.text_view_weekly_sodium);
            TextView weeklySugarTextView = findViewById(R.id.text_view_weekly_sugar);
            TextView weeklyCarbohydratesTextView = findViewById(R.id.text_view_weekly_total_carbs);


            updateStatWithVal(weeklyFatTextView, fat, "mg");
            updateStatWithVal(weeklyFibreTextView, fibre, "mg");
            updateStatWithVal(weeklyProteinTextView, protein, "mg");
            updateStatWithVal(weeklySaturatedFatTextView, satFat, "mg");
            updateStatWithVal(weeklySodiumTextView, sodium, "mg");
            updateStatWithVal(weeklySugarTextView, sugar, "mg");
            updateStatWithVal(weeklyCarbohydratesTextView, carbsTotal, "mg");
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

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_sign_out))
                .setMessage(getString(R.string.msg_confirm_sign_out))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .setPositiveButton(getString(R.string.action_confirm_sign_out), (arg0, arg1) -> {
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
                }).create().show();
    }

    void updateStatWithVal(TextView textView, double value, String unit) {
        textView.setText(
                String.format(
                        getString(R.string.val_with_unit),
                        value == 0.0 ? String.valueOf(0) : String.valueOf(value),
                        unit
                )
        );
    }
}
