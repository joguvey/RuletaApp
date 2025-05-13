package com.example.ruletaapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioFocusRequest;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class SoundManager {
    private String lastMusicUri; // guarda l'última cançó reproduïda
    private static SoundManager instance;
    private MediaPlayer backgroundPlayer;
    private MediaPlayer clickPlayer;
    private MediaPlayer resultPlayer;
    private final Context context;

    private SoundManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static SoundManager getInstance(Context context) {
        if (instance == null) {
            instance = new SoundManager(context);
        }
        return instance;
    }

    public void playBackgroundMusic() {
        releaseBackground();  // Allibera abans

        SharedPreferences prefs = context.getSharedPreferences("configuracio_joc", Context.MODE_PRIVATE);
        String musicaUri = prefs.getString("musica_personalitzada", null);
        int volum = prefs.getInt("volum_musica", 100);
        float volumNormalitzat = volum / 100f;

        backgroundPlayer = new MediaPlayer();

        boolean carregaOK = false;

        // ✅ Primer intentem carregar música personalitzada
        if (musicaUri != null && !musicaUri.isEmpty()) {
            try {
                Uri uri = Uri.parse(musicaUri);
                backgroundPlayer.setDataSource(context, uri);
                lastMusicUri = musicaUri; // guardem només si ha funcionat
                Log.d("SoundManager", "Música personalitzada carregada correctament: " + musicaUri);
                carregaOK = true;
            } catch (Exception e) {
                Log.w("SoundManager", "Falla música personalitzada. Es carregarà la predeterminada.", e);
            }
        }

        // ✅ Si ha fallat, carreguem música predeterminada loop3.mp3
        if (!carregaOK) {
            try {
                AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.loop3);
                backgroundPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                afd.close();
                lastMusicUri = null;  // reiniciem referència
                Log.d("SoundManager", "Música predeterminada loop3.mp3 carregada.");
            } catch (Exception e2) {
                Log.e("SoundManager", "No es pot carregar cap música de fons.", e2);
                return;
            }
        }

        // ✅ Configuració comuna
        try {
            backgroundPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build()
            );

            backgroundPlayer.setLooping(true);
            backgroundPlayer.setVolume(volumNormalitzat, volumNormalitzat);
            backgroundPlayer.prepare();
            backgroundPlayer.start();

            Log.d("SoundManager", "Música de fons iniciada correctament.");

        } catch (Exception e) {
            Log.e("SoundManager", "Error iniciant la música de fons", e);
        }
    }

    public void pauseMusic() {
        if (backgroundPlayer != null && backgroundPlayer.isPlaying()) {
            backgroundPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (backgroundPlayer != null && !backgroundPlayer.isPlaying()) {
            backgroundPlayer.start();
        }
    }

    public void releaseBackground() {
        if (backgroundPlayer != null) {
            backgroundPlayer.release();
            backgroundPlayer = null;
        }
    }

    public void playClickSound() {
        releaseClick();
        try {
            SharedPreferences prefs = context.getSharedPreferences("audio_settings", Context.MODE_PRIVATE);
            float volumVfx = prefs.getInt("volum_vfx", 100) / 100f;

            AssetFileDescriptor afd = context.getResources().openRawResourceFd(R.raw.roulette_click);
            clickPlayer = new MediaPlayer();
            clickPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            clickPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .build()
            );
            clickPlayer.setLooping(true);
            clickPlayer.prepare();
            clickPlayer.setVolume(volumVfx, volumVfx);
            clickPlayer.start();
        } catch (Exception e) {
            Log.e("SoundManager", "Error click", e);
        }
    }

    public void stopClickSound() {
        if (clickPlayer != null && clickPlayer.isPlaying()) {
            clickPlayer.stop();
        }
    }

    public void playResultSound(int resId) {
        releaseResult();
        try {
            SharedPreferences prefs = context.getSharedPreferences("audio_settings", Context.MODE_PRIVATE);
            float volumVfx = prefs.getInt("volum_vfx", 100) / 100f;

            AssetFileDescriptor afd = context.getResources().openRawResourceFd(resId);
            resultPlayer = new MediaPlayer();
            resultPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            resultPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
            );
            resultPlayer.setLooping(false);
            resultPlayer.prepare();
            resultPlayer.setVolume(volumVfx, volumVfx);
            resultPlayer.start();

            resultPlayer.setOnCompletionListener(mp -> releaseResult());

        } catch (Exception e) {
            Log.e("SoundManager", "Error result", e);
        }
    }

    public void releaseClick() {
        if (clickPlayer != null) {
            clickPlayer.release();
            clickPlayer = null;
        }
    }

    public void releaseResult() {
        if (resultPlayer != null) {
            resultPlayer.release();
            resultPlayer = null;
        }
    }

    public void releaseAll() {
        releaseBackground();
        releaseClick();
        releaseResult();
    }
    public boolean isPlaying() {
        return backgroundPlayer != null && backgroundPlayer.isPlaying();
    }

    public String getLastMusicUri() {
        return lastMusicUri;
    }
    public void setVolume(float volumNormalitzat) {
        if (backgroundPlayer != null) {
            backgroundPlayer.setVolume(volumNormalitzat, volumNormalitzat);
        }
    }
}
