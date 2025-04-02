package com.example.ruletaapp;

import android.os.Bundle;
import android.widget.Button;

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

        monedaDao = new MonedaDao(this);

        List<HistorialItem> historial = monedaDao.getHistorial();

        adapter = new HistorialAdapter(historial);
        recyclerHistorial.setAdapter(adapter);
    }
}
