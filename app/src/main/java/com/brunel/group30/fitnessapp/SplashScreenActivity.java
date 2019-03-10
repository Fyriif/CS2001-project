package com.brunel.group30.fitnessapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.appizona.yehiahd.fastsave.FastSave;
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
    private final static int NEXT_ACTIVITY_DELAY = 750;
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

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.web_client_id))
                        .requestEmail()
                        .requestScopes(new Scope(Scopes.FITNESS_LOCATION_READ))
                        .requestScopes(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                        .requestScopes(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        this.mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.user = this.mAuth.getCurrentUser();
        if (this.user == null) {
            startActivityForResult(new Intent(mGoogleSignInClient.getSignInIntent()), RC_SIGN_IN);
        } else {
            isUserSetUp();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions();
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
                    FastSave.getInstance().saveObject(user.getUid(), userInfo);
                    nextActivity(DashboardActivity.class);
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
                        isUserSetUp();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_auth_failed),
                            Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(getApplicationContext(),
                        getString(R.string.error_auth_failed),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    void nextActivity(Class activity) {
        new Handler().postDelayed (() -> startActivity(
                new Intent(getApplicationContext(), activity)
                        .putExtra(UserInfo.COLLECTION_NAME,
                                new Gson().toJson(this.userInfo))),
                NEXT_ACTIVITY_DELAY
        );
    }

    private void requestPermissions() {
        Permissions.check(this, Manifest.permission.ACCESS_FINE_LOCATION,
                null, new PermissionHandler() {
                    @Override
                    public void onGranted() { }

                    @Override
                    public void onDenied(Context context, ArrayList<String> deniedPermissions) {
                        finish();
                    }
                });
    }
}