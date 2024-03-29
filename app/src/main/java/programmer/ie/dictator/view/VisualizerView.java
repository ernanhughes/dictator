/**
 * Copyright 2011, Felix Palmer
 * <p/>
 * Licensed under the MIT license:
 * http://creativecommons.org/licenses/MIT/
 */
package programmer.ie.dictator.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashSet;
import java.util.Set;

import programmer.ie.dictator.util.L;
import programmer.ie.dictator.view.visualizer.AudioData;
import programmer.ie.dictator.view.visualizer.FFTData;
import programmer.ie.dictator.view.visualizer.renderer.Renderer;

/**
 * A class that draws visualizations of data received from a
 * {@link android.media.audiofx.Visualizer.OnDataCaptureListener#onWaveFormDataCapture } and
 * {@link android.media.audiofx.Visualizer.OnDataCaptureListener#onFftDataCapture }
 */
public class VisualizerView extends View {
    private static final String TAG = "VisualizerView";
    AudioData mAudioData = null;
    FFTData mFftData = null;
    Matrix mMatrix = new Matrix();
    boolean mFlash = false;
    Bitmap mCanvasBitmap;
    Canvas mCanvas;
    private Rect mRect = new Rect();
    private Visualizer mVisualizer;
    private Set<Renderer> mRenderers;
    private Paint mFlashPaint = new Paint();
    private Paint mFadePaint = new Paint();

    public VisualizerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs);
        init();
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisualizerView(Context context) {
        this(context, null, 0);
    }

    private void init() {
        mFlashPaint.setColor(Color.argb(122, 255, 255, 255));
        mFadePaint.setColor(Color.argb(238, 255, 255, 255)); // Adjust alpha to change how quickly the image fades
        mFadePaint.setXfermode(new PorterDuffXfermode(Mode.MULTIPLY));

        mRenderers = new HashSet<Renderer>();
    }

    /**
     * Links the visualizer to a player
     *
     * @param player - MediaPlayer instance to link to
     */
    public void link(MediaPlayer player) {
        if (player != null) {
            // Create the Visualizer object and attach it to our media player.
            try {

                mVisualizer = new Visualizer(player.getAudioSessionId());
                mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

                // Pass through Visualizer data to VisualizerView
                Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
                    @Override
                    public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                                      int samplingRate) {
                        updateVisualizer(bytes);
                    }

                    @Override
                    public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                                 int samplingRate) {
                        updateVisualizerFFT(bytes);
                    }
                };

                mVisualizer.setDataCaptureListener(captureListener,
                        Visualizer.getMaxCaptureRate() / 2, true, true);

                // Enabled Visualizer and disable when we're done with the stream
                mVisualizer.setEnabled(true);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        //    mVisualizer.setEnabled(false);
                    }
                });
            } catch (Exception e) {
                L.e(e.getMessage());
            }
        }
    }

    public void addRenderer(Renderer renderer) {
        if (renderer != null) {
            mRenderers.add(renderer);
        }
    }

    public void clearRenderers() {
        mRenderers.clear();
    }

    /**
     * Call to release the resources used by VisualizerView. Like with the
     * MediaPlayer it is good practice to call this method
     */
    public void release() {
        if (null != mVisualizer) {
            mVisualizer.release();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        release();
        super.onDetachedFromWindow();
    }

    /**
     * Pass data to the visualizer. Typically this will be obtained from the
     * Android Visualizer.OnDataCaptureListener call back. See
     * {@link android.media.audiofx.Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
     *
     * @param bytes
     */
    public void updateVisualizer(byte[] bytes) {
        if (bytes != null) {
            mAudioData = new AudioData(bytes);
        }
        invalidate();
    }

    /**
     * Pass FFT data to the visualizer. Typically this will be obtained from the
     * Android Visualizer.OnDataCaptureListener call back. See
     * {@link android.media.audiofx.Visualizer.OnDataCaptureListener#onFftDataCapture }
     *
     * @param bytes
     */
    public void updateVisualizerFFT(byte[] bytes) {
        if (bytes != null) {
            mFftData = new FFTData(bytes);
        }

        invalidate();
    }

    /**
     * Call this to make the visualizer flash. Useful for flashing at the start
     * of a song/loop etc...
     */
    public void flash() {
        mFlash = true;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Create canvas once we're ready to draw
        mRect.set(0, 0, getWidth(), getHeight());

        if (mCanvasBitmap == null) {
            mCanvasBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
        }
        if (mCanvas == null) {
            mCanvas = new Canvas(mCanvasBitmap);
        }

        if (mAudioData != null) {
            for (Renderer r : mRenderers) {
                r.render(mCanvas, mAudioData, mRect);
            }
        }

        if (mFftData != null) {
            for (Renderer r : mRenderers) {
                r.render(mCanvas, mFftData, mRect);
            }
        }

        // Fade out old contents
        mCanvas.drawPaint(mFadePaint);
        if (mFlash) {
            mFlash = false;
            mCanvas.drawPaint(mFlashPaint);
        }
        canvas.drawBitmap(mCanvasBitmap, mMatrix, null);
    }
}