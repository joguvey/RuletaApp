package com.example.ruletaapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RouletteView rouletteView;
    private float currentAngle = 0f;
    private final Random random = new Random();
    private ImageView monedaImage;
    private int monedes = 5;
    private TextView monedesText;
    private Button spinButton;
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerClick;

    private final String[] sectors = {
            "+1", "-2", "*2", "/2", "+3", "-1", "*3", "-3"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MonedaDao monedaDao = new MonedaDao(this);

        rouletteView = findViewById(R.id.rouletteView);
        spinButton = findViewById(R.id.spinButton);
        monedesText = findViewById(R.id.monedesText);
        monedaImage = findViewById(R.id.monedaImage);
        monedesText.setText("Monedes: " + monedes);

        LinearLayout menuLayout = findViewById(R.id.menuLayout);
        Button btnJugar = findViewById(R.id.btnJugar);
        Button btnSalir = findViewById(R.id.btnSalir);
        Button btnRetirar = findViewById(R.id.btnRetirar);
        Button btnPuntuaciones = findViewById(R.id.btnPuntuaciones);
        Button btnOpciones = findViewById(R.id.btnOpciones);

        btnRetirar.setVisibility(View.GONE);
        monedaImage.setVisibility(View.GONE);

        btnPuntuaciones.setOnClickListener(v -> startActivity(new Intent(this, HistorialActivity.class)));

        btnJugar.setOnClickListener(v -> {
            menuLayout.setVisibility(View.GONE);
            monedesText.setVisibility(View.VISIBLE);
            spinButton.setVisibility(View.VISIBLE);
            findViewById(R.id.ruletaContainer).setVisibility(View.VISIBLE);
            btnRetirar.setVisibility(View.VISIBLE);
            monedaImage.setVisibility(View.VISIBLE);
        });

        btnRetirar.setOnClickListener(v -> {
            monedaDao.inserirPartida(monedes);
            menuLayout.setVisibility(View.VISIBLE);
            monedesText.setVisibility(View.GONE);
            spinButton.setVisibility(View.GONE);
            findViewById(R.id.ruletaContainer).setVisibility(View.GONE);
            btnRetirar.setVisibility(View.GONE);
            monedaImage.setVisibility(View.GONE);

            monedes = 5;
            monedesText.setText("Monedes: " + monedes);
            spinButton.setEnabled(true);
            spinButton.setAlpha(1f);
        });

        btnOpciones.setOnClickListener(v -> startActivity(new Intent(this, OpcionesActivity.class)));

        btnSalir.setOnClickListener(v -> finish());

        spinButton.setOnClickListener(v -> {
            int angle = 360 * (3 + random.nextInt(4)) + random.nextInt(360);
            float newAngle = currentAngle + angle;

            RotateAnimation rotate = new RotateAnimation(
                    currentAngle, newAngle,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );

            SharedPreferences prefsVfx = getSharedPreferences("audio_settings", MODE_PRIVATE);
            float volumVfx = prefsVfx.getInt("volum_vfx", 100) / 100f;

            try {
                if (mediaPlayerClick != null) {
                    mediaPlayerClick.release();
                    mediaPlayerClick = null;
                }
                AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.roulette_click);
                if (afd != null) {
                    mediaPlayerClick = new MediaPlayer();
                    mediaPlayerClick.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    afd.close();
                    mediaPlayerClick.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayerClick.setLooping(true);
                    mediaPlayerClick.prepare();
                    mediaPlayerClick.setVolume(volumVfx, volumVfx);
                    mediaPlayerClick.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            rotate.setDuration(3000);
            rotate.setFillAfter(true);

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override public void onAnimationStart(Animation animation) {}
                @Override
                public void onAnimationEnd(Animation animation) {
                    float angleFinal = (360 - (newAngle % 360)) % 360f;
                    int sector = (int) (angleFinal / 45f);
                    String accio = sectors[sector];

                    monedes = aplicarAccio(monedes, accio);
                    monedesText.setText("Monedes: " + monedes);
                    Toast.makeText(MainActivity.this,
                            "Has tocat: " + accio + " → Monedes: " + monedes,
                            Toast.LENGTH_SHORT).show();

                    if (monedes <= 0) {
                        monedaDao.inserirPartida(monedes);
                        spinButton.setEnabled(false);
                        spinButton.setAlpha(0.5f);
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Game Over")
                                .setMessage("T'has quedat sense monedes!")
                                .setCancelable(false)
                                .setPositiveButton("Sortir", (dialog, which) -> finish())
                                .show();
                    }

                    if (mediaPlayerClick != null) {
                        mediaPlayerClick.stop();
                        mediaPlayerClick.release();
                        mediaPlayerClick = null;
                    }
                }
                @Override public void onAnimationRepeat(Animation animation) {}
            });

            rouletteView.startAnimation(rotate);
            currentAngle = newAngle % 360;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        reproduirMusicaAmbVolum();  // Torna a llegir la configuració del volum
    }

    private void reproduirMusicaAmbVolum() {
        SharedPreferences prefsMusica = getSharedPreferences("configuracio_joc", MODE_PRIVATE);
        String musicaUri = prefsMusica.getString("musica_personalitzada", null);
        int volumMusica = prefsMusica.getInt("volum_musica", 100);
        float volumNormalitzat = volumMusica / 100f;

        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }

            if (musicaUri != null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(this, Uri.parse(musicaUri));
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
            } else {
                mediaPlayer = MediaPlayer.create(this, R.raw.loop3);
                mediaPlayer.setLooping(true);
            }

            mediaPlayer.setVolume(volumNormalitzat, volumNormalitzat);
            if (volumMusica > 0) mediaPlayer.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int aplicarAccio(int monedes, String accio) {
        try {
            if (accio.contains("+")) return monedes + Integer.parseInt(accio.substring(1));
            if (accio.contains("-")) return monedes - Integer.parseInt(accio.substring(1));
            if (accio.contains("*")) return monedes * Integer.parseInt(accio.substring(1));
            if (accio.contains("/")) return monedes / Integer.parseInt(accio.substring(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monedes;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        if (mediaPlayerClick != null) {
            mediaPlayerClick.stop();
            mediaPlayerClick.release();
        }
    }
}
