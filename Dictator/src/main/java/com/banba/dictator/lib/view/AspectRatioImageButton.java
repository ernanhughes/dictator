package com.banba.dictator.lib.view;

/**
 * Created by Ernan on 13/02/14.
 * Copyrite Banba Inc. 2013.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class AspectRatioImageButton extends ImageButton {

    public AspectRatioImageButton(Context context) {
        super(context);
    }

    public AspectRatioImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AspectRatioImageButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = width * getDrawable().getIntrinsicHeight() / getDrawable().getIntrinsicWidth();
        setMeasuredDimension(width, height);
    }
}
