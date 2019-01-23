package com.brunel.group30.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[@#$%^&+=]).{6,}$");

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore firebaseDatabase;

    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.loginProgressBar = (ProgressBar) findViewById(R.id.progress_bar_login);

        this.emailEditText = (EditText) findViewById(R.id.edit_email);
        this.passwordEditText = (EditText) findViewById(R.id.edit_password);
      
        findViewById(R.id.button_sign_up).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);

        this.mAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseFirestore.getInstance();
    }

    void createAccount(String email, String password) {
        if (!validateForm()) {
            return;

        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            nextActivity(SettingUpActivity.class);
                        } else {
                            // TODO: user has failed to create account, what next?
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        this.loginProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // TODO: user has logged in, go to dashboard/set-up
                            currentUser = mAuth.getCurrentUser();
                            DocumentReference docRef = firebaseDatabase.collection("user-info").document(currentUser.getUid());
                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        DocumentSnapshot document = task.getResult();
                                        if (document.exists()) {
                                            Toast.makeText(getApplicationContext(), "No need to set-up, go to dashboard", Toast.LENGTH_LONG).show();
                                        } else {
                                            nextActivity(SettingUpActivity.class);
                                        }
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            // TODO: user has failed to log in, what next?
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(getApplicationContext(), "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }

                        loginProgressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }


    boolean validateForm() {
        boolean valid = true;

        String email = this.emailEditText.getText().toString();
        if (email.isEmpty()) {
            this.emailEditText.setError(getString(R.string.error_field_empty));
            valid = false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.emailEditText.setError(getString(R.string.error_enter_valid_email));
            valid = false;
        } else {
            this.emailEditText.setError(null);
        }

        String password = this.passwordEditText.getText().toString();
        if (password.isEmpty()) {
            this.passwordEditText.setError(getString(R.string.error_field_empty));
            valid = false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            this.passwordEditText.setError(getString(R.string.error_password_weak));
            valid = false;
        } else {
            this.passwordEditText.setError(null);
        }

        return valid;
    }

    void nextActivity(Class nextActivity) {
        startActivity(new Intent(getApplicationContext(), nextActivity));
    }

    @Override
    public void onStart() {
        super.onStart();
        this.currentUser = mAuth.getCurrentUser();
        if (this.currentUser != null) {
            DocumentReference docRef = this.firebaseDatabase.collection("user-info").document(this.currentUser.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            // TODO: go to dashboard, once implemented
                            Toast.makeText(getApplicationContext(), "No need to set-up, go to dashboard", Toast.LENGTH_LONG).show();
                        } else {
                            nextActivity(SettingUpActivity.class);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
>>>>>>> origin/master
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_up:
                this.createAccount(this.emailEditText.getText().toString(), this.passwordEditText.getText().toString());
            case R.id.button_login:
                this.signIn(this.emailEditText.getText().toString(), this.passwordEditText.getText().toString());
        }
    }
}






