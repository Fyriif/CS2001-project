package com.brunel.group30.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.appizona.yehiahd.fastsave.FastSave;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        FastSave.init(getApplicationContext());
        FastSave.getInstance().saveBoolean("WELCOME_SCREEN_ALREADY_SHOWN", true);

        Button startBtn = findViewById(R.id.button_start);
        startBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(),
                LoginActivity.class)));
    }
}
