package com.example.ruletaapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.ruletaapp.MusicManager;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private RouletteView rouletteView;
    private float currentAngle = 0f;
    private final Random random = new Random();
    private ImageView monedaImage;
    private int monedes = 5;
    private TextView monedesText;
    private Button spinButton;
    private MediaPlayer mediaPlayerClick;
    private MediaPlayer mediaPlayerResultat;
    private static final int LIMIT_VICTORIA = 100;
    private final String[] sectors = {
            "+1", "-2", "*2", "/2", "+3", "-1", "*3", "-3"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Comprovem si podem accedir a la música personalitzada
        SharedPreferences prefsMusica = getSharedPreferences("configuracio_joc", MODE_PRIVATE);
        String musicaUri = prefsMusica.getString("musica_personalitzada", "");
        int volumMusica = prefsMusica.getInt("volum_musica", 100);
        float volum = volumMusica / 100f;
        Uri uri = null;

        if (musicaUri != null && !musicaUri.isEmpty()) {
            try {
                getContentResolver().openInputStream(Uri.parse(musicaUri)).close();  // Provem accés
                uri = Uri.parse(musicaUri);  // Si va bé, usem el fitxer personalitzat
            } catch (Exception e) {
                Log.w("MainActivity", "No es pot accedir a la música personalitzada, es fa servir loop3.");
            }
        }

        // Iniciem la música
        MusicManager.start(this, uri, R.raw.loop3, volum);

        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

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
                @Override public void onAnimationRepeat(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    float angleFinal = (360 - (newAngle % 360)) % 360f;
                    int sector = (int) (angleFinal / 45f);
                    String accio = sectors[sector];

                    monedes = aplicarAccio(monedes, accio);
                    monedesText.setText("Monedes: " + monedes);

                    float volumVfx = getSharedPreferences("audio_settings", MODE_PRIVATE).getInt("volum_vfx", 100) / 100f;

                    if (accio.contains("+") || accio.contains("*")) {
                        playResultSound(R.raw.monedaguanyada1, volumVfx);
                    } else if (accio.contains("-") || accio.contains("/")) {
                        playResultSound(R.raw.monedaperduda1, volumVfx);
                    }

                    Toast.makeText(MainActivity.this,
                            "Has tocat: " + accio + " → Monedes: " + monedes,
                            Toast.LENGTH_SHORT).show();

                    if (monedes >= LIMIT_VICTORIA) {
                        mostrarNotificacio("Has guanyat!!!", "El teu rècord és de " + monedes + " monedes 🏆");
                        monedaDao.inserirPartida(monedes);
                        spinButton.setEnabled(false);
                        spinButton.setAlpha(0.5f);
                        new Handler().postDelayed(() -> {
                            findViewById(R.id.menuLayout).setVisibility(View.VISIBLE);
                            monedesText.setVisibility(View.GONE);
                            spinButton.setVisibility(View.GONE);
                            findViewById(R.id.ruletaContainer).setVisibility(View.GONE);
                            findViewById(R.id.btnRetirar).setVisibility(View.GONE);
                            monedaImage.setVisibility(View.GONE);

                            monedes = 5;
                            monedesText.setText("Monedes: " + monedes);
                            spinButton.setEnabled(true);
                            spinButton.setAlpha(1f);
                        }, 2500);
                        afegirEsdevenimentCalendari();

                        Intent intent = new Intent(Intent.ACTION_INSERT);
                        intent.setData(CalendarContract.Events.CONTENT_URI);
                        intent.putExtra(CalendarContract.Events.TITLE, "Victòria a la Ruleta!");
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Has guanyat amb " + monedes + " monedes 🏆");
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "RuletaApp");
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + 60 * 60 * 1000);
                        startActivity(intent);
                    }

                    if (monedes <= 0) {
                        monedaDao.inserirPartida(monedes);
                        spinButton.setEnabled(false);
                        spinButton.setAlpha(0.5f);

                        mostrarNotificacio("Game Over", "T'has quedat sense monedes 💀");

                        new Handler().postDelayed(() -> {
                            findViewById(R.id.menuLayout).setVisibility(View.VISIBLE);
                            monedesText.setVisibility(View.GONE);
                            spinButton.setVisibility(View.GONE);
                            findViewById(R.id.ruletaContainer).setVisibility(View.GONE);
                            findViewById(R.id.btnRetirar).setVisibility(View.GONE);
                            monedaImage.setVisibility(View.GONE);

                            monedes = 5;
                            monedesText.setText("Monedes: " + monedes);
                            spinButton.setEnabled(true);
                            spinButton.setAlpha(1f);
                        }, 3000);
                    }

                    if (mediaPlayerClick != null) {
                        mediaPlayerClick.stop();
                        mediaPlayerClick.release();
                        mediaPlayerClick = null;
                    }
                }
            });

            rouletteView.startAnimation(rotate);
            currentAngle = newAngle % 360;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefsMusica = getSharedPreferences("configuracio_joc", MODE_PRIVATE);
        String musicaUri = prefsMusica.getString("musica_personalitzada", "");
        int volumMusica = prefsMusica.getInt("volum_musica", 100);
        float volum = volumMusica / 100f;
        Uri uri = musicaUri != null && !musicaUri.isEmpty() ? Uri.parse(musicaUri) : null;
        MusicManager.start(this, uri, R.raw.loop3, volum);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicManager.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayerClick != null) {
            mediaPlayerClick.stop();
            mediaPlayerClick.release();
        }
    }

    private void playResultSound(int soundResId, float volume) {
        if (mediaPlayerResultat != null) {
            mediaPlayerResultat.release();
            mediaPlayerResultat = null;
        }
        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(soundResId);
            if (afd != null) {
                mediaPlayerResultat = new MediaPlayer();
                mediaPlayerResultat.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                mediaPlayerResultat.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayerResultat.setLooping(false);
                mediaPlayerResultat.prepare();
                mediaPlayerResultat.setVolume(volume, volume);
                mediaPlayerResultat.start();
                mediaPlayerResultat.setOnCompletionListener(mp -> {
                    mp.release();
                    mediaPlayerResultat = null;
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarNotificacio(String titol, String missatge) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String canalId = "canal_ruleta";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    canalId, "Notificacions Ruleta", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(canal);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalId)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(titol)
                .setContentText(missatge)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private void afegirEsdevenimentCalendari() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "Victòria a Ruletilla!")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Has aconseguit el límit de monedes jugant.")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Ruletilla App")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + 60 * 60 * 1000);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No s'ha trobat cap aplicació de calendari.", Toast.LENGTH_SHORT).show();
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
}