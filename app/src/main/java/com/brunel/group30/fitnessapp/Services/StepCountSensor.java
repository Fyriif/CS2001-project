package com.brunel.group30.fitnessapp.Services;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.brunel.group30.fitnessapp.Utils.Utils;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class StepCountSensor extends GoogleFitApi {
    private OnDataPointListener mListener;
    private static final String TAG = "StepSensorsApi";
    private TextView stepCountTextView;

    /**
     * Finds available data sources and attempts to register on a specific {@link DataType}.
     */
    public StepCountSensor(Activity activity, TextView stepCountTextView) throws Exception {
        super(activity);

        this.stepCountTextView = stepCountTextView;

        if (Utils.INSTANCE.isEmulator()) {
            this.getDailyStepCount();
        }  else {
            Fitness.getSensorsClient(activity, mGoogleSignInAccount)
                    .findDataSources(
                            new DataSourcesRequest.Builder()
                                    .setDataTypes(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                                    .setDataSourceTypes(DataSource.TYPE_RAW)
                                    .build())
                    .addOnSuccessListener(
                            dataSources -> {
                                for (DataSource dataSource : dataSources) {
                                    Log.i(TAG, "Data source found: " + dataSource.toString());
                                    Log.i(TAG, "Data Source type: " + dataSource.getDataType().getName());

                                    if (dataSource.getDataType().equals(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                                            && mListener == null) {
                                        registerFitnessDataListener(dataSource,
                                                DataType.TYPE_STEP_COUNT_CUMULATIVE);
                                    }
                                }
                            })
                    .addOnFailureListener(
                            e -> Log.e(TAG, "failed", e));
        }
    }

    /**
     * Registers a listener with the Sensors API for the provided {@link DataSource}
     * and {@link DataType} combo.
     */
    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        mListener =
                dataPoint -> {
                    for (Field field : dataPoint.getDataType().getFields()) {
                        Value val = dataPoint.getValue(field);
                        Log.i(TAG, "Detected DataPoint field: " + field.getName());
                        Log.i(TAG, "Detected DataPoint value: " + val);

                        if (dataPoint.getDataType().equals(DataType.TYPE_STEP_COUNT_CUMULATIVE)) {
                            this.getDailyStepCount();
                        }
                    }
                };

        Fitness.getSensorsClient(activity, mGoogleSignInAccount)
                .add(
                        new SensorRequest.Builder()
                                .setDataSource(dataSource)
                                .setDataType(dataType)
                                .setSamplingRate(1, TimeUnit.SECONDS)
                                .build(),
                        mListener)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "Listener registered!");
                            } else {
                                Log.e(TAG, "Listener not registered.",
                                        task.getException());
                            }
                        });
    }

    public void unregisterFitnessDataListener() {
        if (mListener == null) {
            return;
        }

        Fitness.getSensorsClient(super.activity, super.mGoogleSignInAccount)
                .remove(mListener)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.i(TAG, "Listener was removed!");
                            } else {
                                Log.i(TAG, "Listener was not removed.");
                            }
                        });
    }

    private void getDailyStepCount() {
        Fitness.getHistoryClient(super.activity, super.mGoogleSignInAccount)
                .readDailyTotal(DataType.AGGREGATE_STEP_COUNT_DELTA)
                .addOnCompleteListener(task -> {
                    if (task.isComplete() && task.isSuccessful()) {
                        List<DataPoint> results = task.getResult().getDataPoints();
                        if (results.size() > 0) {
                            DataPoint dataPoint = results.get(0);
                            if (Utils.INSTANCE.isDateToday(dataPoint.getTimestamp(TimeUnit.MILLISECONDS))) {
                                this.stepCountTextView.setText(String.valueOf(
                                        dataPoint.getValue(Field.FIELD_STEPS).asInt()));
                            } else {
                                this.stepCountTextView.setText("0");
                            }
                        } else {
                            this.stepCountTextView.setText("0");
                        }
                    }
                });
    }

    @Override
    public void subscribe() {
        if (mGoogleSignInAccount != null && !Utils.INSTANCE.isEmulator()) {
            Fitness.getRecordingClient(activity, mGoogleSignInAccount)
                    .subscribe(DataType.TYPE_STEP_COUNT_DELTA)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed! "
                                            + DataType.TYPE_STEP_COUNT_DELTA);
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.",
                                            task.getException());
                                }
                            });

            Fitness.getRecordingClient(activity, mGoogleSignInAccount)
                    .subscribe(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                    .addOnCompleteListener(
                            task -> {
                                if (task.isSuccessful()) {
                                    Log.i(TAG, "Successfully subscribed! "
                                            + DataType.TYPE_STEP_COUNT_CUMULATIVE);
                                } else {
                                    Log.w(TAG, "There was a problem subscribing.",
                                            task.getException());
                                }
                            });
        }
    }
}
