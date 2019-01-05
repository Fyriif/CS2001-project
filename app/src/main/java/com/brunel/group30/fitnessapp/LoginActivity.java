package com.brunel.group30.fitnessapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^" +
            "(?=.*[0-9])" +         //minimum 1 digit
            //"(?=.*[a-z])" +         // 1 lower case letter
            "(?=.*[A-Z])" +         //1 upper case letter
            "(?=.*[a-zA-Z])" +      //any letter
            "(?=.*[@#$%^&+=])" +    //1 special character
            "(?=\\S+$)" +           //no white spaces
            ".{6,}" +               //6 characters minimum
            "$");

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private EditText emailEditText;
    private EditText passwordEditText;

    private Button signUpButton;
    private Button loginButton;

    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.loginProgressBar = (ProgressBar) findViewById(R.id.progress_bar_login);

        this.emailEditText = (EditText) findViewById(R.id.text_email);
        this.passwordEditText = (EditText) findViewById(R.id.text_password);

        this.signUpButton = (Button) findViewById(R.id.btn_signUp);
        this.loginButton = (Button) findViewById(R.id.btn_login);

        findViewById(R.id.btn_signUp).setOnClickListener(this);
        findViewById(R.id.btn_login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
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
                            // TODO: user has created an account, go to dashboard/set-up
                            FirebaseUser user = mAuth.getCurrentUser();
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
                            FirebaseUser user = mAuth.getCurrentUser();

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
            this.emailEditText.setError("Field can't be empty");
            valid = false;

        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            this.emailEditText.setError("Please enter a valid email address");
            valid = false;
        } else {
            this.emailEditText.setError(null);
        }

        String password = this.passwordEditText.getText().toString();
        if (password.isEmpty()) {
            this.passwordEditText.setError("Field can't be empty");
            valid = false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            this.passwordEditText.setError("Password is weak");
            valid = false;
        } else {
            this.passwordEditText.setError(null);
        }

        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.currentUser = mAuth.getCurrentUser();
        if (this.currentUser != null) {
            // TODO: go to dashboard or set-up screen
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
            case R.id.btn_signUp:
                this.createAccount(this.emailEditText.getText().toString(), this.passwordEditText.getText().toString());
            case R.id.btn_login:
                this.signIn(this.emailEditText.getText().toString(), this.passwordEditText.getText().toString());
        }
    }
}






