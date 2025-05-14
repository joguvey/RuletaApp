package com.example.ruletaapp;

public class HistorialItem {
    private int monedes;
    private String data;
    private double latitud;
    private double longitud;
    private String adreca;

    public HistorialItem(int monedes, String data, double latitud, double longitud, String adreca) {
        this.monedes = monedes;
        this.data = data;
        this.latitud = latitud;
        this.longitud = longitud;
        this.adreca = adreca;
    }

    public int getMonedes() {
        return monedes;
    }

    public String getData() {
        return data;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public String getAdreca() {
        return adreca;
    }
}
