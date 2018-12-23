package com.brunel.group30.fitnessapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText emailEditText;
    private EditText passwordEditText;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.emailEditText = (EditText) findViewById(R.id.text_email);
        this.passwordEditText = (EditText) findViewById(R.id.text_password);

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

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // TODO: user has logged in, go to dashboard/set-up
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // TODO: user has failed to log in, what next?
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    boolean validateForm() {
        boolean valid = true;

        // TODO: more email validation, use RegEx to determine whether an email has '@' for example
        String email = this.emailEditText.getText().toString();
        if (email.isEmpty()) {
            this.emailEditText.setError("Required.");
            valid = false;
        } else {
            this.emailEditText.setError(null);
        }

        // TODO: more password validation, length, uppercase, symbol for example, consider using RegEx
        String password = this.passwordEditText.getText().toString();
        if (password.isEmpty()) {
            this.passwordEditText.setError("Required.");
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
            Toast.makeText(getApplicationContext(),
                    "User already logged in " + currentUser.getEmail(),
                    Toast.LENGTH_SHORT).show();
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
