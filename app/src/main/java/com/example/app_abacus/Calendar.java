package com.example.app_abacus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.WindowManager;



import com.google.android.material.bottomnavigation.BottomNavigationView;



public class Calendar extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    static final String TAG = "Calendar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_calendar);

        initialize();


        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent home = new Intent(Calendar.this, MainActivity.class);
                        startActivity(home);
                        break;
                    case R.id.nav_calendar:
                        break;
                    case R.id.nav_statistics:
                        Intent statistics = new Intent(Calendar.this, Statistics.class);
                        startActivity(statistics);
                        break;
                }
                return false;
            }
        });
    }

    public void initialize() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
    }
}