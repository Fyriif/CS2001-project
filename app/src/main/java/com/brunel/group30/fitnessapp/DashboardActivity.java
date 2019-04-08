package com.brunel.group30.fitnessapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.appizona.yehiahd.fastsave.FastSave;
import com.brunel.group30.fitnessapp.Custom.CustomCaloriesDialog;
import com.brunel.group30.fitnessapp.Custom.CustomHydrationDialog;
import com.brunel.group30.fitnessapp.Custom.CustomStepCountTargetDialog;
import com.brunel.group30.fitnessapp.Custom.CustomViewPager;
import com.brunel.group30.fitnessapp.Custom.CustomWeightTargetDialog;
import com.brunel.group30.fitnessapp.Fragments.DailyNutrientsInsightsPageAdapter;
import com.brunel.group30.fitnessapp.Fragments.DashboardInsightsPageAdapter;
import com.brunel.group30.fitnessapp.Fragments.NutrientsPageAdapter;
import com.brunel.group30.fitnessapp.Fragments.PatternProgressTextAdapter;
import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.brunel.group30.fitnessapp.Services.CustomFirebaseMessagingService;
import com.brunel.group30.fitnessapp.Services.GoogleFitApi;
import com.brunel.group30.fitnessapp.Services.StepCountSensor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class DashboardActivity extends AppCompatActivity {
    public static UserInfo userInfo;
    GoogleFitApi mGoogleFitApi;

    private FirebaseAuth mAuth;

    public static CircularProgressIndicator stepCountCircularProgressIndicator;
    public static CircularProgressIndicator calorieCountCircularProgressIndicator;
    public static CircularProgressIndicator hydrationCircularProgressIndicator;

    ViewFlipper dashboardViewFlipper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FastSave.init(getApplicationContext());

        this.mAuth = FirebaseAuth.getInstance();
        FirebaseUser mCurrentUser = this.mAuth.getCurrentUser();

        if (mCurrentUser == null) {
            startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        }

        Bundle bundle = getIntent().getExtras();
        userInfo = new Gson().fromJson(bundle != null ?
                bundle.getString(UserInfo.COLLECTION_NAME) : null, UserInfo.class);

        this.dashboardViewFlipper = findViewById(R.id.view_dashboard);
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

                        // Set up the Nutriments tabs
                        TabLayout nutrientsTabLayout = findViewById(R.id.tabs_nutrients_fragment);
                        CustomViewPager customViewPager = findViewById(R.id.view_pager_nutrients_fragment);
                        NutrientsPageAdapter nutrientsPageAdapter = new NutrientsPageAdapter(getSupportFragmentManager());

                        customViewPager.setAdapter(nutrientsPageAdapter);
                        nutrientsTabLayout.setupWithViewPager(customViewPager);

                        nutrientsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                            @Override
                            public void onTabSelected(TabLayout.Tab tab) {
                                switch (tab.getPosition()) {
                                    case 1:
                                        CustomViewPager dailyNutrientsInsightsViewPager = findViewById(R.id.view_pager_nutrients_insights);
                                        DailyNutrientsInsightsPageAdapter dailyNutrientsInsightsPageAdapter = new DailyNutrientsInsightsPageAdapter(getSupportFragmentManager());
                                        DotsIndicator dailyNutrientsInsightsDotsIndicator = findViewById(R.id.dots_indicator_view_pager_nutrients_insights);

                                        dailyNutrientsInsightsViewPager.setAdapter(dailyNutrientsInsightsPageAdapter);
                                        dailyNutrientsInsightsDotsIndicator.setViewPager(dailyNutrientsInsightsViewPager);

                                        hydrationCircularProgressIndicator = findViewById(R.id.circular_progress_daily_hydration_intake);

                                        updateCalorieProgress();
                                        updateHydrationProgress();
                                }
                            }

                            @Override
                            public void onTabUnselected(TabLayout.Tab tab) {

                            }

                            @Override
                            public void onTabReselected(TabLayout.Tab tab) {

                            }
                        });

                        FloatingActionButton barcodeFab = findViewById(R.id.button_barcode_scanner);
                        barcodeFab.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), BarcodeScannerActivity.class)));
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

        // Assign UI elements to variables
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation_view_dashboard);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        stepCountCircularProgressIndicator = findViewById(R.id.progress_circular_step_count);
        CustomViewPager dashboardInsightsViewPager = findViewById(R.id.view_pager_insights);
        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator_view_pager_insights);

        DashboardInsightsPageAdapter dashboardInsightsPageAdapter = new DashboardInsightsPageAdapter(getSupportFragmentManager());
        dashboardInsightsViewPager.setAdapter(dashboardInsightsPageAdapter);
        dotsIndicator.setViewPager(dashboardInsightsViewPager);

        // Start making API calls
        CustomFirebaseMessagingService.isNewTokenRequired(getApplicationContext());
        invokeApi();
    }

    /**
     * Google's Fit API gets called from here
     * Step Count Sensor listeners get created,
     * weekly and daily nutrition information is
     * also requested.
     */
    void invokeApi() {
        try {
            if (this.mGoogleFitApi == null) {
                this.mGoogleFitApi = new StepCountSensor(this,
                        stepCountCircularProgressIndicator);
            }
        } catch (Exception e) {
            e.printStackTrace();
            startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        }
    }

    public void showStepCountTargetDialog(View v) {
        CustomStepCountTargetDialog dialogFragment = new CustomStepCountTargetDialog();
        dialogFragment.show(getSupportFragmentManager(), "StepCountTargetDialog");
    }

    public void showHydrationCountIntakeAndTargetDialog(View v) {
        CustomHydrationDialog dialogFragment = new CustomHydrationDialog();
        dialogFragment.show(getSupportFragmentManager(), "HydrationTargetDialog");
    }

    public void showCalorieCountIntakeAndTargetDialog(View v) {
        CustomCaloriesDialog dialogFragment = new CustomCaloriesDialog();
        dialogFragment.show(getSupportFragmentManager(), "CalorieCountTargetDialog");
    }

    public void weightGainTarget(View v) {
        CustomWeightTargetDialog dialogFragment = new CustomWeightTargetDialog();
        dialogFragment.show(getSupportFragmentManager(), "WeightTargetDialog");
    }

    // TODO: Retrieve some hydration data and update the progress
    void updateHydrationProgress() {
        TextView dailyHydrationIntakeTargetTextView = findViewById(R.id.text_view_target_hydration);

        PatternProgressTextAdapter textAdapter = new PatternProgressTextAdapter(getString(R.string.msg_progress_hydration_pattern));
        textAdapter.formatText(0);
        hydrationCircularProgressIndicator.setProgressTextAdapter(textAdapter);

        dailyHydrationIntakeTargetTextView.setText(
                getString(
                        R.string.msg_target_with_val,
                        String.valueOf(userInfo.getGoals().getHydrationTarget()),
                        "ml"
                )
        );
    }

    void updateCalorieProgress() {
        calorieCountCircularProgressIndicator = findViewById(R.id.circular_progress_daily_calorie_intake);
        calorieCountCircularProgressIndicator.setProgress(
                userInfo.getDailyNutriments().getCalories(),
                userInfo.getGoals().getCalorieTarget()
        );

        PatternProgressTextAdapter textAdapter = new PatternProgressTextAdapter(getString(R.string.msg_progress_calorie_pattern));
        textAdapter.formatText(userInfo.getDailyNutriments().getCalories());
        calorieCountCircularProgressIndicator.setProgressTextAdapter(textAdapter);

        TextView dailyCalorieIntakeTarget = findViewById(R.id.text_view_target_calorie);
        dailyCalorieIntakeTarget.setText(
                getString(
                        R.string.msg_target_with_val,
                        String.valueOf(userInfo.getGoals().getCalorieTarget()),
                        "kcal"
                )
        );
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
}