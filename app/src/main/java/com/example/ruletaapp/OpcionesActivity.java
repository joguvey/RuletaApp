package com.example.ruletaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ruletaapp.MusicManager;

public class OpcionesActivity extends AppCompatActivity {

    private SeekBar seekBarMusica;
    private TextView txtVolum;
    private SeekBar seekBarVfx;
    private TextView txtVolumVfx;

    private SharedPreferences prefsMusica;
    private SharedPreferences prefsVfx;

    private static final String PREFS_MUSICA = "configuracio_joc";
    private static final String PREFS_VFX = "audio_settings";
    private static final String CLAU_MUSICA = "musica_personalitzada";

    private final ActivityResultLauncher<String> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    // Guarda permís d'accés permanent
                    final int flags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
                    try {
                        getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    } catch (SecurityException e) {
                        Log.w("OpcionesActivity", "No s'ha pogut guardar permís persistable per la música");
                    }

                    prefsMusica.edit().putString(CLAU_MUSICA, uri.toString()).apply();
                    Toast.makeText(this, "Música seleccionada!", Toast.LENGTH_SHORT).show();

                    MusicManager.changeToCustomMusic(this, uri);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opciones);

        prefsMusica = getSharedPreferences(PREFS_MUSICA, MODE_PRIVATE);
        prefsVfx = getSharedPreferences(PREFS_VFX, MODE_PRIVATE);

        seekBarMusica = findViewById(R.id.seekBarMusica);
        txtVolum = findViewById(R.id.txtVolum);
        seekBarVfx = findViewById(R.id.seekBarVfx);
        txtVolumVfx = findViewById(R.id.txtVolumVfx);

        Button btnSeleccionarMusica = findViewById(R.id.btnSeleccionarMusica);
        Button btnTornar = findViewById(R.id.btnTornarOpcions);

        // Volum música
        int volumMusica = prefsMusica.getInt("volum_musica", 100);
        seekBarMusica.setMax(100);
        seekBarMusica.setProgress(volumMusica);
        txtVolum.setText("Volum música: " + volumMusica + "%");
        MusicManager.setVolume(volumMusica / 100f); // actualitza el volum al carregar

        seekBarMusica.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefsMusica.edit().putInt("volum_musica", progress).apply();
                MusicManager.setVolume(progress / 100f);
                txtVolum.setText("Volum música: " + progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Volum efectes sonors
        int volumVfx = prefsVfx.getInt("volum_vfx", 100);
        seekBarVfx.setMax(100);
        seekBarVfx.setProgress(volumVfx);
        txtVolumVfx.setText("Volum efectes sonors: " + volumVfx + "%");

        seekBarVfx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                prefsVfx.edit().putInt("volum_vfx", progress).apply();
                txtVolumVfx.setText("Volum efectes sonors: " + progress + "%");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Selector de música personalitzada
        btnSeleccionarMusica.setOnClickListener(v -> filePickerLauncher.launch("audio/*"));

        // Tornar al menú
        btnTornar.setOnClickListener(v -> finish());
    }
}