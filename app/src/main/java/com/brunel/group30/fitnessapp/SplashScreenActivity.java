package com.brunel.group30.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.appizona.yehiahd.fastsave.FastSave;
import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.gson.Gson;

public class SplashScreenActivity extends AppCompatActivity {
    private final static int NEXT_ACTIVITY_DELAY = 750;
    private final static int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseDatabase;

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
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isUserSetUp();
    }

    void isUserSetUp() {
        FirebaseUser user = this.mAuth.getCurrentUser();
        if (user != null) {
            this.userInfo = FastSave.getInstance().getObject(user.getUid(), UserInfo.class);
            if (this.userInfo == null) {
                DocumentReference docRef = this.firebaseDatabase.collection(
                        UserInfo.COLLECTION_NAME)
                        .document(user.getUid());

                docRef.get().addOnCompleteListener(this, task -> {
                    if (task.isComplete() && task.isSuccessful()) {
                        DocumentSnapshot documentResult = task.getResult();
                        if (documentResult != null && documentResult.exists()) {
                            this.userInfo = documentResult.toObject(UserInfo.class);
                            FastSave.getInstance().saveObject(user.getUid(), this.userInfo);
                            nextActivity(DashboardActivity.class);
                        } else {
                            nextActivity(SettingUpActivity.class);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.error_auth_failed),
                                Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                nextActivity(DashboardActivity.class);
            }
        } else {
            mGoogleSignInClient.signOut();
            startActivityForResult(new Intent(mGoogleSignInClient.getSignInIntent()), RC_SIGN_IN);
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
}