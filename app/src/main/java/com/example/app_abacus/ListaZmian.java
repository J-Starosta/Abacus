package com.example.app_abacus;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ListaZmian extends AppCompatActivity {
    String monthSelected = "", yearSelected = "", userID = "";
    ImageView buttonBack, buttonDelete;
    private ListView listView;
    public static final String EXTRA_USER_ID = "com.example.app_abacus.EXTRA_USER_ID";
    TextView textViewLiczbaZmian;
    Integer liczbaZmian = 0;
    //Firebase
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private FirebaseListAdapter<user> adapter;
    private DatabaseReference databaseReference;
    Intent statistics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_lista_zmian);

        Intent intent = getIntent();
        monthSelected = intent.getStringExtra(Statistics.EXTRA_MONTH_SELECTED);
        yearSelected = intent.getStringExtra(Statistics.EXTRA_YEAR_SELECTED);
        userID = intent.getStringExtra(Statistics.EXTRA_USER_ID);

        initialize();

        statistics = new Intent(this, Statistics.class);

        databaseReference = firebaseDatabase.getReference().child(userID).child("Zmiany").child(yearSelected).child(monthSelected);
        Query query = databaseReference;
        FirebaseListOptions<user> options = new FirebaseListOptions.Builder<user>()
                .setLayout(R.layout.single_view_layout)
                .setQuery(query, user.class)
                .build();

        adapter = new FirebaseListAdapter<user>(options) {
            @SuppressLint("SetTextI18n")
            @Override
            protected void populateView(@NonNull View v, @NonNull user model, int position) {
                final DatabaseReference itemRef = getRef(position);
                TextView textViewSelectedData = v.findViewById(R.id.textViewDate);
                TextView valueCzasPracy = v.findViewById(R.id.valueCzasPracy);
                TextView valueNapiwki = v.findViewById(R.id.valueNapiwki);
                TextView valueSerwis = v.findViewById(R.id.valueSerwis);
                TextView valueStawka = v.findViewById(R.id.valueStawka);
                ImageView buttonDelete = v.findViewById(R.id.buttonDelete);
                String day = itemRef.getKey();

                user userData = (user) model;
                liczbaZmian++;
                valueCzasPracy.setText("Czas pracy: " + userData.getCzas_pracy() + " h");
                valueNapiwki.setText("Napiwki: " + userData.getNapiwki() + " zł");
                valueSerwis.setText("Serwis: " + userData.getSerwis()+ " zł");
                valueStawka.setText("Stawka: " + userData.getStawka()+ " zł");
                textViewSelectedData.setText(yearSelected + " " + monthSelected + " " + day);

                buttonDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemRef.setValue(null);
                    }
                });
            }
        } ;

        listView.setAdapter(adapter);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statistics.putExtra(EXTRA_USER_ID, userID);
                startActivity(statistics);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void initialize() {
        buttonBack = findViewById(R.id.buttonBack);
        //textViewLiczbaZmian = findViewById(R.id.textViewLiczbaZmian);
        listView = findViewById(R.id.listView);
    }
}