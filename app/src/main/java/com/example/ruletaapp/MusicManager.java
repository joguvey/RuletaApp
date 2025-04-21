package com.example.ruletaapp;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

public class MusicManager {
    private static MediaPlayer mediaPlayer;
    private static boolean isPrepared = false;
    private static float volum = 1.0f;

    public static void start(Context context, Uri uri, int defaultResId, float volume) {
        stop(); // Evitem duplicats
        volum = volume;

        try {
            if (uri != null) {
                try {
                    // Intentem accedir a la música personalitzada
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(context, uri);
                    mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.prepare();
                    mediaPlayer.setVolume(volum, volum);
                    mediaPlayer.start();
                    isPrepared = true;
                    Log.d("MusicManager", "Música personalitzada carregada.");
                    return;
                } catch (Exception e) {
                    Log.w("MusicManager", "No es pot accedir a la música personalitzada, es fa servir loop3.");
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                        mediaPlayer = null;
                    }
                }
            }

            // Si no hi ha música personalitzada o falla, fem servir la per defecte
            mediaPlayer = MediaPlayer.create(context, defaultResId);
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.setVolume(volum, volum);
                mediaPlayer.start();
                isPrepared = true;
                Log.d("MusicManager", "Música per defecte (loop3) carregada.");
            } else {
                Log.e("MusicManager", "Error carregant la música per defecte.");
            }

        } catch (Exception e) {
            Log.e("MusicManager", "Error iniciant la música", e);
        }
    }

    public static void changeToCustomMusic(Context context, Uri uri) {
        start(context, uri, R.raw.loop3, volum);  // sempre tornem a intentar amb loop3 si falla
    }

    public static void setVolume(float nouVolum) {
        volum = nouVolum;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(volum, volum);
            Log.d("MusicManager", "Volum ajustat a: " + volum);
        }
    }

    public static void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    public static void resume() {
        if (mediaPlayer != null && isPrepared && !mediaPlayer.isPlaying()) mediaPlayer.start();
    }

    public static void stop() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
            isPrepared = false;
        }
    }

    public static boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }
}