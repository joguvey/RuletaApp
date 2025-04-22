package com.example.ruletaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UbicacioDao extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ruleta.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_UBICACIO = "ubicacions";
    private static final String COL_ID = "id";
    private static final String COL_LATITUD = "latitud";
    private static final String COL_LONGITUD = "longitud";
    private static final String COL_TIMESTAMP = "timestamp";

    public UbicacioDao(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_UBICACIO + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LATITUD + " REAL, " +
                COL_LONGITUD + " REAL, " +
                COL_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UBICACIO);
        onCreate(db);
    }

    public void guardarUbicacio(double latitud, double longitud) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LATITUD, latitud);
        values.put(COL_LONGITUD, longitud);

        long result = db.insert(TABLE_UBICACIO, null, values);
        if (result == -1) {
            Log.e("UBICACIO", "Error guardant ubicació");
        } else {
            Log.d("UBICACIO", "Ubicació guardada: " + latitud + ", " + longitud);
        }
    }
}
