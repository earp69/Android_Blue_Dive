package com.dam.bluedive.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Evento implements Parcelable {

    private String nombre;
    private String objetivo;
    private String fecha;
    private String lugar;
    private String urlFoto;

    public Evento(){}

    public Evento(String nombre, String objetivo, String fecha, String lugar, String urlFoto) {
        this.nombre = nombre;
        this.objetivo = objetivo;
        this.fecha = fecha;
        this.lugar = lugar;
        this.urlFoto = urlFoto;
    }

    protected Evento(Parcel in) {
        nombre = in.readString();
        objetivo = in.readString();
        fecha = in.readString();
        lugar = in.readString();
        urlFoto = in.readString();
    }

    public static final Creator<Evento> CREATOR = new Creator<Evento>() {
        @Override
        public Evento createFromParcel(Parcel in) {
            return new Evento(in);
        }

        @Override
        public Evento[] newArray(int size) {
            return new Evento[size];
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
