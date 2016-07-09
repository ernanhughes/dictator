package programmer.ie.dictator.view.visualizer;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import programmer.ie.dictator.R;
import programmer.ie.dictator.view.VisualizerView;
import programmer.ie.dictator.view.visualizer.renderer.BarGraphRenderer;
import programmer.ie.dictator.view.visualizer.renderer.CircleBarRenderer;
import programmer.ie.dictator.view.visualizer.renderer.CircleRenderer;
import programmer.ie.dictator.view.visualizer.renderer.LineRenderer;

/**
 * Created by Ernan on 27/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class VisualiserFactory {

    // Methods for adding renderers to visualizer
    public static void addBarGraphRenderers(Context context, VisualizerView visualizerView) {
        Paint paint = new Paint();
        paint.setStrokeWidth(50f);
        paint.setAntiAlias(true);
        paint.setColor(context.getResources().getColor(R.color.title_font_color));
        BarGraphRenderer barGraphRendererBottom = new BarGraphRenderer(16, paint, false);
        visualizerView.addRenderer(barGraphRendererBottom);

        Paint paint2 = new Paint();
        paint2.setStrokeWidth(12f);
        paint2.setAntiAlias(true);
        paint2.setColor(context.getResources().getColor(R.color.light_selected_color));
        BarGraphRenderer barGraphRendererTop = new BarGraphRenderer(4, paint2, true);
        visualizerView.addRenderer(barGraphRendererTop);
    }

    public static void addCircleBarRenderer(Context context, VisualizerView visualizerView) {
        Paint paint = new Paint();
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.LIGHTEN));
        paint.setColor(context.getResources().getColor(R.color.primary_color));
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
