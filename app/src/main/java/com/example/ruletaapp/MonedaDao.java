package com.example.ruletaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonedaDao {

    private final MonedaDatabaseHelper dbHelper;
    private final Context context;

    public MonedaDao(Context context) {
        this.context = context;
        dbHelper = new MonedaDatabaseHelper(context);
    }

    public void inserirPartida(int monedesFinals, double latitud, double longitud) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MonedaDatabaseHelper.COLUMN_MONEDES_FINALS, monedesFinals);

        String data = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        values.put(MonedaDatabaseHelper.COLUMN_DATA, data);
        values.put("latitud", latitud);
        values.put("longitud", longitud);

        // Obtenir adreça textual
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        String adreca = "Adreça desconeguda";
        try {
            List<Address> addresses = geocoder.getFromLocation(latitud, longitud, 1);
            if (addresses != null && !addresses.isEmpty()) {
                adreca = addresses.get(0).getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        values.put("adreca", adreca);

        db.insert(MonedaDatabaseHelper.TABLE_HISTORIAL, null, values);
        db.close();
    }

    public List<String> obtenirHistorial() {
        List<String> historial = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                MonedaDatabaseHelper.TABLE_HISTORIAL,
                null, null, null, null, null,
                MonedaDatabaseHelper.COLUMN_HISTORIAL_ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                int monedes = cursor.getInt(cursor.getColumnIndexOrThrow(MonedaDatabaseHelper.COLUMN_MONEDES_FINALS));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MonedaDatabaseHelper.COLUMN_DATA));
                historial.add(data + " - Monedes finals: " + monedes);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return historial;
    }

    public List<Puntuacio> getHistorial() {
        List<Puntuacio> historial = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + MonedaDatabaseHelper.TABLE_HISTORIAL, null);

        if (cursor.moveToFirst()) {
            do {
                // Email opcional
                String email = "";
                int emailIndex = cursor.getColumnIndex("email");
                if (emailIndex != -1) {
                    email = cursor.getString(emailIndex);
                }

                int monedes = cursor.getInt(cursor.getColumnIndexOrThrow(MonedaDatabaseHelper.COLUMN_MONEDES_FINALS));
                String data = cursor.getString(cursor.getColumnIndexOrThrow(MonedaDatabaseHelper.COLUMN_DATA));
                double latitud = cursor.getDouble(cursor.getColumnIndexOrThrow("latitud"));
                double longitud = cursor.getDouble(cursor.getColumnIndexOrThrow("longitud"));
                String adreca = cursor.getString(cursor.getColumnIndexOrThrow("adreca"));

                Puntuacio puntuacio = new Puntuacio(email, monedes, data, latitud, longitud, adreca);
                historial.add(puntuacio);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return historial;
    }
}