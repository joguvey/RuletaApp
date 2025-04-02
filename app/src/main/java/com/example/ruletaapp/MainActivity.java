package com.example.ruletaapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import java.util.Random;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private RouletteView rouletteView;
    private float currentAngle = 0f;
    private final Random random = new Random();
    private ImageView monedaImage;
    private int monedes = 5;
    private TextView monedesText;
    private Button spinButton;
    private Button btnRetirar;

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
        Button btnPuntuaciones = findViewById(R.id.btnPuntuaciones); // ✅ Nou botó

        btnRetirar.setVisibility(View.GONE);
        monedaImage.setVisibility(View.GONE);

        btnPuntuaciones.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistorialActivity.class);
            startActivity(intent);
        });

        btnJugar.setOnClickListener(v -> {
            menuLayout.setVisibility(View.GONE);
            monedesText.setVisibility(View.VISIBLE);
            spinButton.setVisibility(View.VISIBLE);
            findViewById(R.id.ruletaContainer).setVisibility(View.VISIBLE);
            btnRetirar.setVisibility(View.VISIBLE);
            monedaImage.setVisibility(View.VISIBLE);
        });

        btnRetirar.setOnClickListener(v2 -> {
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

        btnSalir.setOnClickListener(v -> finish());

        spinButton.setOnClickListener(v -> {
            int angle = 360 * (3 + random.nextInt(4)) + random.nextInt(360);
            float newAngle = currentAngle + angle;

            RotateAnimation rotate = new RotateAnimation(
                    currentAngle, newAngle,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
            );

            rotate.setDuration(3000);
            rotate.setFillAfter(true);

            rotate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    float angleFinal = (360 - (newAngle % 360)) % 360f;
                    int sector = (int)(angleFinal / 45f);
                    Log.d("SECTOR", "Visual toca el sector: " + sector);

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
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });

            rouletteView.startAnimation(rotate);
            currentAngle = newAngle % 360;
        });
    }

    private int aplicarAccio(int monedes, String accio) {
        try {
            if (accio.contains("+"))
                return monedes + Integer.parseInt(accio.substring(1));
            if (accio.contains("-"))
                return monedes - Integer.parseInt(accio.substring(1));
            if (accio.contains("*"))
                return monedes * Integer.parseInt(accio.substring(1));
            if (accio.contains("/"))
                return monedes / Integer.parseInt(accio.substring(1));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return monedes;
    }
}