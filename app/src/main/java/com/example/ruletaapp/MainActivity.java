package com.example.ruletaapp;
import static androidx.core.content.ContextCompat.getSystemService;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
//import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;
//importem notificacions
import android.app.NotificationChannel;
import android.app.NotificationManager;
import androidx.core.app.NotificationCompat;
//aquest import permet fer automaticament fora a l'usuari cap al menu
import android.os.Handler;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;


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
    private MediaPlayer mediaPlayerResultat; //reproductor per sons de resultat (guanyar o perdre)
    private static final int LIMIT_VICTORIA = 6;
    private final String[] sectors = {
            //"*1", "*2", "*2", "*2", "*3", "*1", "*3", "*3" //valors ruleta per guanyar rapid
            //"-1", "-2", "/2", "/2", "-3", "-1", "-3", "-3" //valors ruleta per perdre rapid
            "+1", "-2", "*2", "/2", "+3", "-1", "*3", "-3"  // valors normals
    };
    private static final int REQUEST_LOCATION_PERMISSION = 123;
    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // aixo sempre ha d'anar primer

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            obtenirUbicacioActual(() -> {});
        }

        //aqui ens assegurem que depenent la versio
        //del mobil usuari, demanem permis per fer que surti NOTIFICACIO

        setContentView(R.layout.activity_main);
        reproduirMusicaAmbVolum();
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
            obtenirUbicacioActual(() -> monedaDao.inserirPartida(monedes));
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

                    //cridem el so segons el que surti, la funcio esta fora al main
                    SharedPreferences prefsVfx = getSharedPreferences("audio_settings", MODE_PRIVATE);
                    float volumVfx = prefsVfx.getInt("volum_vfx", 100) / 100f;

                    if (accio.contains("+") || accio.contains("*")) {
                        playResultSound(R.raw.monedaguanyada1, volumVfx);
                    } else if (accio.contains("-") || accio.contains("/")) {
                        playResultSound(R.raw.monedaperduda1, volumVfx);
                    }


                    //text del que ha surt de les monedes
                    Toast.makeText(MainActivity.this,
                            "Has tocat: " + accio + " → Monedes: " + monedes,
                            Toast.LENGTH_SHORT).show();

                    if (monedes >= LIMIT_VICTORIA) {
                        obtenirUbicacioActual(() -> monedaDao.inserirPartida(monedes));
                        mostrarNotificacio("Has guanyat!!!", "El teu rècord és de " + monedes + " monedes 🏆");
                        monedaDao.inserirPartida(monedes);
                        spinButton.setEnabled(false);
                        spinButton.setAlpha(0.5f);
                        // aixo torna al menu principal
                        new Handler().postDelayed(() -> {
                            // No tornem a inserir la partida aquí
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
                        //crida func afegir a calendari
                        afegirEsdevenimentCalendari();
                        //msg que afegeix a l'usuari
                        Intent intent = new Intent(Intent.ACTION_INSERT);
                        intent.setData(CalendarContract.Events.CONTENT_URI);
                        intent.putExtra(CalendarContract.Events.TITLE, "Victòria a la Ruleta!");
                        intent.putExtra(CalendarContract.Events.DESCRIPTION, "Has guanyat amb " + monedes + " monedes 🏆");
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "RuletaApp");
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + 60 * 60 * 1000); // 1 hora
                        startActivity(intent);

                    }

                    if (monedes <= 0) {
                        obtenirUbicacioActual(() -> monedaDao.inserirPartida(monedes));
                        monedaDao.inserirPartida(monedes);
                        spinButton.setEnabled(false);
                        spinButton.setAlpha(0.5f);

                        mostrarNotificacio("Game Over", "T'has quedat sense monedes 💀");

                        // tornem al menu 3 segons (per donar temps a veure el resultat)
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
                @Override public void onAnimationRepeat(Animation animation) {}
            });

            rouletteView.startAnimation(rotate);
            currentAngle = newAngle % 360;
        });
    }
    private void obtenirUbicacioActual(Runnable despresDeGuardar) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    double latitud = location.getLatitude();
                    double longitud = location.getLongitude();

                    Log.d("UBICACIO", "Lat: " + latitud + ", Lon: " + longitud);

                    new UbicacioDao(this).guardarUbicacio(latitud, longitud);

                } else {
                    Log.w("UBICACIO", "No s'ha pogut obtenir la ubicació");
                }

                // 🟢 Executa el codi que va després (guardar la partida, etc.)
                if (despresDeGuardar != null) {
                    despresDeGuardar.run();
                }
            });

        } else {
            Log.w("UBICACIO", "Permís no concedit");
            if (despresDeGuardar != null) {
                despresDeGuardar.run();
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permís de localització concedit ✅", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permís de localització denegat ❌", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        reproduirMusicaAmbVolum();  // Torna a llegir la configuració del volum
    }

    //reproduccio del so
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

    //notificacions
    private void mostrarNotificacio(String titol, String missatge) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String canalId = "canal_ruleta";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(
                    canalId,
                    "Notificacions Ruleta",
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(canal);
        }

        // Intent per tornar al menú (MainActivity)
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canalId)
                .setSmallIcon(R.drawable.ic_launcher_foreground) //  es pot posar icona
                .setContentTitle(titol)
                .setContentText(missatge)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent); // Aquí!

        notificationManager.notify(new Random().nextInt(), builder.build());
    }

    private void reproduirMusicaAmbVolum() {
        SharedPreferences prefsMusica = getSharedPreferences("configuracio_joc", MODE_PRIVATE);
        String musicaUri = prefsMusica.getString("musica_personalitzada", null);
        int volumMusica = prefsMusica.getInt("volum_musica", 100);
        float volumNormalitzat = volumMusica / 100f;

        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
                mediaPlayer = null;
            }

            // Focus d'àudio
            AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
            int result = audioManager.requestAudioFocus(focusChange -> {}, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                Log.e("Música", "No s'ha pogut obtenir focus d'àudio.");
                return;
            }

            if (musicaUri != null && !musicaUri.isEmpty()) {
                try {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(this, Uri.parse(musicaUri));
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepare();
                    Log.d("Música", "Música personalitzada carregada.");
                } catch (Exception e) {
                    Log.e("Música", "Error amb la música personalitzada. Carregant loop3.mp3");
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                    mediaPlayer = MediaPlayer.create(this, R.raw.loop3);
                    mediaPlayer.setLooping(true);
                }
            } else {
                Log.d("Música", "No hi ha música personalitzada. Carregant loop3.mp3");
                mediaPlayer = MediaPlayer.create(this, R.raw.loop3);
                mediaPlayer.setLooping(true);
            }

            if (mediaPlayer != null && volumMusica > 0) {
                mediaPlayer.setVolume(volumNormalitzat, volumNormalitzat);
                mediaPlayer.start();
                Log.d("Música", "Reproducció iniciada.");
            }

        } catch (Exception e) {
            Log.e("Música", "Error carregant música: " + e.getMessage());
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
    private void afegirEsdevenimentCalendari() {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(android.provider.CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, "Victòria a Ruletilla!")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Has aconseguit el límit de monedes jugant.")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "Ruletilla App")
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, System.currentTimeMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, System.currentTimeMillis() + 60 * 60 * 1000); // 1 hora

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No s'ha trobat cap aplicació de calendari.", Toast.LENGTH_SHORT).show();
        }
    }
    private void guardarUbicacio(double latitud, double longitud) {
        MonedaDatabaseHelper dbHelper = new MonedaDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("latitud", latitud);
        values.put("longitud", longitud);
        values.put("timestamp", System.currentTimeMillis());

        long newRowId = db.insert("ubicacions", null, values);
        if (newRowId != -1) {
            Log.d("UBICACIO", "Ubicació guardada amb ID: " + newRowId);
        } else {
            Log.e("UBICACIO", "Error en guardar la ubicació");
        }

        db.close();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
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
