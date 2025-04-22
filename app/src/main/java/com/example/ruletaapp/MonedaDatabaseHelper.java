package com.example.ruletaapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MonedaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ruletilla.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_MONEDES = "monedes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_QUANTITAT = "quantitat";
    public static final String TABLE_HISTORIAL = "historial";
    public static final String COLUMN_HISTORIAL_ID = "_idhist";
    public static final String COLUMN_MONEDES_FINALS = "monedes_finals";
    public static final String COLUMN_DATA = "data";


    private static final String DATABASE_CREATE =
            "CREATE TABLE " + TABLE_MONEDES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    COLUMN_QUANTITAT + " INTEGER);";

    public MonedaDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static final String HISTORIAL_CREATE =
            "CREATE TABLE " + TABLE_HISTORIAL + " (" +
                    COLUMN_HISTORIAL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_MONEDES_FINALS + " INTEGER, " +
                    COLUMN_DATA + " TEXT, " +
                    "latitud REAL, " +
                    "longitud REAL);";



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
        db.execSQL("INSERT INTO " + TABLE_MONEDES + " VALUES (1, 5)");

        db.execSQL(HISTORIAL_CREATE);

        String CREATE_UBICACIONS_TABLE = "CREATE TABLE ubicacions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "latitud REAL," +
                "longitud REAL," +
                "timestamp LONG)";
        db.execSQL(CREATE_UBICACIONS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONEDES);
        onCreate(db);
    }
}
