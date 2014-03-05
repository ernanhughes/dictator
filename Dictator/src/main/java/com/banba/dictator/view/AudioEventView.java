/**
 * This file is part of Speech Trainer.
 * Copyright (C) 2011 Jan Wrobel <wrr@mixedbit.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.banba.dictator.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ImageButton;

import java.util.Stack;

/**
 * Draws a plot with sound levels of recently recorded or played buffers. The
 * data to be plotted is obtained from the AudioEventHistory.
 * <p/>
 * The creator of the AudioEventView must set AudioEventHistory.
 */
public class AudioEventView extends ImageButton {
    private static final int RECORDED_BUFFER_COLOR = 0xffd00000;
    private final Paint recordedBufferPaint;
    private static final int TEXT_COLOR = 0xff0000d0;
    private final Paint textPaint;

    public AudioEventView(Context context, AttributeSet attrs) {
        super(context, attrs);
        recordedBufferPaint = new Paint();
        recordedBufferPaint.setColor(RECORDED_BUFFER_COLOR);
        textPaint = new Paint();
        textPaint.setColor(TEXT_COLOR);
    }

    static final int STACK_SIZE = 100;

    Stack<Integer> readings = new Stack<Integer>();


    public void addReading(int reading) {
        if (readings.size() > STACK_SIZE) {
            readings.pop();
        }
        readings.push(reading);
        invalidate();
    }

    int findMin() {
        int min = 0;
        for (int r : readings) {
            if (r < min) {
                min = r;
            }
        }
        return min;
    }

    int max, min;

    int findMax() {
        int max = 0;
        for (int r : readings) {
            if (r > max) {
                max = r;
            }
        }
        return max;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int viewWidth = getWidth();
        final int viewHeight = getHeight();

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int) ((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

        final int usedWidth = viewWidth / 2;

        int size = readings.size();
        int barWidth = size == 0 ? 1 : usedWidth / size;

        for (int i = 0; i < usedWidth && i < size; ++i) {
            final int height = getHeightForSoundLevel(readings.get(i), viewHeight);
            final int lineStart = (viewHeight - height) / 2;
            canvas.drawText("Max:  " + max, xPos, yPos, textPaint);
            //public void drawRect(float left, float top, float right, float bottom, Paint paint) {

            int left = i * barWidth;
            int right = viewWidth - (i * barWidth);
            canvas.drawRect(left, lineStart, left + barWidth, lineStart + height, recordedBufferPaint);
            canvas.drawRect(right, lineStart, right + barWidth, lineStart + height, recordedBufferPaint);
        }
    }

    /**
     * Calculates height of a line to represent a given soundLevel. Makes sure
     * the lowest and the smallest recorded sound levels fit in the plot.
     */
    private int getHeightForSoundLevel(double soundLevel, int canvasHeight) {
        min = findMin();
        max = findMax();
        return (int) ((soundLevel - min) * (canvasHeight - 1)
                / (max - min));
    }
}