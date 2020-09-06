package com.example.app_abacus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements DialogMainActivity.DialogMainActivityListener{

    private BottomNavigationView bottomNavigationView;
    private Button buttonDodajZmiane;
    private ImageView buttonLogout;
    private TextView textViewCzasPracy, textViewZmiany, textViewNapiwki, textViewSerwis, textViewSrednio, textViewWyplata;
    private Object Tag;
    private Double napiwki = 0.0, serwis = 0.0, czasPracy = 0.0, srednio = 0.0, wyplata = 0.0, stawka = 0.0, serwisSuma = 0.0, napiwkiSuma = 0.0, czasPracySuma = 0.0;
    private Integer zmiany = 0;
    private String userID;
    private String[] dayKey = new String[31];
    public static final String EXTRA_USER_ID = "com.example.app_abacus.EXTRA_USER_ID";
    //Firebase
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference, mReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Arrays.fill(dayKey, "0");
        initialize();

        Date currentDate = Calendar.getInstance().getTime();
        String currentMonth = DateFormat.getDateInstance(DateFormat.SHORT).format(currentDate);
        Log.d((String) Tag, "Obecna data:   " + currentMonth);

        Intent intent = getIntent();
        userID = intent.getStringExtra(Register.EXTRA_USER_ID);
        userID = intent.getStringExtra(Login.EXTRA_USER_ID);
        userID = intent.getStringExtra(Statistics.EXTRA_USER_ID);

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent login = new Intent(MainActivity.this, Login.class);
                startActivity(login);
            }
        });

        databaseReference = firebaseDatabase.getReference(userID);
        buttonDodajZmiane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDialog();
            }
        });
        updatingCurrentMonth();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        break;
                    case R.id.nav_calendar:
                        Toast.makeText(MainActivity.this, "Może dodam", Toast.LENGTH_SHORT).show();
                        //Intent calendar = new Intent(MainActivity.this, Calendar.class);
                        //startActivity(calendar);
                        break;
                    case R.id.nav_statistics:
                        Intent statistics = new Intent(MainActivity.this, Statistics.class);
                        statistics.putExtra(EXTRA_USER_ID, userID);
                        startActivity(statistics);
                        break;
                }
                return false;
            }
        });

    }

    @SuppressLint("SetTextI18n")
    private void updatingCurrentMonth() {
        databaseReference = firebaseDatabase.getReference(userID).child("Zmiany").child(getYear()).child(getMonth());
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

                            textViewCzasPracy.setText(czasPracyString);
                            textViewNapiwki.setText(napiwkiString);
                            textViewSerwis.setText(serwisString);
                            textViewSrednio.setText(srednioString);
                            textViewWyplata.setText(wyplataString);
                            textViewZmiany.setText(zmianyString);
                        }

                    }
                }
                else{
                    textViewCzasPracy.setText("0 h");
                    textViewNapiwki.setText("0 zł");
                    textViewSerwis.setText("0 zł");
                    textViewSrednio.setText("0 zł/h");
                    textViewWyplata.setText("0 zł");
                    textViewZmiany.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getMonth() {
        String month;
        Date currentDate = Calendar.getInstance().getTime();
        String currentMonth = DateFormat.getDateInstance(DateFormat.SHORT).format(currentDate);
        Log.d((String) Tag, "Obecna data:   " + currentDate);
        String[] strings = currentMonth.split("\\.");
        month = resolveMonthNameFromNumber(strings[1]);
        Log.d((String) Tag, " Strings []   " + month);
        return month;
    }

    private String getYear() {
        String year;
        Date currentDate = Calendar.getInstance().getTime();
        String currentMonth = DateFormat.getDateInstance(DateFormat.SHORT).format(currentDate);
        Log.d((String) Tag, "Obecna data:   " + currentMonth);
        String[] strings = currentMonth.split("\\.");
        year = strings[2];
        Log.d((String) Tag, " Strings []   " + year);
        return year;
    }

    private String getDay() {
        String day;
        Date currentDate = Calendar.getInstance().getTime();
        String currentMonth = DateFormat.getDateInstance(DateFormat.SHORT).format(currentDate);
        String[] strings = currentMonth.split(getString(R.string.spliter));
        day = strings[0];
        return day;
    }



    private void openDialog() {
        DialogMainActivity dialogMainActivity = new DialogMainActivity();

        dialogMainActivity.show(getSupportFragmentManager(), "main activity dialog");
    }

    public void initialize() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        buttonDodajZmiane = (Button) findViewById(R.id.button_dodaj_zmiane);
        textViewCzasPracy = (TextView) findViewById(R.id.czas_pracy_value);
        textViewNapiwki = (TextView) findViewById(R.id.napiwki_value);
        textViewSerwis = (TextView) findViewById(R.id.serwis_value);
        textViewSrednio = (TextView) findViewById(R.id.srednio_value);
        textViewWyplata = (TextView) findViewById(R.id.wyplata_value);
        textViewZmiany = (TextView) findViewById(R.id.zmiany_value);
        buttonLogout = (ImageView) findViewById(R.id.buttonLogout);
    }


    public String resolveMonthNameFromNumber(String month) {
        switch (month) {
            case "1":
            case "01":
                return "Styczeń";
            case "2":
            case "02":
                return "Luty";
            case "3":
            case "03":
                return "Marzec";
            case "4":
            case "04":
                return "Kwiecień";
            case "5":
            case "05":
                return "Maj";
            case "6":
            case "06":
                return "Czerwiec";
            case "7":
            case "07":
                return "Lipiec";
            case "8":
            case "08":
                return "Sierpień";
            case "9":
            case "09":
                return "Wrzesień";
            case "10":
                return "Październik";
            case "11":
                return "Listopad";
            case "12":
                return "Grudzień";
            default:
                return "error";
        }
    }

    @Override
    public void applyText(String day, String month, String year, String czas_pracy, String serwis, String napiwki, String stawka) {
        mReference = firebaseDatabase.getReference(userID);
        mReference.child("Zmiany").child(year).child(resolveMonthNameFromNumber(month)).child(day).child("Czas_pracy").setValue(czas_pracy);
        mReference.child("Zmiany").child(year).child(resolveMonthNameFromNumber(month)).child(day).child("Serwis").setValue(serwis);
        mReference.child("Zmiany").child(year).child(resolveMonthNameFromNumber(month)).child(day).child("Napiwki").setValue(napiwki);
        mReference.child("Zmiany").child(year).child(resolveMonthNameFromNumber(month)).child(day).child("Stawka").setValue(stawka);
    }
}
