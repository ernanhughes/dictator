package com.banba.dictator.visualizer;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import com.banba.dictator.visualizer.renderer.BarGraphRenderer;
import com.banba.dictator.visualizer.renderer.CircleBarRenderer;
import com.banba.dictator.visualizer.renderer.CircleRenderer;
import com.banba.dictator.visualizer.renderer.LineRenderer;

/**
 * Created by Ernan on 27/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class VisualiserFactory {

    // Methods for adding renderers to visualizer
    public static void addBarGraphRenderers(VisualizerView visualizerView) {
        Paint paint = new Paint();
        paint.setStrokeWidth(50f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(200, 56, 138, 252));
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
        visualizerView.addRenderer(barGraphRendererBottom);

        Paint paint2 = new Paint();
        paint2.setStrokeWidth(12f);
        paint2.setAntiAlias(true);
        paint2.setColor(Color.argb(200, 181, 111, 233));
        BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
        visualizerView.addRenderer(barGraphRendererTop);
    }

    public static void addCircleBarRenderer(VisualizerView visualizerView) {
        Paint paint = new Paint();
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        paint.setColor(Color.argb(255, 222, 92, 143));
        CircleBarRenderer circleBarRenderer = new CircleBarRenderer(paint, 32, true);
        visualizerView.addRenderer(circleBarRenderer);
    }

    public static void addCircleRenderer(VisualizerView visualizerView) {
        Paint paint = new Paint();
        paint.setStrokeWidth(3f);
        paint.setAntiAlias(true);
        paint.setColor(Color.argb(255, 222, 92, 143));
        CircleRenderer circleRenderer = new CircleRenderer(paint, true);
        visualizerView.addRenderer(circleRenderer);
    }

    public static void addLineRenderer(VisualizerView visualizerView) {
        Paint linePaint = new Paint();
        linePaint.setStrokeWidth(1f);
        linePaint.setAntiAlias(true);
        linePaint.setColor(Color.argb(88, 0, 128, 255));

        Paint lineFlashPaint = new Paint();
        lineFlashPaint.setStrokeWidth(5f);
        lineFlashPaint.setAntiAlias(true);
        lineFlashPaint.setColor(Color.argb(188, 255, 255, 255));
        LineRenderer lineRenderer = new LineRenderer(linePaint, lineFlashPaint, true);
        visualizerView.addRenderer(lineRenderer);
    }
}