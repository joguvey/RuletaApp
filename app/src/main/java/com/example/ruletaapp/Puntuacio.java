package com.example.ruletaapp;

public class Puntuacio {
    private String email;
    private int monedes;
    private long timestamp;

    public Puntuacio() {} // Per Firebase

    public Puntuacio(String email, int monedes, long timestamp) {
        this.email = email;
        this.monedes = monedes;
        this.timestamp = timestamp;
    }

    public String getEmail() { return email; }
    public int getMonedes() { return monedes; }
    public long getTimestamp() { return timestamp; }
}