package com.example.ruletaapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TopMundialActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TopPuntuacionsAdapter adapter;
    private List<Puntuacio> topPuntuacions = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_mundial);

        recyclerView = findViewById(R.id.recyclerTopMundial);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TopPuntuacionsAdapter(topPuntuacions);
        recyclerView.setAdapter(adapter);

        Button btnTornar = findViewById(R.id.btnTornarTop);
        btnTornar.setOnClickListener(v -> finish());

        db = FirebaseFirestore.getInstance();

        carregarTopPuntuacions();
    }

    private void carregarTopPuntuacions() {
        db.collection("puntuacions")
                .orderBy("monedes", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    topPuntuacions.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String email = doc.getString("email");
                        long monedes = doc.getLong("monedes");
                        long timestamp = doc.getLong("timestamp");
                        topPuntuacions.add(new Puntuacio(email, (int) monedes, timestamp));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error carregant puntuacions", Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE", "Error", e);
                });
    }
}