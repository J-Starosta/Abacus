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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Register extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword, editTextConfirmPassword;
    private TextView textViewRegister;
    private String email, password, confirm_password, userID;
    public static final String EXTRA_USER_ID = "com.example.app_abacus.EXTRA_USER_ID";
    //Firebase
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        initialize();

        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = editTextEmail.getText().toString();
                password = editTextPassword.getText().toString();
                confirm_password = editTextConfirmPassword.getText().toString();

                checkRegistrationConditions();
            }
        });

    }

    private void checkRegistrationConditions() {
        if(email.equals(""))
            Toast.makeText(Register.this, "Wymagany adres email", Toast.LENGTH_SHORT).show();
        else if(password.equals(""))
            Toast.makeText(Register.this, "Wymagane hasło", Toast.LENGTH_SHORT).show();
        else if(confirm_password.equals(""))
            Toast.makeText(Register.this, "Potwierdź hasło", Toast.LENGTH_SHORT).show();
        else if(password.length() < 6)
            Toast.makeText(Register.this, "Hasło musi zawierać przynajmniej 6 znaków", Toast.LENGTH_SHORT).show();
        else if(!password.equals(confirm_password))
            Toast.makeText(Register.this, "Hasła niezgodne", Toast.LENGTH_SHORT).show();
        else
            RegisterUser(email, password);
    }

    private void RegisterUser(final String email, final String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    userID = firebaseAuth.getUid();
                    databaseReference = firebaseDatabase.getReference().child(userID);
                    databaseReference.child("email").setValue(email);
                    Toast.makeText(Register.this, "Zarejestrowano pomyślnie", Toast.LENGTH_SHORT).show();
                    Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                    mainActivity.putExtra(EXTRA_USER_ID, userID);
                    startActivity(mainActivity);
                }
                else
                    Toast.makeText(Register.this, "Błąd! " + Objects.requireNonNull(task.getException()).getMessage(),
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initialize() {
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        editTextConfirmPassword = (EditText) findViewById(R.id.editTextConfirmPassword);
        textViewRegister = (TextView) findViewById(R.id.textViewRegister);
        firebaseAuth = FirebaseAuth.getInstance();
    }

}