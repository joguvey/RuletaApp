package com.example.ruletaapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MonedaDao {

    private final MonedaDatabaseHelper dbHelper;

    public MonedaDao(Context context) {
        dbHelper = new MonedaDatabaseHelper(context);
    }

    public void inserirPartida(int monedesFinals) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MonedaDatabaseHelper.COLUMN_MONEDES_FINALS, monedesFinals);

        // Format de la data
        String data = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        values.put(MonedaDatabaseHelper.COLUMN_DATA, data);

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

    public List<HistorialItem> getHistorial() {
        List<HistorialItem> historial = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT monedes_finals, data FROM historial", null);

        if (cursor.moveToFirst()) {
            do {
                int monedes = cursor.getInt(0);
                String data = cursor.getString(1);
                historial.add(new HistorialItem(monedes, data));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return historial;
    }
}
