package com.brunel.group30.fitnessapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.appizona.yehiahd.fastsave.FastSave;
import com.brunel.group30.fitnessapp.Models.Goals;
import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.brunel.group30.fitnessapp.Services.CustomFirebaseFirestoreService;
import com.brunel.group30.fitnessapp.Utils.Exceptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;
import com.nabinbhandari.android.permissions.PermissionHandler;
import com.nabinbhandari.android.permissions.Permissions;

import java.util.ArrayList;

public class SplashScreenActivity extends AppCompatActivity {
    private final static int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    UserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        FastSave.init(getApplicationContext());
        this.mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.web_client_id))
                        .requestEmail()
                        .requestScopes(new Scope(Scopes.FITNESS_LOCATION_READ))
                        .requestScopes(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                        .requestScopes(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        startActivityForResult(new Intent(mGoogleSignInClient.getSignInIntent()), RC_SIGN_IN);
    }

    void isUserSetUp() {
        this.userInfo = FastSave.getInstance().getObject(user.getUid(), UserInfo.class);
        if (this.userInfo == null) {
            Task<DocumentSnapshot> userDocTask = CustomFirebaseFirestoreService.INSTANCE.getDocument(
                    UserInfo.COLLECTION_NAME,
                    user.getUid()
            );

            userDocTask.addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    userInfo = documentSnapshot.toObject(UserInfo.class);
                    getGoals();
                } else {
                    nextActivity(SettingUpActivity.class);
                }
            });

            userDocTask.addOnFailureListener(e -> {
                Exceptions.FirestoreExceptions.errorFailedToGetDocument(
                        UserInfo.COLLECTION_NAME,
                        mAuth.getUid()
                );
            });
        } else {
            nextActivity(DashboardActivity.class);
        }
    }

    void getGoals() {
        Task<DocumentSnapshot> goalsDocTask = CustomFirebaseFirestoreService.INSTANCE.getDocument(
                Goals.COLLECTION_NAME,
                user.getUid()
        );

        goalsDocTask.addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                userInfo.setGoals(documentSnapshot.toObject(Goals.class));
                nextActivity(DashboardActivity.class);
            } else {
                nextActivity(SettingUpActivity.class);
            }
        });

    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.error_auth_failed),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        this.user = this.mAuth.getCurrentUser();
                        requestPermissions();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignResult(task);
        }
    }

    void handleSignResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account);
            }

        } catch (ApiException e) {
            Log.e("GoogleSignIn", e.getMessage());
            Toast.makeText(getApplicationContext(),
                    getString(R.string.error_auth_failed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    void nextActivity(Class activity) {
        startActivity(new Intent(getApplicationContext(), activity)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .putExtra(UserInfo.COLLECTION_NAME, new Gson().toJson(this.userInfo)));
    }

    private void requestPermissions() {
        Permissions.check(this, Manifest.permission.ACCESS_FINE_LOCATION,
                null, new PermissionHandler() {
                    @Override
                    public void onGranted() {
                        user = mAuth.getCurrentUser();
                        if (user == null) {
                            onResume();
                        } else {
                            isUserSetUp();
                        }
                    }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        finish();
                    }
                });
    }
}