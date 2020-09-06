package com.example.app_abacus;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import java.util.Objects;

public class DialogMainActivity extends AppCompatDialogFragment {

    private EditText editTextCzasPracy, editTextSerwis, editTextNapiwki, editTextMonth, editTextDay, editTextYear, editTextStawka;
    private DialogMainActivityListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();

        View view = inflater.inflate(R.layout.dodaj_zmiane_dialog, null);

        builder.setView(view)
                .setTitle("Dodaj zmianę")
                .setNegativeButton("Anuluj", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton("Zatwierdź", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String day = editTextDay.getText().toString();
                        String month = editTextMonth.getText().toString();
                        String year = editTextYear.getText().toString();
                        String czas_pracy = editTextCzasPracy.getText().toString();
                        String napiwki = editTextNapiwki.getText().toString();
                        String serwis = editTextSerwis.getText().toString();
                        String stawka = editTextStawka.getText().toString();
                        listener.applyText(day, month, year, czas_pracy, serwis, napiwki, stawka);
                    }
                });
        editTextDay = (EditText) view.findViewById(R.id.editTextDay);
        editTextMonth = (EditText) view.findViewById(R.id.editTextMonth);
        editTextYear = (EditText) view.findViewById(R.id.editTextYear);
        editTextCzasPracy = (EditText) view.findViewById(R.id.editTextCzasPracy);
        editTextNapiwki = (EditText) view.findViewById(R.id.editTextNapiwki);
        editTextSerwis = (EditText) view.findViewById(R.id.editTextSerwis);
        editTextStawka = (EditText) view.findViewById(R.id.editTextStawka);
        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (DialogMainActivityListener) context;
        } catch (ClassCastException e) {
            throw  new ClassCastException(context.toString()
                    + "must implement DialogMainActivityListener");
        }
    }

    public interface DialogMainActivityListener{
        void applyText(String day, String month, String year, String czas_pracy, String serwis, String napiwki, String stawka);
    }
}
