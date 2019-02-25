package com.brunel.group30.fitnessapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.appizona.yehiahd.fastsave.FastSave;
import com.brunel.group30.fitnessapp.Models.NotificationToken;
import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

public class DashboardActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_OAUTH = 1;
    private static final String OAUTH_PENDING = "auth_pending";
    private boolean authInProgress = false;

    private TextView stepCountTextView;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mFirestore;
    private FirebaseUser mCurrentUser;

    private ViewFlipper dashboardViewFlipper;

    private UserInfo userInfo;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_dashboard_home:
                dashboardViewFlipper.setDisplayedChild(0);
                return true;
            case R.id.navigation_dashboard_calendar:
                dashboardViewFlipper.setDisplayedChild(1);
                return true;
            case R.id.navigation_dashboard_notifications:
                dashboardViewFlipper.setDisplayedChild(2);
                return true;
            case R.id.navigation_dashboard_account:
                dashboardViewFlipper.setDisplayedChild(3);
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        FastSave.init(getApplicationContext());

        this.stepCountTextView = findViewById(R.id.text_view_step_count);

        this.mAuth = FirebaseAuth.getInstance();
        this.mFirestore = FirebaseFirestore.getInstance();
        this.mCurrentUser = this.mAuth.getCurrentUser();

        if (this.mCurrentUser == null) {
            startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
        }

        dashboardViewFlipper = findViewById(R.id.view_dashboard);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(OAUTH_PENDING);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                // Optional: specify more APIs used with additional calls to addApi
                .useDefaultAccount()
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(Fitness.SCOPE_LOCATION_READ_WRITE)
                .addScope(Fitness.SCOPE_BODY_READ_WRITE)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = FastSave.getInstance().getString(
                                NotificationToken.Companion.getPREF_KEY_NAME(), null);
                        if (token != null) {
                            this.mFirestore.collection(NotificationToken.Companion.getCOLLECTION_NAME())
                                    .document(this.mCurrentUser.getUid())
                                    .set(new NotificationToken(token));
                        }
                    }
                });

        Bundle bundle = getIntent().getExtras();
        this.userInfo = new Gson().fromJson(bundle != null ?
                bundle.getString(UserInfo.COLLECTION_NAME) : null, UserInfo.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mGoogleApiClient,
                DataType.AGGREGATE_STEP_COUNT_DELTA);
        result.setResultCallback(dailyTotalResult -> {
            if (dailyTotalResult.getStatus().isSuccess()) {
                DataSet totalSet = dailyTotalResult.getTotal();
                if (totalSet != null && !totalSet.isEmpty()) {
                    this.stepCountTextView.setText(totalSet.getDataPoints().get(0)
                            .getValue(Field.FIELD_STEPS).toString());
                }
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection has been interrupted. Wait until onConnected() is called.
        Log.e("HistoryAPI", "onConnectionSuspended");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Log.e("FIT", "RESULT_CANCELED");
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
            }
        } else {
            Log.e("FIT", "request_oauth");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(OAUTH_PENDING, authInProgress);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.title_sign_out))
                .setMessage(getString(R.string.confirm_sign_out))
                .setNegativeButton(getString(R.string.option_cancel), null)
                .setPositiveButton(getString(R.string.option_sign_out), (arg0, arg1) -> {
                    mAuth.signOut();
                    startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
                }).create().show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Error while connecting. Try to resolve using the pending intent returned.
        if (!authInProgress) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult(this, REQUEST_OAUTH);
            } catch (IntentSender.SendIntentException e) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), SplashScreenActivity.class));
            }
        } else {
            Log.e("FIT", "authInProgress");
            Log.e("HistoryAPI", "onConnectionFailed");
        }
    }
}