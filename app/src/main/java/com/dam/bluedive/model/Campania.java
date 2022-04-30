package com.dam.bluedive.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Campania implements Parcelable {
    private String nombre;
    private String objetivo;
    private String fecha;
    private String lugar;
    private String urlFoto;

    public Campania() {
    }

    public Campania(String nombre, String objetivo, String fecha, String lugar, String urlFoto) {
        this.nombre = nombre;
        this.objetivo = objetivo;
        this.fecha = fecha;
        this.lugar = lugar;
        this.urlFoto = urlFoto;
    }

    protected Campania(Parcel in) {
        nombre = in.readString();
        objetivo = in.readString();
        fecha = in.readString();
        lugar = in.readString();
        urlFoto = in.readString();
    }

    public static final Creator<Campania> CREATOR = new Creator<Campania>() {
        @Override
        public Campania createFromParcel(Parcel in) {
            return new Campania(in);
        }

        @Override
        public Campania[] newArray(int size) {
            return new Campania[size];
        }
    };

    public String getNombre() {
        return nombre;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public String getFecha() {
        return fecha;
    }

    public String getLugar() {
        return lugar;
    }

    public String getUrlFoto() {
        return urlFoto;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(nombre);
        parcel.writeString(objetivo);
        parcel.writeString(fecha);
        parcel.writeString(lugar);
        parcel.writeString(urlFoto);
    }
}