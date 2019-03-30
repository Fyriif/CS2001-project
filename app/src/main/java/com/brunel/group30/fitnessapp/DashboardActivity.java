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
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.appizona.yehiahd.fastsave.FastSave;
import com.brunel.group30.fitnessapp.Custom.CustomCaloriesDialog;
import com.brunel.group30.fitnessapp.Custom.CustomAutoSwipeTask;
import com.brunel.group30.fitnessapp.Custom.CustomHydrationDialog;
import com.brunel.group30.fitnessapp.Custom.CustomNumberPicker;
import com.brunel.group30.fitnessapp.Custom.CustomViewPager;
import com.brunel.group30.fitnessapp.Enums.BMI;
import com.brunel.group30.fitnessapp.Fragments.DailyNutrientsInsightsPageAdapter;
import com.brunel.group30.fitnessapp.Fragments.DashboardInsightsPageAdapter;
import com.brunel.group30.fitnessapp.Fragments.NutrientsPageAdapter;
import com.brunel.group30.fitnessapp.Models.Nutriments;
import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.brunel.group30.fitnessapp.Services.CustomFirebaseMessagingService;
import com.brunel.group30.fitnessapp.Services.GoogleFitApi;
import com.brunel.group30.fitnessapp.Services.StepCountSensor;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.util.List;

import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;

public class DashboardActivity extends AppCompatActivity {
    public static UserInfo userInfo;
    GoogleFitApi mGoogleFitApi;

    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private CircularProgressIndicator stepCountCircularProgressIndicator;
    private CircularProgressIndicator calorieCountCircularProgressIndicator;
    private ViewFlipper dashboardViewFlipper;
    private CustomViewPager dashboardInsightsViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.stepCountCircularProgressIndicator = findViewById(R.id.progress_circular_step_count);

        FastSave.init(getApplicationContext());

        this.mAuth = FirebaseAuth.getInstance();
        this.mCurrentUser = this.mAuth.getCurrentUser();

        if (this.mCurrentUser == null) {
            startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        }

        Bundle bundle = getIntent().getExtras();
        userInfo = new Gson().fromJson(bundle != null ?
                bundle.getString(UserInfo.COLLECTION_NAME) : null, UserInfo.class);

        CustomFirebaseMessagingService.isNewTokenRequired(getApplicationContext());

        invokeApi();
        getDailyNutrition();
        getWeeklyNutrition();

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

                                        calorieCountCircularProgressIndicator = findViewById(R.id.circular_progress_daily_calorie_intake);
                                        calorieCountCircularProgressIndicator.setProgress(
                                                userInfo.getDailyNutriments().getCalories(),
                                                userInfo.getGoals().getCalorieTarget()
                                        );

                                        // THIS IS A BIT BUGGY!
                                        //new CustomAutoSwipeTask(dailyNutrientsInsightsViewPager, dailyNutrientsInsightsPageAdapter.getCount());
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

        this.dashboardViewFlipper = findViewById(R.id.view_dashboard);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation_view_dashboard);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.dashboardInsightsViewPager = findViewById(R.id.view_pager_insights);

        DotsIndicator dotsIndicator = findViewById(R.id.dots_indicator_view_pager_insights);
        DashboardInsightsPageAdapter dashboardInsightsPageAdapter = new DashboardInsightsPageAdapter(getSupportFragmentManager());

        this.dashboardInsightsViewPager.setAdapter(dashboardInsightsPageAdapter);
        dotsIndicator.setViewPager(dashboardInsightsViewPager);

        new CustomAutoSwipeTask(dashboardInsightsViewPager, dashboardInsightsPageAdapter.getCount());
    }

    void updateStats() {
        CircularProgressIndicator weightInsightCircularProgressIndicator = dashboardInsightsViewPager.findViewById(R.id.circular_progress_insights_weight);
        TextView bmiValInsightTextView = dashboardInsightsViewPager.findViewById(R.id.text_view_insights_bmi_val);
        TextView bmiInsightTextView = dashboardInsightsViewPager.findViewById(R.id.text_view_insights_bmi);

        weightInsightCircularProgressIndicator.setCurrentProgress(userInfo.getWeight());

        bmiValInsightTextView.setText(
                String.format(
                        getString(R.string.val),
                        BMI.Companion.getString(userInfo.calculateBMI())
                )
        );

        bmiInsightTextView.setText(
                String.format(
                        getString(R.string.val),
                        BMI.Companion.getString(userInfo.calculateBMI())
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

            Nutriments dailyNutriments = userInfo.getDailyNutriments();
            if (dailyNutriments == null) {
                dailyNutriments = new Nutriments();
            }

            for (DataPoint dataPoint : dataPoints) {
                dailyNutriments.setCalories(dailyNutriments.getCalories() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("calories").doubleValue());
                dailyNutriments.setFat(dailyNutriments.getFat() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("fat.total").doubleValue());
                dailyNutriments.setSodium(dailyNutriments.getSodium() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("sodium").doubleValue());
                dailyNutriments.setFiber(dailyNutriments.getFiber() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("dietary_fiber").doubleValue());
                dailyNutriments.setProtein(dailyNutriments.getProtein() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("protein").doubleValue());
                dailyNutriments.setSaturatedFat(dailyNutriments.getSaturatedFat() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("fat.saturated").doubleValue());
                dailyNutriments.setSugar(dailyNutriments.getSugar() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("sugar").doubleValue());
                dailyNutriments.setCarbohydrates(dailyNutriments.getCarbohydrates() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("carbs.total").doubleValue());
            }

            userInfo.setDailyNutriments(dailyNutriments);
        });
    }

    void getWeeklyNutrition() {
        GoogleFitApi.getWeeklyNutrition(this, GoogleSignIn.getLastSignedInAccount(this)).addOnSuccessListener(dataReadResponse -> {
            List<Bucket> buckets = dataReadResponse.getBuckets();

            Nutriments weeklyNutriments = userInfo.getWeeklyNutriments();
            if (weeklyNutriments == null) {
                weeklyNutriments = new Nutriments();
            }

            for (Bucket bucket : buckets) {
                for (DataPoint dataPoint : bucket.getDataSets().get(0).getDataPoints()) {
                    weeklyNutriments.setFat(weeklyNutriments.getFat() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("fat.total").doubleValue());
                    weeklyNutriments.setSodium(weeklyNutriments.getSodium() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("sodium").doubleValue());
                    weeklyNutriments.setFiber(weeklyNutriments.getFiber() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("dietary_fiber").doubleValue());
                    weeklyNutriments.setProtein(weeklyNutriments.getProtein() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("protein").doubleValue());
                    weeklyNutriments.setSaturatedFat(weeklyNutriments.getSaturatedFat() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("fat.saturated").doubleValue());
                    weeklyNutriments.setSugar(weeklyNutriments.getSugar() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("sugar").doubleValue());
                    weeklyNutriments.setCarbohydrates(weeklyNutriments.getCarbohydrates() + dataPoint.getValue(Field.FIELD_NUTRIENTS).getKeyValue("carbs.total").doubleValue());
                }
            }

            userInfo.setWeeklyNutriments(weeklyNutriments);
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
        CustomCaloriesDialog dialogFragment = new CustomCaloriesDialog();
        dialogFragment.show(getSupportFragmentManager(), "CaloriesDialog");
    }

    public void weightGainTarget(View v) {
        final Dialog dialog= new Dialog(this);
        dialog.setContentView(R.layout.dialog_insert_weight_target);

        CustomNumberPicker numberPicker = dialog.findViewById(R.id.number_picker_weight_target);
        numberPicker.setMaxValue(userInfo.getWeight() + 50);
        numberPicker.setMinValue(userInfo.getWeight() - 50);
        numberPicker.setValue(userInfo.getWeight());

        dialog.findViewById(R.id.button_confirm).setOnClickListener(v1 -> {
            userInfo.getGoals().setWeightTarget(numberPicker.getValue());
            userInfo.getGoals().updateDB(mCurrentUser.getUid());

            FastSave.getInstance().saveObject(UserInfo.COLLECTION_NAME, userInfo);
            updateStats();

            dialog.dismiss();
        });
    }

    public void hydrationCountTarget(View v) {
        CustomHydrationDialog dialogFragment = new CustomHydrationDialog();
        dialogFragment.show(getSupportFragmentManager(), "HydrationDialog");
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