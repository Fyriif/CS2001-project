package com.brunel.group30.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

public class SplashScreenActivity extends AppCompatActivity {

    final static int NEXT_ACTIVITY_DELAY = 750;

    FirebaseUser currentUser;
    FirebaseFirestore firebaseDatabase;

    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.firebaseDatabase = FirebaseFirestore.getInstance();

        if (this.currentUser != null) {
            isUserSetUp();
        } else {
            nextActivity(WelcomeActivity.class);
        }
    }

    void isUserSetUp() {
        DocumentReference docRef = this.firebaseDatabase.collection(
                UserInfo.COLLECTION_NAME)
                .document(this.currentUser.getUid());

        docRef.get().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentResult = task.getResult();
                if (documentResult != null && documentResult.exists()) {
                    userInfo = documentResult.toObject(UserInfo.class);
                    new Handler().postDelayed (() -> startActivity(
                            new Intent(getApplicationContext(), DashboardActivity.class)
                                    .putExtra(UserInfo.COLLECTION_NAME,
                                            new Gson().toJson(this.userInfo))),
                            NEXT_ACTIVITY_DELAY
                    );
                } else {
                    nextActivity(SettingUpActivity.class);
                }
            } else {
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_auth_failed),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    void nextActivity(Class activity) {
        new Handler().postDelayed (() -> startActivity(
                new Intent(getApplicationContext(), activity)), NEXT_ACTIVITY_DELAY
        );
    }
}