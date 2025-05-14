package com.example.ruletaapp;

public class Puntuacio {
    private String email;
    private long monedes;
    private long timestamp;

    public Puntuacio() {
        // Constructor buit necessari per Firebase
    }

    public Puntuacio(String email, long monedes, long timestamp) {
        this.email = email;
        this.monedes = monedes;
        this.timestamp = timestamp;
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
