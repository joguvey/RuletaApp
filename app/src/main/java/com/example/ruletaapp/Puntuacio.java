package com.example.ruletaapp;

public class Puntuacio {
    private String email;
    private long monedes;
    private long timestamp;
    private String data;
    private double latitud;
    private double longitud;
    private String adreca;
    public String getData() { return data; }
    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public String getAdreca() { return adreca; }

    public Puntuacio() {
        // Constructor buit necessari per Firebase
    }

    public Puntuacio(String email, int monedes, String data, double latitud, double longitud, String adreca) {
        this.email = email;
        this.monedes = monedes;
        this.data = data;
        this.latitud = latitud;
        this.longitud = longitud;
        this.adreca = adreca;
    }

    public String getEmail() {
        return email;
    }

    public long getMonedes() {
        return monedes;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
