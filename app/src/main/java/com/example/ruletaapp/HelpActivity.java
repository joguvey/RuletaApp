package com.example.ruletaapp;

import android.os.Bundle;
import android.webkit.WebView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);  // fa serevir el layout amb el WebView

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);

        // espai permetre que el botó HTML tanqui l'activitat
        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void tornarAlMenu() {
                runOnUiThread(() -> finish());
            }
        }, "Android");

        // detecta l'idioma del sistema i carrega l'arxiu corresponent
        String idioma = Locale.getDefault().getLanguage();
        if (idioma.equals("en")) {
            webView.loadUrl("file:///android_asset/ajuda_en.html");
        } else if (idioma.equals("es")) {
            webView.loadUrl("file:///android_asset/ajuda_es.html");
        } else {
            webView.loadUrl("file:///android_asset/ajuda.html"); // català per defecte
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        SoundManager.getInstance(this).pauseMusic();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // No reactivem la música aquí per evitar duplicació
    }
}