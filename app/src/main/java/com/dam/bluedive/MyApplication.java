package com.dam.bluedive;

import android.app.Application;

public class MyApplication extends Application {
    private String nombre;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
