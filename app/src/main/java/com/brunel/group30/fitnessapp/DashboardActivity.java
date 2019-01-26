package com.brunel.group30.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class DashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        this.mAuth = FirebaseAuth.getInstance();
        if (this.mAuth.getCurrentUser() == null) {
            nextActivity(LoginActivity.class);
        }
    }

    void nextActivity(Class nextActivity) {
        startActivity(new Intent(getApplicationContext(), nextActivity));
    }
}
