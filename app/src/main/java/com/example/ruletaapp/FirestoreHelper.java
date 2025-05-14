package com.example.ruletaapp;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirestoreHelper {

    private final Context context;

    public FirestoreHelper(Context context) {
        this.context = context;
    }

    public void desarPuntuacio(String email, int monedes) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> puntuacio = new HashMap<>();
        puntuacio.put("email", email);
        puntuacio.put("monedes", monedes);
        puntuacio.put("timestamp", System.currentTimeMillis());

        db.collection("puntuacions")
                .add(puntuacio)
                .addOnSuccessListener(docRef -> {
                    Log.d("FirestoreHelper", "✅ Puntuació guardada: " + monedes + " monedes per " + email);
                    Toast.makeText(context, "Puntuació guardada al núvol!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("FirestoreHelper", "❌ Error guardant puntuació", e);
                    Toast.makeText(context, "Error guardant puntuació", Toast.LENGTH_LONG).show();
                });
    }
}