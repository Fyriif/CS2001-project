package com.brunel.group30.fitnessapp.Services;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;

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
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_WRITE)
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
        if (mGoogleSignInAccount != null) {
            Fitness.getRecordingClient(activity, mGoogleSignInAccount)
                    .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed!");
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.", task.getException());
                                }
                            });
        }
    }
}
