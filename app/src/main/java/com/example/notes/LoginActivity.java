package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    EditText logInEmailEditText, logInPasswordEditText;
    Button logInButton;
    ProgressBar progressBar;
    TextView signUpTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        logInEmailEditText = findViewById(R.id.log_in_email);
        logInPasswordEditText = findViewById(R.id.log_in_password);
        logInButton = findViewById(R.id.log_in_button);
        progressBar = findViewById(R.id.progress_bar);
        signUpTextView = findViewById(R.id.sign_up_text);

        logInButton.setOnClickListener(v->logIn());
        signUpTextView.setOnClickListener(v->startActivity(new Intent(LoginActivity.this, SignupActivity.class)));
    }

    void setProgressBar(boolean inProgress)
    {
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            logInButton.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            logInButton.setVisibility(View.VISIBLE);
        }
    }
    boolean validation(String email, String password){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            logInEmailEditText.setError("Invalid Email");
            return false;
        }
        if(password.length() < 5) {
            logInPasswordEditText.setError("Password length Invalid ");
            return false;
        }

        return true;
    }

    void logIn(){
        String email = logInEmailEditText.getText().toString();
        String password = logInPasswordEditText.getText().toString();

        boolean isValid = validation(email, password);

        if(!isValid){
            return;
        }

        logInAccountOnFirebase(email, password);
    }

    void logInAccountOnFirebase(String email,String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        setProgressBar(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setProgressBar(false);
                if(task.isSuccessful()){
                    //login is successfull
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        //go to mainactivity
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this, "Not Verified Email", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    //login failed
                    Toast.makeText(LoginActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}