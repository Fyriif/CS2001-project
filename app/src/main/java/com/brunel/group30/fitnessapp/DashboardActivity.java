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
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class DashboardActivity extends AppCompatActivity
        implements OnDataPointListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_OAUTH = 1;
    private static final String OAUTH_PENDING = "auth_pending";
    private boolean authInProgress = false;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

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

        this.mAuth = FirebaseAuth.getInstance();

        dashboardViewFlipper = findViewById(R.id.view_dashboard);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(OAUTH_PENDING);
        }

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                // Optional: specify more APIs used with additional calls to addApi
                .useDefaultAccount()
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

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
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                .setDataSourceTypes(DataSource.TYPE_DERIVED)
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = result -> {
            for (DataSource data : result.getDataSources()) {
                if (DataType.TYPE_STEP_COUNT_CUMULATIVE.equals(data.getDataType())) {
                    onDataListener(data, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mGoogleApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }

    private void onDataListener(DataSource dataSource, DataType dataType) {

        SensorRequest fitness = new SensorRequest.Builder()
                .setDataSource(dataSource)
                .setDataType(dataType)
                .setSamplingRate(1, TimeUnit.SECONDS)
                .build();

        Fitness.SensorsApi.add(mGoogleApiClient, fitness, this)
                .setResultCallback(status -> {
                    if (status.isSuccess()) {
                        Log.e("GoogleFit", "SensorApi successfully added");
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection has been interrupted. Wait until onConnected() is called.
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
                startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
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
                    startActivity(new Intent(getApplicationContext(), WelcomeActivity.class));
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
                startActivity(new Intent(getApplicationContext(),WelcomeActivity.class));
            }
        } else {
            Log.e("FIT", "authInProgress");
        }
    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        for (final Field field : dataPoint.getDataType().getFields()) {
            final Value value = dataPoint.getValue(field);
            runOnUiThread(() -> Toast.makeText(getApplicationContext(),
                    "Field: " + field.getName() + " Value: " +
                            value, Toast.LENGTH_SHORT).show());
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        Fitness.SensorsApi.remove(mGoogleApiClient, this)
                .setResultCallback(status -> {
                    if (status.isSuccess()) {
                        mGoogleApiClient.disconnect();
                    }
                });
    }
}