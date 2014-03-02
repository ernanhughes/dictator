package com.banba.dictator.lib;

import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.widget.ImageView;

/**
 * Created by Ernan on 07/02/14.
 * Copyrite Banba Inc. 2013.
 */
public class ColorUtil {

    public static void applyTempColorFilter(final ImageView iv, int iColor) {
        iv.setColorFilter(getFilter(iColor));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (iv != null) {
                    iv.setColorFilter(null);
                }
            }
        }, 100);
    }

    public static void applyTempColorFilter(final Drawable iv, int iColor) {
        iv.setColorFilter(getFilter(iColor));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (iv != null) {
                    iv.setColorFilter(null);
                }
            }
        }, 100);
    }

    static float[] colorMatrixNegative = {
            -1.0f, 0, 0, 0, 255, //red
            0, -1.0f, 0, 0, 255, //green
            0, 0, -1.0f, 0, 255, //blue
            0, 0, 0, 1.0f, 0 //alpha
    };

    public static void applyTempNegativeColorFilter(final Drawable iv) {
        ColorFilter colorFilter = new ColorMatrixColorFilter(colorMatrixNegative);
        iv.setColorFilter(colorFilter);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (iv != null) {
                    iv.setColorFilter(null);
                }
            }
        }, 100);
    }


    public static ColorMatrixColorFilter getFilter(int iColor) {
        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;
        return new ColorMatrixColorFilter(new float[]{
                0, 0, 0, 0, red
                , 0, 0, 0, 0, green
                , 0, 0, 0, 0, blue
                , 0, 0, 0, 1, 0});
    }

    public static ColorMatrixColorFilter getTranslateFilter(float dr, float dg,
                                                            float db, float da) {
        return new ColorMatrixColorFilter(new float[]{
                2, 0, 0, 0, dr,
                0, 2, 0, 0, dg,
                0, 0, 2, 0, db,
                0, 0, 0, 1, da});
    }

    public static ColorMatrixColorFilter getContrastFilter(float contrast) {
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        return new ColorMatrixColorFilter(new float[]{
                scale, 0, 0, 0, translate,
                0, scale, 0, 0, translate,
                0, 0, scale, 0, translate,
                0, 0, 0, 1, 0});
    }

    public static ColorMatrixColorFilter getContrastTranslateOnlyFilter(float contrast) {
        float scale = contrast + 1.f;
        float translate = (-.5f * scale + .5f) * 255.f;
        return new ColorMatrixColorFilter(new float[]{
                1, 0, 0, 0, translate,
                0, 1, 0, 0, translate,
                0, 0, 1, 0, translate,
                0, 0, 0, 1, 0});
    }

    public static ColorMatrixColorFilter getContrastScaleOnlyFilter(float contrast) {
        float scale = contrast + 1.f;
        return new ColorMatrixColorFilter(new float[]{
                scale, 0, 0, 0, 0,
                0, scale, 0, 0, 0,
                0, 0, scale, 0, 0,
                0, 0, 0, 1, 0});
    }
}
