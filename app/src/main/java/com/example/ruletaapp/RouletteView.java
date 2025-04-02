package com.example.ruletaapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class RouletteView extends View {

    private Paint sectorPaint;
    private Paint textPaint;
    private RectF rectF;


    private final String[] sectors = {
            "+1", "-2", "*2", "/2", "+3", "-1", "*3", "-3"
    };



    private final int[] colors = {
            Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW,
            Color.MAGENTA, Color.CYAN, Color.LTGRAY, Color.DKGRAY
    };

    public RouletteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        sectorPaint = new Paint();
        sectorPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(60);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);

        rectF = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2f;
        float centerY = getHeight() / 2f;
        float radius = Math.min(centerX, centerY) - 20;

        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);

        float anglePerSector = 360f / sectors.length;

        for (int i = 0; i < sectors.length; i++) {
            sectorPaint.setColor(colors[i % colors.length]);
            canvas.drawArc(rectF, i * anglePerSector - 90, anglePerSector, true, sectorPaint);

            float angle = (i + 0.5f) * anglePerSector - 90;
            float textX = (float) (centerX + radius * 0.6 * Math.cos(Math.toRadians(angle)));
            float textY = (float) (centerY + radius * 0.6 * Math.sin(Math.toRadians(angle))) + 20;

            canvas.drawText(sectors[i], textX, textY, textPaint);
        }
    }
}
