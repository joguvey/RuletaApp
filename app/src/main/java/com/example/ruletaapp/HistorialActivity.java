package com.example.ruletaapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.example.ruletaapp.Puntuacio;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private RecyclerView recyclerHistorial;
    private HistorialAdapter adapter;
    private MonedaDao monedaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        Button btnTornar = findViewById(R.id.btnTornar);
        btnTornar.setOnClickListener(v -> finish());

        // 🔥 AFEGIT: BOTÓ TOP MUNDIAL
        Button btnTopMundial = findViewById(R.id.btnTopMundial);
        btnTopMundial.setOnClickListener(v -> {
            Intent intent = new Intent(HistorialActivity.this, TopMundialActivity.class);
            startActivity(intent);
        });

        monedaDao = new MonedaDao(this);

        List<Puntuacio> historial = monedaDao.getHistorial();

        adapter = new HistorialAdapter(historial);
        recyclerHistorial.setAdapter(adapter);
    }
}
