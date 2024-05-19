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

public class SignupActivity extends AppCompatActivity {

    EditText signUpEmailEditText, signUpPasswordEditText, signUpPasswordConfirmEditText;
    Button signUpButton;
    ProgressBar progressBar;
    TextView signUpLogInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpEmailEditText = findViewById(R.id.sign_up_email);
        signUpPasswordEditText = findViewById(R.id.sign_up_password);
        signUpPasswordConfirmEditText = findViewById(R.id.sign_up_password_confirm);
        signUpButton = findViewById(R.id.sign_up_button);
        progressBar = findViewById(R.id.progress_bar);
        signUpLogInTextView = findViewById(R.id.sign_up_login_text);

        signUpButton.setOnClickListener(v->signUp());
        signUpLogInTextView.setOnClickListener(v->startActivity(new Intent(SignupActivity.this, LoginActivity.class)));
    }

    void signUp(){
        String email = signUpEmailEditText.getText().toString();
        String password = signUpPasswordEditText.getText().toString();
        String password_confirm = signUpPasswordConfirmEditText.getText().toString();

        boolean isValid = validation(email, password, password_confirm);

        if(!isValid){
            return;
        }

        createAccountOnFirebase(email, password);
    }

    boolean validation(String email, String password, String passwordConfirm){
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            signUpEmailEditText.setError("Invalid Email");
            return false;
        }
        if(password.length() < 5){
            signUpPasswordEditText.setError("Password length Invalid ");
            return false;
        }
        if(!password.equals(passwordConfirm)){
            signUpPasswordConfirmEditText.setError("Password Not Matched");
            return false;
        }

        return true;
    }

    void createAccountOnFirebase(String email,String  password){
        setProgressBar(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignupActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        setProgressBar(false);
                        if(task.isSuccessful()){
                            //creating acc is done
                            Toast.makeText(SignupActivity.this, "Successfully create account,Check email to verify", Toast.LENGTH_SHORT).show();
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }else{
                            //failure
                            Toast.makeText(SignupActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }
    void setProgressBar(boolean inProgress)
    {
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            signUpButton.setVisibility(View.VISIBLE);
        }
    }
}
