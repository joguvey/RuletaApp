package com.example.ruletaapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MonedaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ruletilla.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_MONEDES = "monedes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUANTITAT = "quantitat";
    public static final String TABLE_HISTORIAL = "historial";
    public static final String COLUMN_HISTORIAL_ID = "_idhist";
    public static final String COLUMN_MONEDES_FINALS = "monedes_finals";
    public static final String COLUMN_DATA = "data";

    private static final String DATABASE_CREATE =
            "CREATE TABLE IF NOT EXISTS " + TABLE_MONEDES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_QUANTITAT + " INTEGER);";

    public MonedaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static final String HISTORIAL_CREATE = "CREATE TABLE historial (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "monedes_finals INTEGER, " +
            "data TEXT, " +
            "latitud REAL, " +
            "longitud REAL, " +
            "adreca TEXT)";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL("INSERT OR IGNORE INTO " + TABLE_MONEDES + " VALUES (1, 5)");

        db.execSQL(HISTORIAL_CREATE);

        String CREATE_UBICACIONS_TABLE = "CREATE TABLE IF NOT EXISTS ubicacions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "latitud REAL," +
                "longitud REAL," +
                "timestamp LONG)";
        db.execSQL(CREATE_UBICACIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE historial ADD COLUMN latitud REAL DEFAULT 0");
            db.execSQL("ALTER TABLE historial ADD COLUMN longitud REAL DEFAULT 0");
            db.execSQL("ALTER TABLE historial ADD COLUMN adreca TEXT DEFAULT 'Adreça desconeguda'");
        }
    }
}
