package com.example.ruletaapp;

public class HistorialItem {

    private int monedesFinals;
    private String data;

    public HistorialItem(int monedesFinals, String data) {
        this.monedesFinals = monedesFinals;
        this.data = data;
    }

    public int getMonedesFinals() {
        return monedesFinals;
    }

    public String getData() {
        return data;
    }
}
