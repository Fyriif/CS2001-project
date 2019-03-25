package com.brunel.group30.fitnessapp.Services;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.brunel.group30.fitnessapp.Models.Product;
import com.brunel.group30.fitnessapp.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class GoogleFitApi {
    private static final String TAG = "GoogleFitApi";
    public static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    static Activity activity;
    static GoogleSignInAccount mGoogleSignInAccount;

    GoogleFitApi(Activity activity) throws Exception {
        GoogleFitApi.activity = activity;

        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(GoogleFitApi.activity);
        if (mGoogleSignInAccount == null) {
            throw new Exception("Unable to retrieve last signed in account");
        }

        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_LOCATION_SAMPLE)
                        .addDataType(DataType.TYPE_ACTIVITY_SAMPLES, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_WRITE)
                        .addDataType(DataType.TYPE_NUTRITION, FitnessOptions.ACCESS_WRITE)
                        .build();

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(GoogleFitApi.activity), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    GoogleFitApi.activity,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(GoogleFitApi.activity),
                    fitnessOptions);
        } else {
            subscribe();
        }
    }

    public void subscribe() {
        if (mGoogleSignInAccount != null && !Utils.INSTANCE.isEmulator()) {

        }
    }

    private static void sendData(DataType dataType, DataPoint dataPoint) {
        DataSource dataSource = new DataSource.Builder()
                .setAppPackageName(activity.getPackageName())
                .setDataType(dataType)
                .setType(DataSource.TYPE_RAW)
                .build();

        DataSet dataSet = DataSet.create(dataSource);
        dataSet.add(dataPoint);

        Fitness.getHistoryClient(GoogleFitApi.activity, GoogleFitApi.mGoogleSignInAccount)
                .insertData(dataSet)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("TEST", "It sent!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TEST", "Failed!");
                    }
                });
    }

    public static void sendNutritionalData(Product product) {
        DataSource nutritionSource = new DataSource.Builder()
                .setAppPackageName(activity.getPackageName())
                .setType(DataSource.TYPE_RAW)
                .setDataType(DataType.TYPE_NUTRITION)
                .build();

        DataPoint food = DataPoint.create(nutritionSource);
        food.setTimestamp(Utils.INSTANCE.getCurrentDateTimeInMillis(), TimeUnit.MILLISECONDS);
        food.getValue(Field.FIELD_FOOD_ITEM).setString(product.getName());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_FAT, product.getNutriments().getFat());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SODIUM, product.getNutriments().getSodium());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SATURATED_FAT, product.getNutriments().getSaturatedFat());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_PROTEIN, product.getNutriments().getProtein());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_CARBS, product.getNutriments().getCarbohydrates());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_CALORIES, product.getNutriments().getCalories());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SUGAR, product.getNutriments().getSugar());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_DIETARY_FIBER, product.getNutriments().getFiber());

        sendData(DataType.TYPE_NUTRITION, food);
    }

    public static Task<DataReadResponse> getHeight(Activity activity, GoogleSignInAccount account) {
        DataReadRequest readHeightRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_HEIGHT)
                .setTimeRange(1, Utils.INSTANCE.getCurrentDateTimeInMillis(), TimeUnit.MILLISECONDS)
                .setLimit(1)
                .enableServerQueries()
                .build();

        return Fitness.getHistoryClient(
                activity, account).readData(readHeightRequest);
    }

    public static Task<DataReadResponse> getWeight(Activity activity, GoogleSignInAccount account) {
        DataReadRequest readWeightRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(1, Utils.INSTANCE.getCurrentDateTimeInMillis(), TimeUnit.MILLISECONDS)
                .setLimit(1)
                .enableServerQueries()
                .build();

        return Fitness.getHistoryClient(
                activity, account).readData(readWeightRequest);
    }

    public static Task<DataReadResponse> getDailyNutrition(Activity activity, GoogleSignInAccount account) {
        DataReadRequest readNutritionRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_NUTRITION, DataType.AGGREGATE_NUTRITION_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(Utils.INSTANCE.getDateTimeFromMidnightInMillis(),
                        Utils.INSTANCE.getCurrentDateTimeInMillis(), TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build();

        return Fitness.getHistoryClient(
                activity, account).readData(readNutritionRequest);
    }

    public static Task<DataReadResponse> getWeeklyNutrition(Activity activity, GoogleSignInAccount account) {
        DataReadRequest readNutritionRequest = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_NUTRITION, DataType.AGGREGATE_NUTRITION_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(Utils.INSTANCE.getDateTimeLastWeekInMillis(),
                        Utils.INSTANCE.getCurrentDateTimeInMillis(), TimeUnit.MILLISECONDS)
                .enableServerQueries()
                .build();

        return Fitness.getHistoryClient(
                activity, account).readData(readNutritionRequest);
    }
}
