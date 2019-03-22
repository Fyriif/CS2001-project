package com.brunel.group30.fitnessapp;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.brunel.group30.fitnessapp.Services.BarcodeInfoGetterService;
import com.brunel.group30.fitnessapp.Services.GoogleFitApi;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class BarcodeScannerActivity extends AppCompatActivity implements ZBarScannerView.ResultHandler {

    ZBarScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions();

        mScannerView = new ZBarScannerView(this);
        mScannerView.setResultHandler(this);
        setContentView(mScannerView);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        //TODO: add validation for whether barcode is correct
        Toast.makeText(this, rawResult.getContents(), Toast.LENGTH_LONG).show();
        try {
            GoogleFitApi.sendNutritionalData(new BarcodeInfoGetterService().execute(rawResult.getContents()).get());
            mScannerView.stopCamera();
            finish();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void requestPermissions() {
        Permissions.check(this, Manifest.permission.CAMERA,
                null, new PermissionHandler() {
                    @Override
                    public void onGranted() { }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        Toast.makeText(getApplicationContext(), "Permission denied, please enable this in your phone settings or via the app prompt request", Toast.LENGTH_LONG).show();
                        finish();
                    }
                });
    }
}
