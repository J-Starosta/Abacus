package com.example.app_abacus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Statistics extends AppCompatActivity {

    private boolean checkMonth = true, checkYear = true;
    private ImageView buttonLogout;
    private BottomNavigationView bottomNavigationView;
    private Button buttonListaZmian;
    private Object Tag;
    private Spinner spinnerMonths, spinnerYears;
    private TextView valueSrednio, valueSerwis, valueWyplata, valueZmiany, valueCzasPracy, valueNapiwki;
    public static final String EXTRA_USER_ID = "com.example.app_abacus.EXTRA_USER_ID";
    public static final String EXTRA_MONTH_SELECTED = "com.example.app_abacus.EXTRA_MONTH_SELECTED";
    public static final String EXTRA_YEAR_SELECTED = "com.example.app_abacus.EXTRA_YEAR_SELECTED";
    private Double napiwki = 0.0, serwis = 0.0, czasPracy = 0.0, srednio = 0.0, wyplata = 0.0, stawka = 0.0, serwisSuma = 0.0, napiwkiSuma = 0.0, czasPracySuma = 0.0;
    private Integer zmiany = 0;
    private ArrayAdapter<CharSequence> monthAdapter, yearAdapter;

    private String userID;
    private String yearSelected = "";
    private String monthSelected = "";
    private Intent listaZmian, listaZmianYears;
    //Firebase
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference;

    public Statistics() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_statistics);

        Intent intent = getIntent();
        userID = intent.getStringExtra(MainActivity.EXTRA_USER_ID);
        initialize();
        listaZmian = new Intent(this, ListaZmian.class);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent login = new Intent(Statistics.this, Login.class);
                startActivity(login);
            }
        });

        yearAdapter = ArrayAdapter.createFromResource(this, R.array.years, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYears.setAdapter(yearAdapter);

        if(checkYear){
            spinnerYears.setSelection(0);
            checkYear = false;
        }

        spinnerYears.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                yearSelected = spinnerYears.getSelectedItem().toString();
                updatingMonth();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        monthAdapter = ArrayAdapter.createFromResource(this, R.array.months, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonths.setAdapter(monthAdapter);

        if(checkMonth){
            spinnerMonths.setSelection(0);
            checkMonth = false;
        }

        spinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                monthSelected = spinnerMonths.getSelectedItem().toString();
                updatingMonth();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        Log.d((String) Tag, "Wartość yearSelected w mainie:      " +yearSelected);
        buttonListaZmian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaZmian = new Intent(getApplicationContext(), ListaZmian.class);
                listaZmian.putExtra(EXTRA_USER_ID, userID);
                listaZmian.putExtra(EXTRA_YEAR_SELECTED, yearSelected);
                listaZmian.putExtra(EXTRA_MONTH_SELECTED, monthSelected);
                startActivity(listaZmian);
            }
        });
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent home = new Intent(Statistics.this, MainActivity.class);
                        home.putExtra(EXTRA_USER_ID, userID);
                        startActivity(home);
                        break;
                    case R.id.nav_calendar:
                        Toast.makeText(Statistics.this, "Może dodam", Toast.LENGTH_SHORT).show();
                        //Intent calendar = new Intent(Statistics.this, Calendar.class);
                        //startActivity(calendar);
                        break;
                    case R.id.nav_statistics:
                        break;
                }
                return false;
            }
        });
    }

    private void updatingMonth() {
        databaseReference = firebaseDatabase.getReference(userID).child("Zmiany").child(yearSelected).child(monthSelected);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<user> arrayList = new ArrayList<>();
                if(snapshot.exists()){
                    napiwki = serwis = czasPracy = srednio = wyplata = stawka = 0.0;
                    zmiany = 0;
                    for(DataSnapshot ds : snapshot.getChildren()){
                        if(ds.child("Serwis").getValue() != null && ds.child("Napiwki").getValue() != null
                                && ds.child("Czas_pracy").getValue() != null && ds.child("Stawka").getValue() != null){
                            user userData = ds.getValue(user.class);
                            arrayList.add(userData);
                            napiwki = Double.parseDouble(String.valueOf(userData.getNapiwki()));
                            serwis = Double.parseDouble(String.valueOf(userData.getSerwis()));
                            czasPracy = Double.parseDouble(String.valueOf(userData.getCzas_pracy()));
                            stawka = Double.parseDouble(String.valueOf(userData.getStawka()));
                            napiwkiSuma += napiwki;
                            serwisSuma += serwis;
                            czasPracySuma += czasPracy;
                            zmiany++;
                            wyplata += (czasPracy * stawka) + (serwis * 0.5) + napiwki;
                            srednio = wyplata / czasPracySuma;


                            String czasPracyString, napiwkiString, serwisString, srednioString, wyplataString, zmianyString;
                            czasPracyString = czasPracySuma.toString() + " h";
                            napiwkiString = napiwkiSuma.toString() + " zł";
                            serwisString = serwisSuma.toString() + " zł";
                            srednioString = String.format("%.2f", srednio) + " zł/h";
                            wyplataString = String.format("%.2f", wyplata) + " zł";
                            zmianyString = zmiany.toString();

                            valueCzasPracy.setText(czasPracyString);
                            valueNapiwki.setText(napiwkiString);
                            valueSerwis.setText(serwisString);
                            valueSrednio.setText(srednioString);
                            valueWyplata.setText(wyplataString);
                            valueZmiany.setText(zmianyString);
                        }

                    }
                }
                else{
                    valueCzasPracy.setText("0 h");
                    valueNapiwki.setText("0 zł");
                    valueSerwis.setText("0 zł");
                    valueSrednio.setText("0 zł/h");
                    valueWyplata.setText("0 zł");
                    valueZmiany.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void initialize() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        spinnerMonths = findViewById(R.id.spinner_months);
        spinnerYears = findViewById(R.id.spinner_years);
        buttonListaZmian = findViewById(R.id.buttonListaZmian);
        valueCzasPracy = findViewById(R.id.valueCzasPracy);
        valueNapiwki = findViewById(R.id.valueNapiwki);
        valueSerwis = findViewById(R.id.valueSerwis);
        valueSrednio = findViewById(R.id.valueSrednio);
        valueWyplata = findViewById(R.id.valueWyplata);
        valueZmiany = findViewById(R.id.valueZmiany);
        buttonLogout = findViewById(R.id.buttonLogout);
    }
}