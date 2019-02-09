package com.brunel.group30.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.brunel.group30.fitnessapp.Models.UserInfo;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class WelcomeActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.web_client_id))
                        .requestEmail()
                        .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseFirestore.getInstance();

        Button startBtn = findViewById(R.id.button_start);
        startBtn.setOnClickListener(view -> isUserSetUp());
    }


    @Override
    protected void onStart() {
        super.onStart();
        if (this.mAuth.getCurrentUser() == null) {
            startActivityForResult(new Intent(mGoogleSignInClient.getSignInIntent()), RC_SIGN_IN);
        }
    }

    void isUserSetUp() {
        FirebaseUser user = this.mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = this.firebaseDatabase.collection(
                    UserInfo.COLLECTION_NAME)
                    .document(user.getUid());

            docRef.get().addOnCompleteListener(this, task -> {
                if (task.isComplete() && task.isSuccessful()) {
                    DocumentSnapshot documentResult = task.getResult();
                    nextActivity(documentResult != null && documentResult.exists()
                            ? DashboardActivity.class : SettingUpActivity.class);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_auth_failed),
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            startActivityForResult(new Intent(mGoogleSignInClient.getSignInIntent()), RC_SIGN_IN);
        }
    }

    void nextActivity(Class nextActivity) {
        startActivity(new Intent(getApplicationContext(), nextActivity));
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.error_auth_failed),
                                Toast.LENGTH_SHORT).show();
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
}
