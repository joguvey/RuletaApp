package com.example.ruletaapp;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class TopMundialActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TopPuntuacionsAdapter adapter;
    private List<Puntuacio> llista = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_mundial);

        recycler = findViewById(R.id.recyclerTopMundial);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TopPuntuacionsAdapter(llista);
        recycler.setAdapter(adapter);

        carregarTopPuntuacions();
    }

    private void carregarTopPuntuacions() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("puntuacions")
                .orderBy("monedes", Query.Direction.DESCENDING)
                .limit(10)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    llista.clear();
                    for (var doc : querySnapshot.getDocuments()) {
                        Puntuacio p = doc.toObject(Puntuacio.class);
                        if (p != null) llista.add(p);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> Log.e("FIREBASE", "Error carregant top mundial", e));
    }
}
