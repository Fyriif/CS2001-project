package com.brunel.group30.fitnessapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.appizona.yehiahd.fastsave.FastSave;

public class SplashScreenActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 1000;
    public final String WELCOME_SCREEN_SHOWN_KEY = "WELCOME_SCREEN_ALREADY_SHOWN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FastSave.init(getApplicationContext());
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                startActivity(new Intent(getApplicationContext(), FastSave.getInstance().isKeyExists(WELCOME_SCREEN_SHOWN_KEY) ? LoginActivity.class : WelcomeActivity.class));
                FastSave.getInstance().saveBoolean("WELCOME_SCREEN_ALREADY_SHOWN", true);
            }
        }, this.SPLASH_DISPLAY_LENGTH);
    }
}
