package com.example.app_abacus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private TextView textViewLogin, textViewToRegister;

    private String email, password, userID;
    public static final String EXTRA_USER_ID = "com.example.app_abacus.EXTRA_USER_ID";
    //Firebase
    private FirebaseAuth firebaseAuth;

    /**/


    public Login() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        initialize();

        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();

                checkLoginConditions();
            }
        });

        textViewToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent register = new Intent(getApplicationContext(), Register.class);
                startActivity(register);
            }
        });

    }

    private void checkLoginConditions() {
        if(email.equals(""))
            Toast.makeText(Login.this, "Wymagany adres email", Toast.LENGTH_SHORT).show();
        else if(password.equals(""))
            Toast.makeText(Login.this, "Wymagane hasło", Toast.LENGTH_SHORT).show();
        else
            authorization(email, password);
    }

    private void authorization(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    userID = firebaseAuth.getUid();
                    Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    mainActivity.putExtra(EXTRA_USER_ID, userID);
                    startActivity(mainActivity);
                }
                else
                    Toast.makeText(Login.this, "Błąd! " + Objects.requireNonNull(task.getException()).getMessage(),
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialize() {
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        textViewLogin = (TextView) findViewById(R.id.textViewLogin);
        textViewToRegister = (TextView) findViewById(R.id.textViewToRegister);
        firebaseAuth = FirebaseAuth.getInstance();

    }
}