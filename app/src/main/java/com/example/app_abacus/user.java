package com.example.app_abacus;

public class user {
    private String Czas_pracy;
    private String Napiwki;
    private String Serwis;
    private String Stawka;

    public user() {
    }

    public user(String czas_pracy, String napiwki, String serwis, String stawka) {
        Czas_pracy = czas_pracy;
        Napiwki = napiwki;
        Serwis = serwis;
        Stawka = stawka;
    }

    public String getCzas_pracy() {
        return Czas_pracy;
    }

    public void setCzas_pracy(String czas_pracy) {
        Czas_pracy = czas_pracy;
    }

    public String getNapiwki() {
        return Napiwki;
    }

    public void setNapiwki(String napiwki) {
        Napiwki = napiwki;
    }

    public String getSerwis() {
        return Serwis;
    }

    public void setSerwis(String serwis) {
        Serwis = serwis;
    }

    public String getStawka() {
        return Stawka;
    }

    public void setStawka(String stawka) {
        Stawka = stawka;
    }
}
