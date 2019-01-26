package com.brunel.group30.fitnessapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appizona.yehiahd.fastsave.FastSave;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreenActivity extends AppCompatActivity {

    final String WELCOME_SCREEN_SHOWN_KEY = "WELCOME_SCREEN_ALREADY_SHOWN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FastSave.init(getApplicationContext());
        setContentView(R.layout.activity_splash_screen);

        int SPLASH_DISPLAY_LENGTH = 1000;
        new Handler().postDelayed (() -> startActivity(new Intent(getApplicationContext(),
                FastSave.getInstance().getBoolean(WELCOME_SCREEN_SHOWN_KEY, false)
                        ? (FirebaseAuth.getInstance().getCurrentUser() != null
                        ? DashboardActivity.class : LoginActivity.class)
                        : WelcomeActivity.class)), SPLASH_DISPLAY_LENGTH);
    }
}