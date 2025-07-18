package br.edu.ifsuldeminas.mch.codefacil;

import android.app.Application;

import com.google.firebase.FirebaseApp;

public class CodeFacilApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
    }
}