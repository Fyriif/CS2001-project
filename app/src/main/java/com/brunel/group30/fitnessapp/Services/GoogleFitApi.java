package com.brunel.group30.fitnessapp.Services;

import android.app.Activity;

import com.brunel.group30.fitnessapp.Models.Product;
import com.brunel.group30.fitnessapp.Utils.Utils;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.Task;

import java.util.concurrent.TimeUnit;

public class GoogleFitApi {
    private static final String TAG = "GoogleFitApi";
    public static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    Activity activity;
    GoogleSignInAccount mGoogleSignInAccount;

    GoogleFitApi(Activity activity) throws Exception {
        this.activity = activity;

        mGoogleSignInAccount = GoogleSignIn.getLastSignedInAccount(this.activity);
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

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this.activity), fitnessOptions)) {
        GoogleSignIn.requestPermissions(
                this.activity,
                REQUEST_OAUTH_REQUEST_CODE,
                GoogleSignIn.getLastSignedInAccount(this.activity),
                fitnessOptions);
        } else {
            subscribe();
        }
    }

    public void subscribe() {
        if (mGoogleSignInAccount != null && !Utils.INSTANCE.isEmulator()) {

        }
    }

    public static void sendNutritionalData(Product product) {
        DataSource nutritionSource = new DataSource.Builder()
                .setType(DataSource.TYPE_RAW)
                .setDataType(DataType.TYPE_NUTRITION)
                .build();

        DataPoint food = DataPoint.create(nutritionSource);
        food.setTimestamp(Utils.INSTANCE.getTimeDateInMillis(), TimeUnit.MILLISECONDS);
        food.getValue(Field.FIELD_FOOD_ITEM).setString(product.getName());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_FAT, product.getNutriments().getFat());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SODIUM, product.getNutriments().getSodium());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SATURATED_FAT, product.getNutriments().getSaturatedFat());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_PROTEIN, product.getNutriments().getProtein());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_TOTAL_CARBS, product.getNutriments().getCarbohydrates());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_CALORIES, product.getNutriments().getCalories());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_SUGAR, product.getNutriments().getSugar());
        food.getValue(Field.FIELD_NUTRIENTS).setKeyValue(Field.NUTRIENT_DIETARY_FIBER, product.getNutriments().getFiber());
    }

    public static Task<DataReadResponse> getHeight(Activity activity, GoogleSignInAccount account) {
        DataReadRequest readHeightRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_HEIGHT)
                .setTimeRange(1, Utils.INSTANCE.getTimeDateInMillis(), TimeUnit.MILLISECONDS)
                .setLimit(1)
                .enableServerQueries()
                .build();

        return Fitness.getHistoryClient(
                activity, account).readData(readHeightRequest);
    }

    public static Task<DataReadResponse> getWeight(Activity activity, GoogleSignInAccount account) {
        DataReadRequest readWeightRequest = new DataReadRequest.Builder()
                .read(DataType.TYPE_WEIGHT)
                .setTimeRange(1, Utils.INSTANCE.getTimeDateInMillis(), TimeUnit.MILLISECONDS)
                .setLimit(1)
                .enableServerQueries()
                .build();

        return Fitness.getHistoryClient(
                activity, account).readData(readWeightRequest);
    }
}
