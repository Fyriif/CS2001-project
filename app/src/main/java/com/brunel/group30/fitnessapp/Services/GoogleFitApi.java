package com.brunel.group30.fitnessapp.Services;

import android.app.Activity;
import android.util.Log;

import com.brunel.group30.fitnessapp.DashboardActivity;
import com.brunel.group30.fitnessapp.Models.Nutriments;
import com.brunel.group30.fitnessapp.Models.Product;
import com.brunel.group30.fitnessapp.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.Task;

import java.util.List;
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
                .addOnSuccessListener(aVoid -> {
                    // Reset daily and weekly nutriments to null
                    // in order to re-trigger getting data from Google Fit
                    DashboardActivity.userInfo.setDailyNutriments(null);
                    DashboardActivity.userInfo.setWeeklyNutriments(null);
                })
                .addOnFailureListener(e -> Log.e("GoogleFitApi::sendData", e.getMessage()));
    }

    public static void sendNutritionalData(Product product) {
        if (product != null) {
            DataSource nutritionSource = new DataSource.Builder()
                    .setAppPackageName(activity.getPackageName())
                    .setType(DataSource.TYPE_RAW)
                    .setDataType(DataType.TYPE_NUTRITION)
                    .build();

            DataPoint food = DataPoint.create(nutritionSource);
            food.setTimestamp(Utils.INSTANCE.getCurrentDateTimeInMillis(), TimeUnit.MILLISECONDS);
            food.getValue(Field.FIELD_FOOD_ITEM).setString(product.getName());
            food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_FAT, (float) product.getNutriments().getFat());
            food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SODIUM, (float) product.getNutriments().getSodium());
            food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SATURATED_FAT, (float) product.getNutriments().getSaturatedFat());
            food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_PROTEIN, (float) product.getNutriments().getProtein());
            food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_CARBS, (float) product.getNutriments().getCarbohydrates());
            food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_CALORIES, (float) product.getNutriments().getCalories());
            food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SUGAR, (float) product.getNutriments().getSugar());
            food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_DIETARY_FIBER, (float) product.getNutriments().getFiber());

            sendData(DataType.TYPE_NUTRITION, food);
        }

        // TODO: add else statement, check if user wants to try scanning the barcode again for instance.
    }

    public static void sendHydrationData(int milliliters) {
        if (milliliters > 0) {
            DataSource hydrationSource = new DataSource.Builder()
                    .setAppPackageName(activity.getPackageName())
                    .setType(DataSource.TYPE_RAW)
                    .setDataType(DataType.TYPE_HYDRATION)
                    .build();

            DataPoint water = DataPoint.create(hydrationSource);
            water.setTimestamp(Utils.INSTANCE.getCurrentDateTimeInMillis(), TimeUnit.MILLISECONDS);
            water.getValue(Field.FIELD_VOLUME).setFloat((float) milliliters / 1000);

            sendData(DataType.TYPE_HYDRATION, water);
        }

        // TODO: add else statement, check if user wants to try scanning the barcode again for instance.
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

    private static Task<DataReadResponse> getDailyNutrition(Activity activity, GoogleSignInAccount account) {
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

    private static Task<DataReadResponse> getWeeklyNutrition(Activity activity, GoogleSignInAccount account) {
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

    static void getDailyNutrition() {
        GoogleFitApi.getDailyNutrition(activity, mGoogleSignInAccount).addOnSuccessListener(dataReadResponse -> {
            List<DataPoint> dataPoints = dataReadResponse.getBuckets().get(0).getDataSets().get(0).getDataPoints();

            Nutriments dailyNutriments = DashboardActivity.userInfo.getDailyNutriments();
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

            DashboardActivity.userInfo.setDailyNutriments(dailyNutriments);
        });
    }

    static void getWeeklyNutrition() {
        GoogleFitApi.getWeeklyNutrition(activity, mGoogleSignInAccount).addOnSuccessListener(dataReadResponse -> {
            List<Bucket> buckets = dataReadResponse.getBuckets();

            Nutriments weeklyNutriments = DashboardActivity.userInfo.getWeeklyNutriments();
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

            DashboardActivity.userInfo.setWeeklyNutriments(weeklyNutriments);
        });
    }
}
