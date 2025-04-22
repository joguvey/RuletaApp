package com.example.ruletaapp;

public class UbicacioItem {
    private double latitud;
    private double longitud;
    private long timestamp;

    public UbicacioItem(double latitud, double longitud, long timestamp) {
        this.latitud = latitud;
        this.longitud = longitud;
        this.timestamp = timestamp;
    }

    public double getLatitud() { return latitud; }
    public double getLongitud() { return longitud; }
    public long getTimestamp() { return timestamp; }
}
