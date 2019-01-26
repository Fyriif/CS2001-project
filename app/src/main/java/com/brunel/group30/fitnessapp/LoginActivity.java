package com.brunel.group30.fitnessapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z])(?=.*[@#$%^&+=]).{6,}$"
    );

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseDatabase;

    private EditText emailEditText;
    private EditText passwordEditText;

    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.loginProgressBar = findViewById(R.id.progress_bar_login);

        this.emailEditText = findViewById(R.id.edit_email);
        this.passwordEditText = findViewById(R.id.edit_password);

        TextView forgotPassTextView = findViewById(R.id.text_view_forgot_password);
        forgotPassTextView.setOnClickListener(this);

        findViewById(R.id.button_sign_up).setOnClickListener(this);
        findViewById(R.id.button_login).setOnClickListener(this);

        this.mAuth = FirebaseAuth.getInstance();
        this.mAuth.signOut();

        this.firebaseDatabase = FirebaseFirestore.getInstance();
    }

    void createAccount(String email, String password) {
        if (!validateForm()) {
            return;

        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        nextActivity(SettingUpActivity.class);
                    } else {
                        // TODO: user has failed to create account, what next?
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.error_auth_failed),
                                Toast.LENGTH_SHORT).show();
                    }

                });
    }

    void signIn(String email, String password) {
        if (!validateForm()) {
            return;
        }

        this.loginProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        isUserSetUp();
                    } else {
                        // TODO: user has failed to log in, what next?
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.error_auth_failed),
                                Toast.LENGTH_SHORT).show();
                    }

                    loginProgressBar.setVisibility(View.INVISIBLE);
                });
    }

    void forgotPassword(String email) {
        if (!email.isEmpty()) {
            this.mAuth.sendPasswordResetEmail(email);
            Toast.makeText(getApplicationContext(),
                    getString(R.string.info_password_reset_email_sent),
                    Toast.LENGTH_SHORT).show();
        }
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

    void isUserSetUp() {
        FirebaseUser user = this.mAuth.getCurrentUser();
        if (user != null) {
            DocumentReference docRef = this.firebaseDatabase.collection(
                    DBFields.USER_INFO_COLLECTION)
                    .document(user.getUid());

            docRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentResult = docRef.get().getResult();
                    nextActivity(documentResult != null && documentResult.exists()
                            ? DashboardActivity.class : SettingUpActivity.class);
                } else {
                    Toast.makeText(getApplicationContext(),
                            getString(R.string.error_auth_failed),
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    void nextActivity(Class nextActivity) {
        startActivity(new Intent(getApplicationContext(), nextActivity));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_sign_up:
                this.createAccount(this.emailEditText.getText().toString(), this.passwordEditText.getText().toString());
                break;
            case R.id.button_login:
                this.signIn(this.emailEditText.getText().toString(), this.passwordEditText.getText().toString());
                break;
            case R.id.text_view_forgot_password:
                forgotPassword(this.emailEditText.getText().toString());
                break;
        }
    }
}






